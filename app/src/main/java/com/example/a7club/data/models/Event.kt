package com.example.a7club.data.models

import com.google.firebase.firestore.DocumentId

/**
 * Represents an event created by a club.
 * This data class is Firestore-compliant.
 */
data class Event(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val clubId: String = "",
    val clubName: String = "",
    val location: String = "",
    val startTime: Long = 0L,
    val capacity: Int? = null,
    val status: EventStatus = EventStatus.PENDING
)

enum class EventStatus {
    PENDING,
    APPROVED,
    REJECTED
}
