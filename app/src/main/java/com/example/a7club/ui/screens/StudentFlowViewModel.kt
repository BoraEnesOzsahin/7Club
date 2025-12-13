package com.example.a7club.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.a7club.model.Event

class StudentFlowViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val interests = mutableStateListOf<Int?>()

    private val allEvents = listOf(
        Event("1", "X kulübü Y etkinliği"),
        Event("2", "A kulübü B etkinliği"),
        Event("3", "Müzik dinletisi"),
        Event("4", "Dans gösterisi"),
        Event("5", "Tiyatro oyunu"),
        Event("6", "Film gösterimi"),
        Event("7", "Kitap kulübü toplantısı"),
        Event("8", "Kariyer günü"),
        Event("9", "Bahar şenliği"),
        Event("10", "Spor turnuvası"),
        Event("11", "Satranç turnuvası"),
        Event("12", "E-spor turnuvası"),
        Event("13", "Yazılım atölyesi"),
        Event("14", "Girişimcilik paneli"),
        Event("15", "Doğa yürüyüşü")
    )

    val searchQuery = mutableStateOf("")
    val filteredEvents = mutableStateOf(allEvents)

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        filteredEvents.value = allEvents.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }
}
