package com.example.a7club.model

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val location: String = "",
    val time: String = "",
    val date: String = "", // "2024-05-20" formatında veya Timestamp
    val timestamp: Timestamp? = null, // Otomatik sıralama ve kontrol için
    val contactPhone: String = "",
    val clubName: String = "",
    val status: String = "Pending" // Pending, Verified, Rejected, Expired
)
