package com.example.a7club.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class VehicleRequestViewModel : ViewModel() {
    // Form alanları
    var eventName by mutableStateOf("")
    var clubName by mutableStateOf("")
    var vehicleType by mutableStateOf("")
    var pickupLocation by mutableStateOf("")
    var destination by mutableStateOf("")
    var passengerCount by mutableStateOf("")
    var reason by mutableStateOf("")
    var dateString by mutableStateOf("")

    // Belge Yükleme için
    var selectedDocumentUri by mutableStateOf<Uri?>(null)
    var selectedDocumentName by mutableStateOf("")
    var isUploading by mutableStateOf(false)

    fun createVehicleRequest(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // TODO: Belgeyi Firebase Storage'a yükleme ve form verilerini
        // Firestore'a kaydetme mantığı buraya eklenecek.
        if (eventName.isBlank() || clubName.isBlank() || destination.isBlank() || dateString.isBlank()) {
            onError("Lütfen yıldızlı alanları doldurun.")
            return
        }

        // Şimdilik sadece başarılı senaryosunu simüle ediyoruz.
        onSuccess()
    }
}
