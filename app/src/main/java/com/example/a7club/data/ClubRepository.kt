package com.example.a7club.data

import com.example.a7club.model.Event
import com.example.a7club.model.Club
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ClubRepository {

    private val firestore = Firebase.firestore
// ... ClubRepository içindeki mevcut kodların ...

    // --- ÖĞRENCİ ÖZEL FONKSİYONLARI ---

    // 1. Öğrencinin etkinliğe katılması (Database Update)
    suspend fun joinEvent(eventId: String, userId: String): Resource<Boolean> {
        return try {
            // "participants" dizisine userId'yi ekler.
            // arrayUnion: Eğer ID zaten varsa tekrar eklemez (Duplicate önler).
            firestore.collection("events").document(eventId)
                .update("participants", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .await()

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Etkinliğe katılım sırasında hata oluştu.")
        }
    }

    // 2. Öğrencinin katıldığı etkinlikleri listeleme (Database Query)
    suspend fun getJoinedEvents(userId: String): Resource<List<Event>> {
        return try {
            // "participants" listesi içinde userId geçen dökümanları filtrele
            val snapshot = firestore.collection("events")
                .whereArrayContains("participants", userId)
                .get()
                .await()

            val events = snapshot.toObjects(Event::class.java)
            Resource.Success(events)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Katıldığınız etkinlikler yüklenemedi.")
        }
    }
    suspend fun getAllEvents(): Resource<List<Event>> {
        return try {
            val snapshot = firestore.collection("events").get().await()
            val events = snapshot.toObjects(Event::class.java)
            Resource.Success(events)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    // YENİ: Tüm kulüpleri getiren fonksiyon
    suspend fun getAllClubs(): Resource<List<Club>> {
        return try {
            val snapshot = firestore.collection("clubs").get().await()
            val clubs = snapshot.toObjects(Club::class.java)
            Resource.Success(clubs)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Kulüpler çekilemedi")
        }
    }

    // YENİ: Başlangıç verilerini (10 kulüp) veritabanına yükleme fonksiyonu
    suspend fun seedDatabaseWithClubs() {
        val clubs = listOf(
            Club("1", "Yeditepe Bilişim Kulübü", "Teknoloji ve yazılım dünyasına kapı açan kulüp.", "", "bilisim@yeditepe.edu.tr"),
            Club("2", "Yeditepe Girişimcilik", "Geleceğin iş liderlerini yetiştiren platform.", "", "girisim@yeditepe.edu.tr"),
            Club("3", "Yeditepe Sanat Kulübü", "Resim ve heykel etkinlikleri merkezi.", "", "sanat@yeditepe.edu.tr"),
            Club("4", "Yeditepe Müzik Kulübü", "Konserler ve enstrüman atölyeleri.", "", "muzik@yeditepe.edu.tr"),
            Club("5", "Yeditepe Spor Kulübü", "Üniversiteler arası turnuvalar ve aktif yaşam.", "", "spor@yeditepe.edu.tr"),
            Club("6", "Yeditepe Tiyatro Topluluğu", "Sahne sanatları ve oyunculuk eğitimleri.", "", "tiyatro@yeditepe.edu.tr"),
            Club("7", "Yeditepe IEEE Öğrenci Kolu", "Uluslararası mühendislik projeleri.", "", "ieee@yeditepe.edu.tr"),
            Club("8", "Yeditepe Sinema Kulübü", "Film gösterimleri ve yönetmenlik workshopları.", "", "sinema@yeditepe.edu.tr"),
            Club("9", "Yeditepe Doğa Sporları", "Kamp, trekking ve ekstrem sporlar.", "", "doga@yeditepe.edu.tr"),
            Club("10", "Yeditepe E-Spor Kulübü", "Profesyonel oyun dünyası ve turnuvalar.", "", "espor@yeditepe.edu.tr")
        )

        for (club in clubs) {
            firestore.collection("clubs").document(club.id).set(club).await()
        }
    }
}
