package com.example.a7club.model

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val clubName: String = "",
    val date: Long = 0L,
    val imageUrl: String? = null
)