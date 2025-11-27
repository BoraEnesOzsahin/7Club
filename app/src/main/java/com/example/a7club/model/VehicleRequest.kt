package com.example.a7club.model

import com.google.firebase.Timestamp

// Dosya: model/VehicleRequest.kt

data class VehicleRequest(
    val id: String = "",
    val clubId: String = "",
    val eventId: String = "",         // Hangi etkinliğe araç lazım?

    val destination: String = "",     // Gidilecek yer
    val passengerCount: Int = 0,
    val requestDate: Timestamp? = null,

    // Onay Durumu
    val status: RequestStatus = RequestStatus.PENDING,
    val approvedBy: String? = null    // Onaylayan personelin UID'si
)