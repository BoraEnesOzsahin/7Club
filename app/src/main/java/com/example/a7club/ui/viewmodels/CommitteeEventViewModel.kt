package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.a7club.data.models.VehicleRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommitteeEventViewModel : ViewModel() {

    var vehicleType by mutableStateOf("")
    var pickupLocation by mutableStateOf("")
    var dropoffLocation by mutableStateOf("")
    var passengerCount by mutableStateOf("")
    var notes by mutableStateOf("")

    fun submitVehicleRequest(eventId: String, clubId: String) {
        val request = VehicleRequest(
            eventId = eventId,
            clubId = clubId,
            vehicleType = vehicleType,
            pickupLocation = pickupLocation,
            dropoffLocation = dropoffLocation,
            // TODO: Implement proper time picker
            pickupTime = System.currentTimeMillis(),
            passengerCount = passengerCount.toIntOrNull() ?: 0,
            notes = notes
        )

        // TODO: Add error handling and success feedback
        Firebase.firestore.collection("vehicleRequests").add(request)
    }
}
