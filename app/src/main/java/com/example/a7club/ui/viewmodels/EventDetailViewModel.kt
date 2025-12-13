package com.example.a7club.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.data.models.EventSignup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EventDetailViewModel : ViewModel() {

    // TODO: Add state management for UI (e.g., loading, success, error)

    fun signUpForEvent(eventId: String, studentId: String) {
        viewModelScope.launch {
            val signup = EventSignup(
                eventId = eventId,
                studentId = studentId,
                timestamp = System.currentTimeMillis()
            )

            try {
                Firebase.firestore.collection("eventSignups")
                    .add(signup)
                    .await()
                // TODO: Handle success: show a snackbar or update UI state
                println("Signup successful!")
            } catch (e: Exception) {
                // TODO: Handle error: show a snackbar or update UI state
                println("Signup failed: ${e.message}")
            }
        }
    }
}
