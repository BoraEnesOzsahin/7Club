package com.example.a7club.utils

import android.util.Log
import com.example.a7club.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

object FirebaseSeeder {
    private val db = FirebaseFirestore.getInstance()

    fun seedDatabase(onResult: (String) -> Unit) {
        val batch = db.batch()

        // --- 1. KULLANICILAR (HashMap Kullanarak - En Garantili Yöntem) ---

        // A) ÖĞRENCİ
        val studentRef = db.collection("users").document("student_demo")
        val studentData = hashMapOf<String, Any>(
            "uid" to "student_demo",
            "fullName" to "Ahmet Öğrenci",
            "email" to "ogrenci@yeditepe.edu.tr",
            "role" to "STUDENT",
            "studentId" to "2022001",
            "enrolledClubs" to listOf("club_yukek")
        )
        batch.set(studentRef, studentData)

        // B) PERSONEL (SENİN HESABIN - DOĞRU, DOKUNMA)
        val personnelUid = "kCawCtupLNbfGEkvdJEIHIlbjCJ2"

        val staffRef = db.collection("users").document(personnelUid)
        val staffData = hashMapOf<String, Any>(
            "uid" to personnelUid,
            "fullName" to "Mehmet Personel",
            "email" to "personel@yeditepe.edu.tr",
            "role" to "PERSONNEL"
        )
        batch.set(staffRef, staffData)

        // C) KULÜP YÖNETİCİSİ (YÖNETİM)
        // -----------------------------------------------------------
        // BURAYA DİKKAT: Firebase'den 'yonetim@yeditepe.edu.tr' için aldığın UID'yi yapıştır.
        val committeeUid = "JCVei8L0cjYG0WOLifXDXdil7xO2"
        // -----------------------------------------------------------

        val committeeRef = db.collection("users").document(committeeUid)
        val committeeData = hashMapOf<String, Any>(
            "uid" to committeeUid, // Değişkeni buraya atadık
            "fullName" to "Ayşe Başkan",
            "email" to "yonetim@yeditepe.edu.tr",
            "role" to "COMMITTEE",
            "enrolledClubs" to listOf("club_yukek")
        )
        batch.set(committeeRef, committeeData)

        // --- 2. KULÜPLER ---
        val clubRef = db.collection("clubs").document("club_yukek")
        val clubData = hashMapOf<String, Any>(
            "id" to "club_yukek",
            "name" to "Kültür ve Etkinlik Kulübü",
            "description" to "Yeditepe Kültür ve Etkinlik Kulübü",
            // ARTIK BU KULÜBÜN YÖNETİCİSİ SENİN YENİ HESABIN:
            "committeeUids" to listOf(committeeUid)
        )
        batch.set(clubRef, clubData)

        val clubTechRef = db.collection("clubs").document("club_bilisim")
        val clubTechData = hashMapOf<String, Any>(
            "id" to "club_bilisim",
            "name" to "Bilişim Kulübü",
            "description" to "Teknoloji ve Yazılım",
            "committeeUids" to listOf<String>()
        )
        batch.set(clubTechRef, clubTechData)

        // --- 3. ETKİNLİKLER ---

        // A) Gelecek Bekleyen Etkinlik
        val eventPendingRef = db.collection("events").document("event_pending")
        val eventPending = Event(
            id = "event_pending",
            title = "Liderlik Zirvesi",
            description = "İş dünyasının liderleri ile buluşma.",
            location = "İnan Kıraç Salonu",
            clubId = "club_yukek",
            clubName = "Kültür ve Etkinlik Kulübü",
            dateString = "20 Mayıs - 14:00",
            timestamp = Timestamp(getDate(daysFromNow = 5)),
            status = "PENDING",
            category = "Business",
            formUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        )
        batch.set(eventPendingRef, eventPending)

        // B) Gecikmiş Bekleyen Etkinlik
        val eventOverdueRef = db.collection("events").document("event_overdue")
        val eventOverdue = Event(
            id = "event_overdue",
            title = "Yapay Zeka Semineri",
            description = "AI teknolojilerindeki son gelişmeler.",
            location = "Mavi Salon",
            clubId = "club_bilisim",
            clubName = "Bilişim Kulübü",
            dateString = "1 Ocak - 10:00",
            timestamp = Timestamp(getDate(daysFromNow = -5)),
            status = "PENDING",
            category = "Tech",
            formUrl = ""
        )
        batch.set(eventOverdueRef, eventOverdue)

        // C) Geçmiş Onaylı Etkinlik
        val eventPastRef = db.collection("events").document("event_past")
        val eventPast = Event(
            id = "event_past",
            title = "Tanışma Toplantısı",
            description = "Yeni üyelerle tanışma.",
            location = "GSF Çimler",
            clubId = "club_yukek",
            clubName = "Kültür ve Etkinlik Kulübü",
            dateString = "10 Nisan - 12:00",
            timestamp = Timestamp(getDate(daysFromNow = -30)),
            status = "APPROVED",
            category = "General"
        )
        batch.set(eventPastRef, eventPast)

        // --- 4. ARAÇ TALEPLERİ ---
        val vehicleRef = db.collection("vehicleRequests").document("req_001")
        val vehicleReq = VehicleRequest(
            id = "req_001",
            eventId = "event_pending",
            eventName = "Liderlik Zirvesi",
            clubName = "Kültür ve Etkinlik Kulübü",
            vehicleType = "Otobüs (45 Kişilik)",
            pickupLocation = "Kadıköy Rıhtım",
            destination = "Yeditepe Kampüs",
            passengerCount = 40,
            status = "PENDING",
            requestDate = Timestamp.now()
        )
        batch.set(vehicleRef, vehicleReq)

        // --- KAYDET ---
        batch.commit()
            .addOnSuccessListener {
                Log.d("FirebaseSeeder", "Veritabanı başarıyla güncellendi!")
                onResult("Veritabanı güncellendi! Uygulamayı yeniden başlatabilirsin.")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseSeeder", "Hata: ${e.message}")
                onResult("Hata: ${e.message}")
            }
    }

    private fun getDate(daysFromNow: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysFromNow)
        return calendar.time
    }
}