package com.example.a7club.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.a7club.data.Resource
import com.example.a7club.model.Event
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class StudentFlowViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val interests = mutableStateListOf<Int?>()

    private val _eventsState = mutableStateOf<Resource<List<Event>>>(Resource.Loading())
    val eventsState: State<Resource<List<Event>>> = _eventsState

    val searchQuery = mutableStateOf("")

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        _eventsState.value = Resource.Loading()
        Firebase.firestore.collection("events")
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    // This ensures Firestore documents are converted to the CORRECT Event data class
                    val events = snapshot.toObjects<Event>()
                    _eventsState.value = Resource.Success(events)
                } catch (e: Exception) {
                    _eventsState.value = Resource.Error("Veri ayrıştırılırken hata oluştu: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                _eventsState.value = Resource.Error("Etkinlikler yüklenemedi: ${e.message}")
            }
    }

    fun addInterest(interest: Int?) {
        interests.add(interest)
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}
