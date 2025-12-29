package com.example.a7club.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.a7club.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Club(
    val id: String = "",
    val name: String = "",
    val logoUrl: String = "",
    val description: String = ""
)

data class Member(
    val id: String = "",
    val studentNo: String = "",
    val fullName: String = ""
)

class PersonnelViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    
    // TÜM Bekleyen Talepler (Hata almamak için geri eklendi)
    private val _pendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val pendingEvents: StateFlow<List<Event>> = _pendingEvents

    // Yeni Talepler (Son 2 gün)
    private val _recentPendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val recentPendingEvents: StateFlow<List<Event>> = _recentPendingEvents

    // Gecikmiş Talepler (2 günden eski)
    private val _overduePendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val overduePendingEvents: StateFlow<List<Event>> = _overduePendingEvents

    private val _pastEvents = MutableStateFlow<List<Event>>(emptyList())
    val pastEvents: StateFlow<List<Event>> = _pastEvents

    private val _clubs = MutableStateFlow<List<Club>>(emptyList())
    val clubs: StateFlow<List<Club>> = _clubs

    private val _currentClubMembers = MutableStateFlow<List<Member>>(emptyList())
    val currentClubMembers: StateFlow<List<Member>> = _currentClubMembers

    private val _currentClubEvents = MutableStateFlow<List<Event>>(emptyList())
    val currentClubEvents: StateFlow<List<Event>> = _currentClubEvents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMembers = MutableStateFlow(false)
    val isLoadingMembers: StateFlow<Boolean> = _isLoadingMembers

    init {
        fetchPendingEvents()
        fetchPastEvents()
        fetchClubs()
    }

    private fun fetchPendingEvents() {
        val twoDaysAgo = Timestamp(Timestamp.now().seconds - (2 * 24 * 60 * 60), 0)

        db.collection("events")
            .whereEqualTo("status", "Pending")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val allPending = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                // Tümünü güncelle
                _pendingEvents.value = allPending

                // Yeni Talepler
                _recentPendingEvents.value = allPending.filter { 
                    it.timestamp != null && it.timestamp!! >= twoDaysAgo 
                }

                // Gecikmiş Talepler
                _overduePendingEvents.value = allPending.filter { 
                    it.timestamp != null && it.timestamp!! < twoDaysAgo 
                }
            }
    }

    private fun fetchPastEvents() {
        db.collection("events")
            .whereIn("status", listOf("Verified", "Rejected"))
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _pastEvents.value = events
            }
    }

    private fun fetchClubs() {
        db.collection("clubs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val clubs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Club::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _clubs.value = clubs
            }
    }

    fun fetchClubEvents(clubName: String, isPast: Boolean) {
        _isLoading.value = true
        val now = Timestamp.now()
        var query = db.collection("events").whereEqualTo("clubName", clubName)
        
        if (isPast) {
            query = query.whereLessThan("timestamp", now)
        } else {
            query = query.whereGreaterThanOrEqualTo("timestamp", now)
        }

        query.addSnapshotListener { snapshot, error ->
            _isLoading.value = false
            if (error != null) return@addSnapshotListener
            val events = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Event::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            _currentClubEvents.value = events
        }
    }

    fun fetchClubMembers(clubName: String) {
        _isLoadingMembers.value = true
        db.collection("users")
            .whereArrayContains("enrolledClubs", clubName)
            .addSnapshotListener { snapshot, error ->
                _isLoadingMembers.value = false
                if (error != null) return@addSnapshotListener
                val members = snapshot?.documents?.mapNotNull { doc ->
                    Member(
                        id = doc.id,
                        studentNo = doc.getString("studentNo") ?: "000000000",
                        fullName = doc.getString("fullName") ?: "İsimsiz Üye"
                    )
                } ?: emptyList()
                _currentClubMembers.value = members
            }
    }

    fun verifyEvent(eventId: String, onSuccess: () -> Unit) {
        db.collection("events").document(eventId).update("status", "Verified").addOnSuccessListener { onSuccess() }
    }

    fun rejectEvent(eventId: String, onSuccess: () -> Unit) {
        db.collection("events").document(eventId).update("status", "Rejected").addOnSuccessListener { onSuccess() }
    }
}
