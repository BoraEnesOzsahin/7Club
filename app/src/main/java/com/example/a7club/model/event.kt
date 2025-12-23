package com.example.a7club.model

data class Event(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val location: String,
    val time: String,
    val contactPhone: String
)
