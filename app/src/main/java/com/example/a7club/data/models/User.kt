package com.example.a7club.data.models

/**
 * Represents a generic user in the system.
 * This data class is designed to be compatible with Firestore.
 */
data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "", // Note: Storing passwords in plaintext is not secure. This is for educational purposes.
    val role: String = ""
)
