package com.example.a7club.data

import com.example.a7club.model.Personnel
import com.example.a7club.model.Student
import com.example.a7club.model.User // Sealed Interface'i import et
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

enum class UserRole(val collectionName: String) {
    STUDENT("students"),
    CLUB_COMMITTEE("club_committees"),
    PERSONNEL("personnel")
}

class AuthRepository {

    private val db = Firebase.firestore

    suspend fun signIn(email: String, password: String, role: UserRole): Resource<User> {
        return try {
            val querySnapshot = db.collection(role.collectionName)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return Resource.Error("Bu email ile kayıtlı kullanıcı bulunamadı.")
            }

            val document = querySnapshot.documents.first()

            val dbPassword = document.getString("password")
            if (dbPassword == null || dbPassword != password) {
                return Resource.Error("Şifre yanlış.")
            }

            // Role göre doğru veri sınıfına dönüştür
            val userResult: User? = when (role) {
                UserRole.STUDENT, UserRole.CLUB_COMMITTEE -> {
                    document.toObject(Student::class.java)?.copy(uid = document.id)
                }
                UserRole.PERSONNEL -> {
                    document.toObject(Personnel::class.java)?.copy(uid = document.id)
                }
            }

            if (userResult == null) {
                Resource.Error("Kullanıcı verisi okunamadı veya zorunlu alanlar eksik. (Örn: studentId)")
            } else {
                Resource.Success(userResult)
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Giriş yapılırken bilinmeyen bir hata oluştu.")
        }
    }
}
