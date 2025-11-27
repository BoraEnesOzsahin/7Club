package com.example.a7club.model

// Dosya: model/Event.kt

import com.google.firebase.Timestamp // Tarih formatı için

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp? = null,     // Firebase Timestamp formatı
    val location: String = "",

    // İlişkisel Veriler
    val clubId: String = "",
    val clubName: String = "",       // UI'da hızlı göstermek için

    // Onay Mekanizması
    val status: RequestStatus = RequestStatus.PENDING,
    val rejectionReason: String? = null, // Reddedilirse dolu olur

    val createdBy: String = "",      // Etkinliği oluşturan Committee üyesi UID
    val attendeesCount: Int = 0      // Katılımcı sayısı
)