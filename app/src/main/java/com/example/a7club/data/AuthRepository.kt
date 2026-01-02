package com.example.a7club.data

import com.example.a7club.model.Personnel
import com.example.a7club.model.Student
import com.example.a7club.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

// Role artık koleksiyon ismi değil, 'users' içindeki 'role' alanının değerini temsil ediyor
enum class UserRole(val roleName: String) {
    STUDENT("STUDENT"),
    CLUB_COMMITTEE("COMMITTEE"),
    PERSONNEL("STAFF")
}

class AuthRepository {

    private val db = Firebase.firestore

    suspend fun signIn(email: String, password: String, role: UserRole): Resource<User> {
        return try {
            // Belirlediğimiz şemaya göre tüm kullanıcılar 'users' koleksiyonunda
            val querySnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("role", role.roleName) // Seçilen role göre filtreleme ekledik
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return Resource.Error("Bu bilgilere uygun kullanıcı bulunamadı.")
            }

            val document = querySnapshot.documents.first()

            // Şifre kontrolü (Güvenlik için plaintext önerilmez ancak mevcut mantığı koruyoruz)
            val dbPassword = document.getString("password")
            if (dbPassword == null || dbPassword != password) {
                return Resource.Error("Şifre yanlış.")
            }

            // Role göre doğru veri sınıfına (Student/Personnel) dönüştür
            val userResult: User? = when (role) {
                UserRole.STUDENT, UserRole.CLUB_COMMITTEE -> {
                    document.toObject(Student::class.java)?.copy(uid = document.id)
                }
                UserRole.PERSONNEL -> {
                    document.toObject(Personnel::class.java)?.copy(uid = document.id)
                }
            }

            if (userResult == null) {
                Resource.Error("Kullanıcı verisi ayrıştırılamadı.")
            } else {
                Resource.Success(userResult)
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Giriş yapılırken bir hata oluştu.")
        }
    }
}