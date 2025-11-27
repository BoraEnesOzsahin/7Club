package com.example.a7club.model

// Dosya: model/User.kt

data class User(
    val uid: String = "",            // Firebase Auth ID'si
    val fullName: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT, // Varsayılan öğrenci
    val studentId: String? = null,   // Personelin öğrenci numarası olmayabilir, o yüzden nullable (?)
    val department: String? = null,  // Sadece Staff veya Öğrenci için
    val followedClubs: List<String> = emptyList() // Takip edilen kulüp ID'leri
)