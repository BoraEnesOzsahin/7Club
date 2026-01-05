package com.example.a7club.model

import com.google.firebase.Timestamp

data class VehicleRequest(
    val id: String = "",
    val eventId: String = "",       // Hangi etkinliğe bağlı?
    val eventName: String = "",     // Personel ekranında kolay okunsun diye
    val clubName: String = "",
    val vehicleType: String = "",
    val pickupLocation: String = "",
    val destination: String = "",
    val passengerCount: Int = 0,
    val notes: String = "",
    val requestDate: Timestamp? = null,
    val status: String = "PENDING"
)