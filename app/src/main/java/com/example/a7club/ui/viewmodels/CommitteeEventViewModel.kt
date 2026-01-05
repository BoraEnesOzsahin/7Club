package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.a7club.model.VehicleRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommitteeEventViewModel : ViewModel() {

    var vehicleType by mutableStateOf("")
    var pickupLocation by mutableStateOf("")
    var dropoffLocation by mutableStateOf("")
    var passengerCount by mutableStateOf("")
    var notes by mutableStateOf("")

    fun submitVehicleRequest(eventName: String, clubId: String) {
        // 1. İsme göre Etkinlik ID'sini bul
        Firebase.firestore.collection("events")
            .whereEqualTo("title", eventName)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val realEventId = if (!snapshot.isEmpty) snapshot.documents[0].id else "unknown_event"

                // 2. Talebi Kaydet
                val request = VehicleRequest(
                    eventId = realEventId,
                    eventName = eventName,
                    clubName = "YUKEK Kulübü", // Dinamik yapılabilir
                    vehicleType = vehicleType,
                    pickupLocation = pickupLocation,
                    destination = dropoffLocation,
                    passengerCount = passengerCount.toIntOrNull() ?: 0,
                    notes = notes,
                    requestDate = Timestamp.now(),
                    status = "PENDING"
                )

                Firebase.firestore.collection("vehicleRequests").add(request)
            }
            .addOnFailureListener {
                println("Etkinlik bulunamadı hatası: ${it.message}")
            }
    }
}