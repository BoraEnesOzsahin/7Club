package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import com.example.a7club.utils.TranslationHelper // Bu dosyanın utils klasöründe olduğundan emin ol
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.Locale

class StudentFlowViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val interests = mutableStateListOf<Int?>()

    private val _eventsState = mutableStateOf<Resource<List<Event>>>(Resource.Loading())
    val eventsState: State<Resource<List<Event>>> = _eventsState

    val searchQuery = mutableStateOf("")

    init {
        // ViewModel başlarken çeviri modelini arka planda indir
        viewModelScope.launch {
            try {
                TranslationHelper.downloadModelIfNeeded()
            } catch (e: Exception) {
                // Hata olursa sessizce geç
            }
        }
        fetchEvents()
    }

    fun fetchEvents() {
        _eventsState.value = Resource.Loading()

        Firebase.firestore.collection("events")
            .get()
            .addOnSuccessListener { snapshot ->
                viewModelScope.launch {
                    try {
                        val rawEvents = snapshot.toObjects<Event>()

                        // Dil kontrolü
                        val currentLang = Locale.getDefault().language
                        val isEnglish = currentLang.startsWith("en")

                        val processedEvents = if (isEnglish) {
                            // İngilizce ise çevir
                            rawEvents.map { event ->
                                event.copy(
                                    title = TranslationHelper.translate(event.title),
                                    clubName = TranslationHelper.translate(event.clubName),
                                    location = TranslationHelper.translate(event.location)
                                )
                            }
                        } else {
                            // Türkçe ise olduğu gibi bırak
                            rawEvents
                        }

                        _eventsState.value = Resource.Success(processedEvents)
                    } catch (e: Exception) {
                        _eventsState.value = Resource.Error("Hata: ${e.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                _eventsState.value = Resource.Error("Yükleme hatası: ${e.message}")
            }
    }

    fun addInterest(interest: Int?) {
        interests.add(interest)
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}