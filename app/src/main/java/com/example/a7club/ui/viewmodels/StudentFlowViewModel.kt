package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.data.ClubRepository
import com.example.a7club.data.Resource
import com.example.a7club.model.Club
import com.example.a7club.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class StudentFlowViewModel : ViewModel() {

    // --- 1. ALTYAPI (Repository & Auth) ---
    // Silinecek dosyadan alındı (Veri güvenliği için önemli)
    private val repository = ClubRepository()
    private val auth = FirebaseAuth.getInstance()

    // --- 2. DURUM DEĞİŞKENLERİ (State) ---

    // a) Hata/Bilgi Mesajları (Silinecek dosyadan alındı - EventDetailScreen için ŞART)
    private val _operationStatus = mutableStateOf<String?>(null)
    val operationStatus: State<String?> = _operationStatus

    // b) Mevcut Kullanıcı Verileri
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val interests = mutableStateListOf<Int?>()
    val searchQuery = mutableStateOf("")

    // c) Tab Kontrolü (Mevcut dosyadan korundu - Tab menüler için ŞART)
    var selectedTab = mutableIntStateOf(0)

    // --- 3. LİSTE STATE'LERİ ---

    // Etkinlikler Listesi
    private val _eventsState = mutableStateOf<Resource<List<Event>>>(Resource.Loading())
    val eventsState: State<Resource<List<Event>>> = _eventsState

    // Kulüpler Listesi (Mevcut dosyadan korundu - ClubsScreen için ŞART)
    private val _clubsState = mutableStateOf<Resource<List<Club>>>(Resource.Loading())
    val clubsState: State<Resource<List<Club>>> = _clubsState

    // Katıldığım Etkinlikler (Silinecek dosyadan alındı - JoinedEventsScreen için ŞART)
    private val _joinedEventsState = mutableStateOf<List<Event>>(emptyList())
    val joinedEventsState: State<List<Event>> = _joinedEventsState

    init {
        // Uygulama açıldığında her şeyi yükle
        fetchEvents()
        fetchClubs()
        fetchMyJoinedEvents()
    }

    // --- 4. FONKSİYONLAR ---

    // Etkinlikleri Çek (Repository kullanarak güncellendi)
    fun fetchEvents() {
        viewModelScope.launch {
            _eventsState.value = Resource.Loading()
            val result = repository.getAllEvents()
            _eventsState.value = result
        }
    }

    // Kulüpleri Çek (Mevcut dosyadan korundu)
    fun fetchClubs() {
        viewModelScope.launch {
            _clubsState.value = Resource.Loading()
            Firebase.firestore.collection("clubs")
                .get()
                .addOnSuccessListener { snapshot ->
                    try {
                        val clubs = snapshot.toObjects<Club>()
                        _clubsState.value = Resource.Success(clubs)
                    } catch (e: Exception) {
                        _clubsState.value = Resource.Error("Hata: ${e.message}")
                    }
                }
                .addOnFailureListener { e ->
                    _clubsState.value = Resource.Error("Hata: ${e.message}")
                }
        }
    }

    // Etkinliğe Katıl (Silinecek dosyadan alındı - KRİTİK FONKSİYON)
    fun joinEvent(event: Event) {
        joinEventById(event.id, event.title)
    }

    fun joinEvent(eventId: String) {
        val title = eventsState.value.data?.find { it.id == eventId }?.title ?: "Etkinlik"
        joinEventById(eventId, title)
    }

    private fun joinEventById(eventId: String, eventTitle: String) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _operationStatus.value = "Hata: Giriş yapmış kullanıcı bulunamadı."
            return
        }

        viewModelScope.launch {
            _operationStatus.value = "İşlem yapılıyor..."
            val result = repository.joinEvent(eventId, currentUserId)
            when(result) {
                is Resource.Success -> {
                    _operationStatus.value = "Başarıyla Katıldınız: $eventTitle"
                    fetchMyJoinedEvents() // Listeyi güncelle
                }
                is Resource.Error -> {
                    _operationStatus.value = result.message ?: "Bir hata oluştu."
                }
                else -> {}
            }
        }
    }

    // Katıldıklarımı Getir (Silinecek dosyadan alındı)
    fun fetchMyJoinedEvents() {
        val currentUserId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = repository.getJoinedEvents(currentUserId)
            if (result is Resource.Success) {
                _joinedEventsState.value = result.data ?: emptyList()
            }
        }
    }

    // Arama ve Diğer Yardımcılar
    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun addInterest(interest: Int?) {
        interests.add(interest)
    }

    fun clearStatusMessage() {
        _operationStatus.value = null
    }
}