package com.example.a7club.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.data.ClubRepository
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentFlowViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val interests = mutableListOf<Int?>()

    private val repository = ClubRepository()

    private val _eventsState = mutableStateOf<Resource<List<Event>>>(Resource.Loading())
    val eventsState: State<Resource<List<Event>>> = _eventsState

    var searchQuery by mutableStateOf("")
        private set

    init {
        fetchEvents()
        // Start a coroutine that refreshes the data every 5 minutes
        viewModelScope.launch {
            while (true) {
                delay(300_000) // 5 minutes in milliseconds
                fetchEvents()
            }
        }
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _eventsState.value = Resource.Loading()
            _eventsState.value = repository.getAllEvents()
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
    }
}
