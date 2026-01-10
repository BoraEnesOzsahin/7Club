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
import java.text.SimpleDateFormat
import java.util.Locale
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

    // Belge (PDF/Word) Yükleme Alanı
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
        // 1. Validasyon Kontrolü
        if (title.value.isEmpty() || description.value.isEmpty() || location.value.isEmpty() || dateString.value.isEmpty()) {
            onError("Lütfen zorunlu alanları doldurun.")
            return
        }

        isUploading.value = true
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                // 2. Kulüp Bilgilerini Al
                val userSnapshot = db.collection("users").document(uid).get().await()
                val enrolledClubs = userSnapshot.get("enrolledClubs") as? List<String>
                val clubId = enrolledClubs?.firstOrNull() ?: ""

                // 3. Dosya URL'lerini Hazırla (Şimdilik string olarak)
                val imageUrlString = selectedFileUri.value?.toString() ?: ""
                val documentUrlString = selectedDocumentUri.value?.toString() ?: ""

                val newEventId = UUID.randomUUID().toString()

                // --- TARİH VE SAAT İŞLEME (Önemli Kısım) ---
                // Kullanıcının girdiği tarih ve saati birleştiriyoruz.
                // Örn: dateString="25/11/2023", eventTime="14:00" -> "25/11/2023 14:00"
                val fullDateString = "${dateString.value} ${eventTime.value}".trim()

                val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("tr"))
                val parsedDate = try {
                    format.parse(fullDateString)
                } catch (e: Exception) {
                    // Saat girilmediyse sadece tarihi dene
                    try {
                        SimpleDateFormat("dd/MM/yyyy", Locale("tr")).parse(dateString.value)
                    } catch (e2: Exception) {
                        null
                    }
                }

                // Eğer tarih başarıyla çevrildiyse onu kullan, yoksa şu anı al (Fallback)
                // Bu timestamp sayesinde takvimde doğru günde görünecek.
                val eventTimestamp = if (parsedDate != null) Timestamp(parsedDate) else Timestamp.now()

                // 4. Etkinlik Objesini Oluştur
                val newEvent = Event(
                    id = newEventId,
                    title = title.value,
                    description = description.value,
                    location = location.value,
                    clubId = clubId,
                    clubName = clubName.value.ifEmpty { "Kulüp" },
                    dateString = fullDateString, // Ekranda göstermek için String hali
                    timestamp = eventTimestamp,  // Sıralama ve takvim filtresi için Timestamp hali
                    status = "PENDING",
                    category = category.value,
                    contactPhone = contactPhone.value,
                    imageUrl = imageUrlString,
                    formUrl = documentUrlString
                )

                // 5. Veritabanına Kaydet
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