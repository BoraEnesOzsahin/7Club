package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
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

    // Tab durumu
    var selectedTab = mutableIntStateOf(0)

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        _eventsState.value = Resource.Loading()

        // --- GÜNCELLEME: Sadece 'APPROVED' (Onaylanmış) olanları çek ---
        Firebase.firestore.collection("events")
            .whereEqualTo("status", "APPROVED")
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    val events = snapshot.toObjects<Event>()
                    _eventsState.value = Resource.Success(events)
                } catch (e: Exception) {
                    _eventsState.value = Resource.Error("Veri işlenirken hata: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                _eventsState.value = Resource.Error("Hata: ${e.message}")
            }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}