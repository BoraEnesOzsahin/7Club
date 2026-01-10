package com.example.a7club.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class EventSignup(
    @DocumentId val id: String = "",
    val eventId: String = "",
    val studentId: String = "",
    val timestamp: Timestamp? = null // System.currentTimeMillis yerine Firebase Timestamp
)