package com.example.a7club.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "", // "STUDENT", "PERSONNEL", "COMMITTEE"

    // Öğrenci olmayanlar (Personel vb.) için bu alanlar boş (null) olabilir:
    val studentId: String? = null,
    val department: String? = null,

    // Sadece öğrenci ve komite için dolu olur, personel için boş liste:
    val enrolledClubs: List<String> = emptyList()
)