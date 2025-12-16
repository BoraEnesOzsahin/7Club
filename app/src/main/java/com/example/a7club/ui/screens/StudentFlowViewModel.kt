package com.example.a7club.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.data.Resource
import com.example.a7club.model.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentFlowViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val interests = mutableStateListOf<Int?>()

    // Resource state for events loading/success/error
    private val _eventsState = mutableStateOf<Resource<List<Event>>>(Resource.Loading())
    val eventsState: State<Resource<List<Event>>> = _eventsState

    val searchQuery = mutableStateOf("")

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

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _eventsState.value = Resource.Loading()
            try {
                // Simulate network delay
                delay(1000)
                _eventsState.value = Resource.Success(allEvents)
            } catch (e: Exception) {
                _eventsState.value = Resource.Error("Etkinlikler yüklenirken bir hata oluştu: ${e.message}")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        // Filtreleme artık UI tarafında yapılıyor veya buraya yeni bir filtreleme mantığı eklenebilir.
        // Şimdilik sadece searchQuery state'ini güncelliyoruz.
    }
}
