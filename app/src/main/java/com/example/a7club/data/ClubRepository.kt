package com.example.a7club.data

import com.example.a7club.data.models.Event
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ClubRepository {

    private val firestore = Firebase.firestore

    suspend fun getAllEvents(): Resource<List<Event>> {
        return try {
            val snapshot = firestore.collection("events").get().await()
            val events = snapshot.toObjects(Event::class.java)
            Resource.Success(events)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
