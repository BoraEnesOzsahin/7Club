package com.example.a7club.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CreateEventViewModel : ViewModel() {

    // Form Alanları
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var eventTime by mutableStateOf("")      // YENİ
    var contactPhone by mutableStateOf("")   // YENİ
    var clubName by mutableStateOf("")

    // Dosya Seçimi
    var selectedFileUri by mutableStateOf<Uri?>(null)
    var isUploading by mutableStateOf(false)

    fun createEvent(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // 1. Boş Alan Kontrolü
        if (title.isBlank() || eventTime.isBlank() || contactPhone.isBlank() || selectedFileUri == null) {
            onError("Lütfen isim, saat, telefon girin ve PDF seçin.")
            return
        }

        viewModelScope.launch {
            isUploading = true
            try {
                // 2. Önce PDF'i Storage'a Yükle
                val storageRef = Firebase.storage.reference
                val fileName = "forms/${UUID.randomUUID()}.pdf"
                val fileRef = storageRef.child(fileName)

                fileRef.putFile(selectedFileUri!!).await()
                val downloadUrl = fileRef.downloadUrl.await().toString()

                // 3. Veritabanı Nesnesini Oluştur
                val newEvent = Event(
                    title = title,
                    description = description,
                    eventTime = eventTime,        // Saati ekledik
                    contactPhone = contactPhone,  // Telefonu ekledik
                    clubName = if(clubName.isBlank()) "Kulüp" else clubName,
                    timestamp = Timestamp.now(),
                    formUrl = downloadUrl,        // PDF linkini ekledik
                    status = "PENDING"            // Personel ekranına düşmesi için PENDING olmalı
                )

                // 4. Firestore'a Kaydet
                Firebase.firestore.collection("events").add(newEvent).await()

                onSuccess()
                resetForm()

            } catch (e: Exception) {
                onError("Hata: ${e.message}")
            } finally {
                isUploading = false
            }
        }
    }

    private fun resetForm() {
        title = ""
        description = ""
        eventTime = ""
        contactPhone = ""
        selectedFileUri = null
    }
}