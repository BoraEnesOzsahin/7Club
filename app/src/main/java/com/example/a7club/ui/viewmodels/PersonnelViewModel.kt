package com.example.a7club.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.model.Club
import com.example.a7club.model.Event
import com.example.a7club.model.User
import com.example.a7club.model.VehicleRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PersonnelViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // --- STATE FLOWS (Veri Akışları) ---

    // 1. Ana Sayfa ve Talepler İçin
    private val _recentPendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val recentPendingEvents: StateFlow<List<Event>> = _recentPendingEvents.asStateFlow()

    private val _overduePendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val overduePendingEvents: StateFlow<List<Event>> = _overduePendingEvents.asStateFlow()

    private val _pastEvents = MutableStateFlow<List<Event>>(emptyList())
    val pastEvents: StateFlow<List<Event>> = _pastEvents.asStateFlow()

    // --- YENİ EKLENEN: Takvim için TÜM etkinlikler ---
    private val _allEventsForCalendar = MutableStateFlow<List<Event>>(emptyList())
    val allEventsForCalendar: StateFlow<List<Event>> = _allEventsForCalendar.asStateFlow()

    private val _clubs = MutableStateFlow<List<Club>>(emptyList())
    val clubs: StateFlow<List<Club>> = _clubs.asStateFlow()

    private val _selectedVehicleRequest = MutableStateFlow<VehicleRequest?>(null)
    val selectedVehicleRequest: StateFlow<VehicleRequest?> = _selectedVehicleRequest.asStateFlow()

    // 2. Kulüp Detay Sayfaları İçin
    private val _currentClubEvents = MutableStateFlow<List<Event>>(emptyList())
    val currentClubEvents: StateFlow<List<Event>> = _currentClubEvents.asStateFlow()

    private val _currentClubMembers = MutableStateFlow<List<User>>(emptyList())
    val currentClubMembers: StateFlow<List<User>> = _currentClubMembers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchEvents()
        fetchClubs()
        fetchAllEventsForCalendar() // YENİ: Başlangıçta tümünü çek
    }

    // YENİ FONKSİYON: Takvim için tüm etkinlikleri getirir
    private fun fetchAllEventsForCalendar() {
        viewModelScope.launch {
            try {
                // Tarihe göre sıralı hepsini çek (Status farketmeksizin)
                val snapshot = db.collection("events")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .await()

                val events = snapshot.toObjects(Event::class.java)
                _allEventsForCalendar.value = events

            } catch (e: Exception) {
                Log.e("PersonnelViewModel", "Takvim verisi çekilemedi: ${e.message}")
            }
        }
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            try {
                // BEKLEYENLER (PENDING)
                db.collection("events")
                    .whereEqualTo("status", "PENDING")
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) return@addSnapshotListener

                        val allPending = snapshot?.toObjects(Event::class.java) ?: emptyList()
                        val now = Timestamp.now()

                        _recentPendingEvents.value = allPending.filter {
                            val ts = it.timestamp
                            ts != null && ts >= now
                        }

                        _overduePendingEvents.value = allPending.filter {
                            val ts = it.timestamp
                            ts != null && ts < now
                        }
                    }

                // GEÇMİŞ / ONAYLI
                db.collection("events")
                    .whereIn("status", listOf("APPROVED", "REJECTED"))
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) return@addSnapshotListener
                        _pastEvents.value = snapshot?.toObjects(Event::class.java) ?: emptyList()
                    }

            } catch (e: Exception) {
                Log.e("PersonnelViewModel", "Veri çekme hatası: ${e.message}")
            }
        }
    }

    private fun fetchClubs() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("clubs").get().await()
                _clubs.value = snapshot.toObjects(Club::class.java)
            } catch (e: Exception) {
                Log.e("PersonnelViewModel", "Kulüp hatası: ${e.message}")
            }
        }
    }

    // --- FONKSİYONLAR ---

    // 1. Araç Talebi
    fun loadVehicleRequest(eventId: String) {
        viewModelScope.launch {
            _selectedVehicleRequest.value = null
            try {
                val snapshot = db.collection("vehicleRequests")
                    .whereEqualTo("eventId", eventId)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    _selectedVehicleRequest.value = snapshot.documents[0].toObject(VehicleRequest::class.java)
                }
            } catch (e: Exception) {
                Log.e("PersonnelViewModel", "Araç talep hatası: ${e.message}")
            }
        }
    }

    // 2. Onaylama / Reddetme
    fun approveEvent(event: Event, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val batch = db.batch()
                val eventRef = db.collection("events").document(event.id)
                batch.update(eventRef, "status", "APPROVED")

                val vehicleSnapshot = db.collection("vehicleRequests").whereEqualTo("eventId", event.id).get().await()
                for (doc in vehicleSnapshot) {
                    batch.update(doc.reference, "status", "APPROVED")
                }
                batch.commit().await()
                // Verileri yenile
                fetchAllEventsForCalendar()
                onSuccess()
            } catch (e: Exception) {
                Log.e("PersonnelViewModel", "Onay hatası: ${e.message}")
            }
        }
    }

    fun rejectEvent(event: Event, reason: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val batch = db.batch()
                val eventRef = db.collection("events").document(event.id)
                batch.update(eventRef, "status", "REJECTED")

                val vehicleSnapshot = db.collection("vehicleRequests").whereEqualTo("eventId", event.id).get().await()
                for (doc in vehicleSnapshot) {
                    batch.update(doc.reference, "status", "REJECTED")
                }
                batch.commit().await()
                // Verileri yenile
                fetchAllEventsForCalendar()
                onSuccess()
            } catch (e: Exception) {
                Log.e("PersonnelViewModel", "Ret hatası: ${e.message}")
            }
        }
    }

    fun fetchClubEvents(clubName: String, isPast: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val query = db.collection("events")
                    .whereEqualTo("clubName", clubName)

                val snapshot = query.get().await()
                val allEvents = snapshot.toObjects(Event::class.java)
                val now = Timestamp.now()

                _currentClubEvents.value = if (isPast) {
                    allEvents.filter {
                        val ts = it.timestamp
                        ts != null && ts < now
                    }
                } else {
                    allEvents.filter {
                        val ts = it.timestamp
                        ts != null && ts >= now
                    }
                }
            } catch (e: Exception) {
                Log.e("PersonnelViewModel", "Kulüp etkinlikleri hatası: ${e.message}")
                _currentClubEvents.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchClubMembers(clubName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val clubSnapshot = db.collection("clubs").whereEqualTo("name", clubName).get().await()
                if (!clubSnapshot.isEmpty) {
                    val clubId = clubSnapshot.documents[0].getString("id") ?: ""

                    if (clubId.isNotEmpty()) {
                        val usersSnapshot = db.collection("users")
                            .whereArrayContains("enrolledClubs", clubId)
                            .get()
                            .await()
                        _currentClubMembers.value = usersSnapshot.toObjects(User::class.java)
                    }
                }
            } catch (e: Exception) {
                Log.e("PersonnelViewModel", "Üye çekme hatası: ${e.message}")
                _currentClubMembers.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}