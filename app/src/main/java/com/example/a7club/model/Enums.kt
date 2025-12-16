package com.example.a7club.model

// Dosya: model/Enums.kt

enum class UserRole {
    STUDENT,
    COMMITTEE, // Kulüp Yöneticisi
    STAFF      // SKS Personeli
}

enum class RequestStatus {
    PENDING,   // Onay Bekliyor
    APPROVED,  // Onaylandı
    REJECTED   // Reddedildi
}