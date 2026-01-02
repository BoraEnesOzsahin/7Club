package com.example.a7club.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Event(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",

    // --- İŞTE EKSİK OLAN KISIM BURASIYDI ---
    val eventTime: String = "",      // Bunu eklemezsen hata alırsın
    // ---------------------------------------

    val contactPhone: String = "",
    val location: String = "",
    val clubId: String = "",
    val clubName: String = "",
    val timestamp: Timestamp? = null,
    val status: String = "PENDING",
    val category: String = "General",
    val formUrl: String = ""
)