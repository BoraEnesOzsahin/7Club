package com.example.a7club.ui.viewmodels

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
        // Firestore'da "events" koleksiyonu altında status == "Pending" olanları çeker
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
}
