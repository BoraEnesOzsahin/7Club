package com.example.a7club.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.a7club.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PersonnelViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    
    private val _pendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val pendingEvents: StateFlow<List<Event>> = _pendingEvents

    init {
        fetchPendingEvents()
    }

    private fun fetchPendingEvents() {
        db.collection("events")
            .whereEqualTo("status", "Pending")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                _pendingEvents.value = events
            }
    }

    // Etkinliği onaylama fonksiyonu
    fun verifyEvent(eventId: String, onSuccess: () -> Unit) {
        db.collection("events").document(eventId)
            .update("status", "Verified")
            .addOnSuccessListener {
                Log.d("PersonnelVM", "Etkinlik onaylandı: $eventId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("PersonnelVM", "Onaylama hatası: ${e.message}")
            }
    }

    // Etkinliği reddetme fonksiyonu
    fun rejectEvent(eventId: String, onSuccess: () -> Unit) {
        db.collection("events").document(eventId)
            .update("status", "Rejected")
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun addTestData() {
        val testEvents = listOf(
            Event(title = "Yaratıcı Yazarlık Atölyesi", clubName = "Kültür ve Etkinlik Kulübü", status = "Pending", location = "Yeditepe Üniversitesi Kampüsü", time = "06.00", contactPhone = "0526 254 2598"),
            Event(title = "Çalışma Maçı", clubName = "Münazara Kulübü", status = "Pending", location = "Hazırlık Binası", time = "14.00", contactPhone = "0530 000 0000")
        )

        val batch = db.batch()
        testEvents.forEach { event ->
            val docRef = db.collection("events").document()
            batch.set(docRef, event)
        }
        batch.commit()
    }
}
