package com.example.a7club.utils

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

suspend fun addTestData() {
    val db = Firebase.firestore
    val testData = hashMapOf(
        "message" to "Hello from Studio Bot!"
    )

    try {
        db.collection("test")
            .add(testData)
            .await()
        println("Test data added successfully!")
    } catch (e: Exception) {
        println("Error adding test data: ${e.message}")
    }
}
