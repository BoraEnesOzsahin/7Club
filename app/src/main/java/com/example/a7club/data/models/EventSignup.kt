package com.example.a7club.data.models

import com.google.firebase.firestore.DocumentId

/**
 * Represents a student's sign-up for an event.
 * This data class is Firestore-compliant.
 */
data class EventSignup(
    @DocumentId val id: String = "",
    val eventId: String = "",
    val studentId: String = "",
    val timestamp: Long = 0L
)
