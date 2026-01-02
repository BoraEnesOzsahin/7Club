package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.a7club.model.VehicleRequest
import com.google.firebase.Timestamp // Timestamp eklendi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommitteeEventViewModel : ViewModel() {

    // UI State değişkenleri kalabilir (Form için gerekli)
    var vehicleType by mutableStateOf("")
    var pickupLocation by mutableStateOf("")
    var dropoffLocation by mutableStateOf("")
    var passengerCount by mutableStateOf("")
    var notes by mutableStateOf("")

    // CommitteeEventViewModel.kt içindeki fonksiyonu bununla değiştir:

    fun submitVehicleRequest(eventName: String, clubId: String) { // eventId yerine eventName alıyoruz
        // 1. Önce bu isme sahip etkinliğin Gerçek ID'sini bulalım
        Firebase.firestore.collection("events")
            .whereEqualTo("title", eventName)
            .get()
            .addOnSuccessListener { snapshot ->
                // Eğer etkinlik bulunduysa ID'sini al, yoksa rastgele bir ID ata
                val realEventId = if (!snapshot.isEmpty) snapshot.documents[0].id else "bilinmeyen_id"

                // 2. Şimdi bu GERÇEK ID ile formu kaydedelim
                val request = VehicleRequest(
                    eventId = realEventId, // Artık doğru ID kullanılıyor!
                    clubId = clubId,
                    vehicleType = vehicleType, // Formdan gelen veri (Eksikti, eklendi)
                    pickupLocation = pickupLocation, // Formdan gelen veri
                    destination = dropoffLocation,
                    passengerCount = passengerCount.toIntOrNull() ?: 0,
                    notes = notes, // Formdan gelen notlar
                    requestDate = Timestamp.now()
                )

                Firebase.firestore.collection("vehicleRequests").add(request)
                    .addOnSuccessListener {
                        println("Araç talebi başarıyla ve DOĞRU ID ile oluşturuldu.")
                    }
                    .addOnFailureListener { e ->
                        println("Hata: ${e.message}")
                    }
            }
    }
}