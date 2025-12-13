package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.data.models.Event
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreateEventViewModel : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var clubName by mutableStateOf("")
    var date by mutableStateOf("") // For simplicity, using String for now

    fun createEvent(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (title.isBlank() || description.isBlank() || clubName.isBlank()) {
            onError("Tüm alanlar doldurulmalıdır.")
            return
        }

        viewModelScope.launch {
            val newEvent = Event(
                title = title,
                description = description,
                clubName = clubName,
                // The 'date' parameter was incorrect, changed to 'startTime'
                startTime = System.currentTimeMillis()
            )

            try {
                Firebase.firestore.collection("events").add(newEvent).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Bilinmeyen bir hata oluştu.")
            }
        }
    }
}
