package com.example.a7club.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.a7club.model.Event
import com.google.firebase.Timestamp
data class ClubMember(
    val id: String,
    val schoolNo: String,
    val fullName: String
)

class ClubViewModel : ViewModel() {
    // Dinamik Üye Listesi (Gerçek zamanlı StateList)
    private val _members = mutableStateListOf<ClubMember>(
        ClubMember("1", "202101001", "Fatma Zülal Baltacı"),
        ClubMember("2", "202101002", "Bora Enes Özşahin"),
        ClubMember("3", "202101003", "Yağmur Direkçi"),
        ClubMember("4", "202202015", "Sami Sidar"),
        ClubMember("5", "202305012", "Azra Sağdıç")
    )
    val members: List<ClubMember> get() = _members

    // Yeni bir öğrenci katıldığında bu fonksiyon çağrılacak
    fun addMember(schoolNo: String, fullName: String) {
        val newId = (_members.size + 1).toString()
        _members.add(ClubMember(newId, schoolNo, fullName))
    }
}
