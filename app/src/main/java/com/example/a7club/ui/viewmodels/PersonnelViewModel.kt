package com.example.a7club.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.a7club.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PersonnelViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    
    private val _pendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val pendingEvents: StateFlow<List<Event>> = _pendingEvents

    private val _pastEvents = MutableStateFlow<List<Event>>(emptyList())
    val pastEvents: StateFlow<List<Event>> = _pastEvents

    init {
        addTestData() 
        fetchPendingEvents()
        fetchPastEvents()
    }

    private fun fetchPendingEvents() {
        // Sadece 'Pending' olan VE zamanı GELECEKTE olanlar
        db.collection("events")
            .whereEqualTo("status", "Pending")
            .whereGreaterThan("timestamp", Timestamp.now())
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _pendingEvents.value = events
            }
    }

    private fun fetchPastEvents() {
        // Zamanı GEÇMİŞ olan tüm etkinlikler
        db.collection("events")
            .whereLessThan("timestamp", Timestamp.now())
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PersonnelVM", "Past events error: ${error.message}")
                    return@addSnapshotListener
                }
                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _pastEvents.value = events
            }
    }

    fun verifyEvent(eventId: String, onSuccess: () -> Unit) {
        db.collection("events").document(eventId).update("status", "Verified").addOnSuccessListener { onSuccess() }
    }

    fun rejectEvent(eventId: String, onSuccess: () -> Unit) {
        db.collection("events").document(eventId).update("status", "Rejected").addOnSuccessListener { onSuccess() }
    }

    fun addTestData() {
        db.collection("events").get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            
            val now = Timestamp.now().seconds
            val dayInSeconds = 86400L

            val mixedEvents = listOf(
                // --- GELECEKTEKİ TALEPLER (Talepler sayfasında çıkacak) ---
                Event(
                    title = "Gelecek Hafta Konseri", 
                    clubName = "Müzik Kulübü", 
                    status = "Pending", 
                    location = "Amfi", 
                    time = "20:00",
                    contactPhone = "0555 111 22 33",
                    timestamp = Timestamp(now + (dayInSeconds * 7), 0)
                ),
                Event(
                    title = "Yarınki Kariyer Günü", 
                    clubName = "İşletme Kulübü", 
                    status = "Pending", 
                    location = "Rektörlük", 
                    time = "10:00",
                    contactPhone = "0555 999 88 77",
                    timestamp = Timestamp(now + dayInSeconds, 0)
                ),

                // --- GEÇMİŞ ETKİNLİKLER (Geçmiş kısmında çıkacak) ---
                Event(
                    title = "Dünkü Onaylı Gezi", 
                    clubName = "Kültür Kulübü", 
                    status = "Verified", 
                    location = "Eminönü", 
                    timestamp = Timestamp(now - dayInSeconds, 0)
                ),
                Event(
                    title = "Reddedilmiş Eski Talep", 
                    clubName = "Spor Kulübü", 
                    status = "Rejected", 
                    location = "Spor Salonu", 
                    timestamp = Timestamp(now - (dayInSeconds * 3), 0)
                ),
                Event(
                    title = "Zamanı Geçmiş Bekleyen", 
                    clubName = "Bilişim Kulübü", 
                    status = "Pending", 
                    location = "Lab 1", 
                    timestamp = Timestamp(now - 7200, 0) // 2 saat önce
                )
            )

            mixedEvents.forEach { event ->
                val docRef = db.collection("events").document()
                batch.set(docRef, event)
            }
            batch.commit()
        }
    }
}
