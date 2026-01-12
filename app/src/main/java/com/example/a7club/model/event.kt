package com.example.a7club.model

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val clubId: String = "",
    val clubName: String = "",
    val dateString: String = "",
    val timestamp: Timestamp? = null,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val category: String = "General",
    val participants: List<String> = emptyList(),
    // Islak imzalı belge yolu olarak bu alanı kullanacağız:
    val formUrl: String = "",

    val contactPhone: String = "",
    val imageUrl: String = ""
)