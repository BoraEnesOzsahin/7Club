package com.example.a7club.model

import com.google.firebase.Timestamp

data class VehicleRequest(
    val id: String = "",
    val eventId: String = "",
    val clubId: String = "",
    val vehicleType: String = "",      // Örn: Otobüs
    val pickupLocation: String = "",   // Nereden
    val destination: String = "",      // Nereye
    val passengerCount: Int = 0,       // Kaç Kişi
    val notes: String = "",            // Notlar
    val requestDate: Timestamp? = null
)