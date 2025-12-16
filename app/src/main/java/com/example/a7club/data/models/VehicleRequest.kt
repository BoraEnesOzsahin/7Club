package com.example.a7club.data.models

import com.google.firebase.firestore.DocumentId

/**
 * Represents a vehicle request for an event.
 * This data class is Firestore-compliant.
 */
data class VehicleRequest(
    @DocumentId val id: String = "",
    val eventId: String = "",
    val clubId: String = "",
    val vehicleType: String = "",
    val pickupLocation: String = "",
    val dropoffLocation: String = "",
    val pickupTime: Long = 0L,
    val passengerCount: Int = 0,
    val notes: String = "",
    val status: RequestStatus = RequestStatus.PENDING
)

enum class RequestStatus {
    PENDING,
    APPROVED,
    REJECTED
}
