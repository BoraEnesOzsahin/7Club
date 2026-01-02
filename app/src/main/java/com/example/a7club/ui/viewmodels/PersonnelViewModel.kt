package com.example.a7club.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.a7club.model.Club
import com.example.a7club.model.Event
import com.example.a7club.model.Student
import com.example.a7club.model.VehicleRequest // Eklendi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PersonnelViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _pendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val pendingEvents: StateFlow<List<Event>> = _pendingEvents

    private val _pastEvents = MutableStateFlow<List<Event>>(emptyList())
    val pastEvents: StateFlow<List<Event>> = _pastEvents

    private val _clubs = MutableStateFlow<List<Club>>(emptyList())
    val clubs: StateFlow<List<Club>> = _clubs

    private val _currentClubMembers = MutableStateFlow<List<Student>>(emptyList())
    val currentClubMembers: StateFlow<List<Student>> = _currentClubMembers

    private val _currentClubEvents = MutableStateFlow<List<Event>>(emptyList())
    val currentClubEvents: StateFlow<List<Event>> = _currentClubEvents

    private val _isLoadingMembers = MutableStateFlow(false)
    val isLoadingMembers: StateFlow<Boolean> = _isLoadingMembers

    // --- YENİ EKLENEN KISIM: Araç Talebi State'i ---
    private val _currentVehicleRequest = MutableStateFlow<VehicleRequest?>(null)
    val currentVehicleRequest: StateFlow<VehicleRequest?> = _currentVehicleRequest
    // -----------------------------------------------

    init {
        try {
            fetchPendingEvents()
            fetchPastEvents()
            fetchClubs()
        } catch (e: Exception) {
            Log.e("PersonnelVM", "Init Error: ${e.message}")
        }
    }

    private fun fetchPendingEvents() {
        db.collection("events")
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val events = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Event::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                _pendingEvents.value = events
            }
    }

    private fun fetchPastEvents() {
        db.collection("events")
            .whereIn("status", listOf("APPROVED", "REJECTED"))
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val events = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Event::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                _pastEvents.value = events
            }
    }

    private fun fetchClubs() {
        db.collection("clubs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val clubList = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Club::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                _clubs.value = clubList
            }
    }

    fun fetchClubEvents(clubName: String, isPast: Boolean) {
        db.collection("events")
            .whereEqualTo("clubName", clubName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                val now = com.google.firebase.Timestamp.now()
                val filteredEvents = if (isPast) {
                    events.filter { it.timestamp != null && it.timestamp.seconds < now.seconds }
                } else {
                    events.filter { it.timestamp != null && it.timestamp.seconds >= now.seconds }
                }
                _currentClubEvents.value = filteredEvents
            }
    }

    fun fetchClubMembers(clubName: String) {
        _isLoadingMembers.value = true
        db.collection("users")
            .whereEqualTo("role", "STUDENT")
            .whereArrayContains("enrolledClubs", clubName)
            .get()
            .addOnSuccessListener { snapshot ->
                _isLoadingMembers.value = false
                val members = snapshot.documents.mapNotNull { doc -> doc.toObject(Student::class.java) }
                _currentClubMembers.value = members
            }
            .addOnFailureListener {
                _isLoadingMembers.value = false
            }
    }

    // --- YENİ EKLENEN FONKSİYON: Araç Talebini Getir ---
    fun fetchVehicleRequest(eventId: String) {
        _currentVehicleRequest.value = null // Eskiyi temizle
        db.collection("vehicleRequests")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    // İlk bulunan isteği al
                    val doc = snapshot.documents[0]
                    _currentVehicleRequest.value = doc.toObject(VehicleRequest::class.java)
                } else {
                    _currentVehicleRequest.value = null
                }
            }
            .addOnFailureListener {
                _currentVehicleRequest.value = null
            }
    }
    // ----------------------------------------------------

    fun verifyEvent(eventId: String, onSuccess: () -> Unit) {
        db.collection("events").document(eventId)
            .update("status", "APPROVED")
            .addOnSuccessListener { onSuccess() }
    }

    fun rejectEvent(eventId: String, onSuccess: () -> Unit) {
        db.collection("events").document(eventId)
            .update("status", "REJECTED")
            .addOnSuccessListener { onSuccess() }
    }
}