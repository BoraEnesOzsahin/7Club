package com.example.a7club.model

// Dosya: model/Club.kt

data class Club(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val logoUrl: String = "",
    val contactEmail: String = "",

    // Yetkilendirme
    // Bu listedeki UID'ye sahip kişiler bu kulüp adına işlem yapabilir
    val committeeUids: List<String> = emptyList()
)