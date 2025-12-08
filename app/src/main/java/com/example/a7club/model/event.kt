package com.example.a7club.model

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp? = null,
    val location: String = "",

    // İlişkisel Veriler
    val clubId: String = "",
    val clubName: String = "",

    // Onay Mekanizması (Çok Önemli)
    val status: RequestStatus = RequestStatus.PENDING,
    val rejectionReason: String? = null,

    val createdBy: String = "",
    val attendeesCount: Int = 0,

    // Öğrenci katılımı için (Son eklediğimiz kısım)
    val attendees: List<String> = emptyList()
)