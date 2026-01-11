package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.a7club.data.Resource
import com.example.a7club.model.Club // --- YENİ EKLENDİ: Club modelini import et
import com.example.a7club.model.Event
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class StudentFlowViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val interests = mutableStateListOf<Int?>()

    // --- MEVCUT: Etkinlikler State ---
    private val _eventsState = mutableStateOf<Resource<List<Event>>>(Resource.Loading())
    val eventsState: State<Resource<List<Event>>> = _eventsState

    // --- YENİ EKLENDİ: Kulüpler State ---
    // Kulüplerin listesini tutacak değişken
    private val _clubsState = mutableStateOf<Resource<List<Club>>>(Resource.Loading())
    val clubsState: State<Resource<List<Club>>> = _clubsState

    val searchQuery = mutableStateOf("")

    // Tab durumu
    var selectedTab = mutableIntStateOf(0)

    init {
        fetchEvents()
        fetchClubs() // --- YENİ EKLENDİ: Uygulama açılınca kulüpleri de çek
    }

    // --- MEVCUT FONKSİYON ---
    fun fetchEvents() {
        _eventsState.value = Resource.Loading()

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

    // --- YENİ EKLENDİ: Kulüpleri Çekme Fonksiyonu ---
    fun fetchClubs() {
        _clubsState.value = Resource.Loading()

        // "clubs" koleksiyonundaki tüm belgeleri çekiyoruz
        Firebase.firestore.collection("clubs")
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    // Gelen veriyi Club modeline dönüştür
                    val clubs = snapshot.toObjects<Club>()
                    _clubsState.value = Resource.Success(clubs)
                } catch (e: Exception) {
                    _clubsState.value = Resource.Error("Kulüpler alınırken hata: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                _clubsState.value = Resource.Error("Hata: ${e.message}")
            }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}