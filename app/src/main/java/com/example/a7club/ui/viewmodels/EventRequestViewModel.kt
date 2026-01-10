package com.example.a7club.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EventRequestViewModel : ViewModel() {
    var selectedFileUri by mutableStateOf<Uri?>(null)
    var isUploading by mutableStateOf(false)

    fun uploadEventForm(eventName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (selectedFileUri == null) {
            onError("Lütfen bir PDF dosyası seçin.")
            return
        }

        viewModelScope.launch {
            isUploading = true
            try {
                val db = Firebase.firestore

                // 1. Önce Event Name'e göre gerçek Event ID'yi bulalım
                val eventSnapshot = db.collection("events")
                    .whereEqualTo("title", eventName)
                    .limit(1)
                    .get()
                    .await()

                if (eventSnapshot.isEmpty) {
                    onError("Etkinlik bulunamadı!")
                    return@launch
                }

                val eventDoc = eventSnapshot.documents.first()
                val eventId = eventDoc.id

                // 2. PDF'i Storage'a Yükle
                val storageRef = Firebase.storage.reference
                val fileName = "forms/${eventId}_${UUID.randomUUID()}.pdf"
                val fileRef = storageRef.child(fileName)

                fileRef.putFile(selectedFileUri!!).await()
                val downloadUrl = fileRef.downloadUrl.await().toString()

                // 3. Firestore'daki mevcut etkinliği GÜNCELLE (Update)
                // "formUrl" alanını güncelliyoruz. createEvent'teki gibi yeni obje yaratmıyoruz.
                db.collection("events").document(eventId)
                    .update("formUrl", downloadUrl)
                    .await()

                onSuccess()

            } catch (e: Exception) {
                onError("Hata: ${e.message}")
            } finally {
                isUploading = false
            }
        }
    }
}

