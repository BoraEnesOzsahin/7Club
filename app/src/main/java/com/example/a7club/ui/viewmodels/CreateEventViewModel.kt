package com.example.a7club.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CreateEventViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- Form Alanları ---
    var title = mutableStateOf("")
    var description = mutableStateOf("")
    var location = mutableStateOf("")

    // Tarih ve Saat ayrı ayrı tutuluyor
    var dateString = mutableStateOf("")
    var eventTime = mutableStateOf("")

    var category = mutableStateOf("Business")
    var contactPhone = mutableStateOf("")

    // Görsel Yükleme Alanı
    var selectedFileUri = mutableStateOf<Uri?>(null)

    // --- YENİ EKLENEN: Belge (PDF/Word) Yükleme Alanı ---
    var selectedDocumentUri = mutableStateOf<Uri?>(null)
    var selectedDocumentName = mutableStateOf("") // Kullanıcıya dosya adını göstermek için

    var isUploading = mutableStateOf(false)
    var clubName = mutableStateOf("")

    init {
        fetchClubName()
    }

    private fun fetchClubName() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val userSnapshot = db.collection("users").document(uid).get().await()
                val enrolledClubs = userSnapshot.get("enrolledClubs") as? List<String>
                val clubId = enrolledClubs?.firstOrNull() ?: ""

                if (clubId.isNotEmpty()) {
                    val clubSnapshot = db.collection("clubs").document(clubId).get().await()
                    clubName.value = clubSnapshot.getString("name") ?: ""
                }
            } catch (e: Exception) {
                Log.e("CreateEventVM", "Kulüp adı çekilemedi: ${e.message}")
            }
        }
    }

    fun createEvent(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (title.value.isEmpty() || description.value.isEmpty() || location.value.isEmpty() || dateString.value.isEmpty()) {
            onError("Lütfen zorunlu alanları doldurun.")
            return
        }

        isUploading.value = true
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val userSnapshot = db.collection("users").document(uid).get().await()
                val enrolledClubs = userSnapshot.get("enrolledClubs") as? List<String>
                val clubId = enrolledClubs?.firstOrNull() ?: ""

                // 1. Görsel URL'si (Şimdilik URI string)
                val imageUrlString = selectedFileUri.value?.toString() ?: ""

                // 2. Belge URL'si (Şimdilik URI string) - formUrl alanına kaydedilecek
                val documentUrlString = selectedDocumentUri.value?.toString() ?: ""

                val newEventId = UUID.randomUUID().toString()
                val finalDateString = "${dateString.value} - ${eventTime.value}"

                val newEvent = Event(
                    id = newEventId,
                    title = title.value,
                    description = description.value,
                    location = location.value,
                    clubId = clubId,
                    clubName = clubName.value.ifEmpty { "Kulüp" },
                    dateString = finalDateString,
                    timestamp = Timestamp.now(),
                    status = "PENDING",
                    category = category.value,
                    contactPhone = contactPhone.value,
                    imageUrl = imageUrlString,

                    // Islak imzalı belge buraya kaydediliyor:
                    formUrl = documentUrlString
                )

                db.collection("events").document(newEventId).set(newEvent).await()

                isUploading.value = false
                onSuccess()

            } catch (e: Exception) {
                Log.e("CreateEventVM", "Hata: ${e.message}")
                isUploading.value = false
                onError(e.message ?: "Etkinlik oluşturulamadı.")
            }
        }
    }
}