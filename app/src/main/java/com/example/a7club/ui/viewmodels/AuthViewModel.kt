package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a7club.data.AuthRepository
import com.example.a7club.data.Resource
import com.example.a7club.data.UserRole
import com.example.a7club.model.User
import com.google.firebase.auth.FirebaseAuth // YENİ EKLENDİ
import com.google.firebase.firestore.FirebaseFirestore // YENİ EKLENDİ
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // YENİ EKLENDİ
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // UI state - Artık sealed User arayüzü ile çalışıyor
    private val _loginState = MutableStateFlow<Resource<User>?>(null)
    val loginState: StateFlow<Resource<User>?> = _loginState

    // -------------------------------------------------------------
    // --- YENİ EKLENEN BÖLÜM: Profil İsmi İçin State ---
    // -------------------------------------------------------------
    private val _currentStudentName = MutableStateFlow("Yükleniyor...")
    val currentStudentName: StateFlow<String> = _currentStudentName.asStateFlow()
    // -------------------------------------------------------------

    // Form fields
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun signIn(role: UserRole) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = Resource.Error("Email ve şifre boş bırakılamaz.")
            return
        }

        viewModelScope.launch {
            // Set loading state
            _loginState.value = Resource.Loading()

            // Perform sign in
            val result = authRepository.signIn(email, password, role)

            // Update state with the result
            _loginState.value = result
        }
    }

    fun signOut() {
        authRepository.signOut()
        _loginState.value = null // Reset state on sign out
    }

    fun resetLoginState() {
        _loginState.value = null
        email = ""
        password = ""
    }

    // -------------------------------------------------------------
    // --- YENİ EKLENEN BÖLÜM: Veritabanından İsim Çekme Fonksiyonu ---
    // -------------------------------------------------------------
    fun fetchStudentProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Veritabanında "fullName" alanını okuyoruz
                        val name = document.getString("fullName") ?: "İsimsiz Kullanıcı"
                        _currentStudentName.value = name
                    } else {
                        _currentStudentName.value = "Kullanıcı Bulunamadı"
                    }
                }
                .addOnFailureListener {
                    _currentStudentName.value = "Hata Oluştu"
                }
        } else {
            _currentStudentName.value = "Giriş Yapılmadı"
        }
    }
    // -------------------------------------------------------------
}