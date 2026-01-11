package com.example.a7club.data

import com.example.a7club.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Role isimlerini FirebaseSeeder'da kullandığımız stringlerle eşleştirdik
enum class UserRole(val roleName: String) {
    STUDENT("STUDENT"),
    CLUB_COMMITTEE("COMMITTEE"),
    PERSONNEL("PERSONNEL") // Seeder'da 'PERSONNEL' olarak kaydettik
}

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun signIn(email: String, password: String, role: UserRole): Resource<User> {
        return try {
            // 1. Firebase Authentication ile şifre ve mail kontrolü yap
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid

            if (uid == null) {
                return Resource.Error("Kullanıcı kimliği alınamadı.")
            }

            // 2. Firestore'dan kullanıcının detaylarını çek
            val snapshot = db.collection("users").document(uid).get().await()

            // 3. Gelen veriyi TEK OLAN User modeline çevir
            // (Artık Student veya Personnel ayrımı yok, hepsi User)
            val user = snapshot.toObject(User::class.java)

            if (user != null) {
                // 4. Kullanıcının rolü, seçilen rol ile eşleşiyor mu kontrol et
                if (user.role == role.roleName) {
                    Resource.Success(user)
                } else {
                    // Örneğin: Öğrenci girişinden personel girmeye çalışırsa engelle
                    auth.signOut()
                    Resource.Error("Bu hesaba ${role.roleName} giriş yetkisi verilmemiş. (Rolünüz: ${user.role})")
                }
            } else {
                Resource.Error("Kullanıcı verisi veritabanında bulunamadı.")
            }

        } catch (e: Exception) {
            // Firebase hatalarını yakala
            Resource.Error(e.message ?: "Giriş başarısız.")
        }
    }

    fun signOut() {
        auth.signOut()
    }
}