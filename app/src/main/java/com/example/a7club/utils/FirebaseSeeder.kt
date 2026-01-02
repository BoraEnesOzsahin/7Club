package com.example.a7club.utils

import com.example.a7club.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

object FirebaseSeeder {
    private val db = FirebaseFirestore.getInstance()

    fun seedDatabase(onResult: (String) -> Unit) {
        val batch = db.batch()

        // --- 1. KULLANICILAR ---
        val studentRef = db.collection("users").document("student_demo")
        val studentData = hashMapOf(
            "uid" to "student_demo",
            "fullName" to "Ahmet Öğrenci",
            "email" to "ogrenci@yeditepe.edu.tr",
            "password" to "123456",
            "role" to "STUDENT",
            "studentId" to "2022001",
            "department" to "Bilgisayar Müh.",
            "enrolledClubs" to listOf("club_yukek")
        )
        batch.set(studentRef, studentData)

        val staffRef = db.collection("users").document("staff_demo")
        val staffData = hashMapOf(
            "uid" to "staff_demo",
            "fullName" to "Mehmet Personel",
            "email" to "personel@yeditepe.edu.tr",
            "password" to "123456",
            "role" to "STAFF",
            "department" to "Kültür Ofisi"
        )
        batch.set(staffRef, staffData)

        val committeeRef = db.collection("users").document("committee_demo")
        val committeeData = hashMapOf(
            "uid" to "committee_demo",
            "fullName" to "Ayşe Başkan",
            "email" to "yonetim@yeditepe.edu.tr",
            "password" to "123456",
            "role" to "COMMITTEE",
            "studentId" to "2021005",
            "department" to "Endüstri Tasarımı",
            "enrolledClubs" to listOf("club_yukek")
        )
        batch.set(committeeRef, committeeData)

        // --- 2. KULÜPLER ---
        val clubRef = db.collection("clubs").document("club_yukek")
        val clubData = hashMapOf(
            "id" to "club_yukek",
            "name" to "Yükek Kulübü",
            "description" to "Yeditepe Kültür ve Etkinlik Kulübü",
            "contactEmail" to "yukek@yeditepe.edu.tr",
            "committeeUids" to listOf("committee_demo")
        )
        batch.set(clubRef, clubData)

        // --- 3. ETKİNLİKLER ---
        // Gelecek Etkinlik (Onay Bekleyen + PDF Linkli)
        val eventPendingRef = db.collection("events").document("event_pending")
        val eventPending = Event(
            id = "event_pending",
            title = "Yazılım Kampı",
            description = "Android geliştirme üzerine yoğunlaştırılmış kamp.",
            location = "B Blok Konferans Salonu",
            clubId = "club_yukek",
            clubName = "Yükek Kulübü",
            timestamp = Timestamp(getDate(daysFromNow = 5)),
            status = "PENDING",
            category = "Tech",
            contactPhone = "05551112233",
            formUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        )
        batch.set(eventPendingRef, eventPending)

        // Geçmiş Etkinlik
        val eventPastRef = db.collection("events").document("event_past")
        val eventPast = Event(
            id = "event_past",
            title = "Tanışma Toplantısı",
            description = "Yeni üyelerle tanışma.",
            location = "GSF Çimler",
            clubId = "club_yukek",
            clubName = "Yükek Kulübü",
            timestamp = Timestamp(getDate(daysFromNow = -2)),
            status = "APPROVED",
            category = "General",
            contactPhone = "05551112233"
        )
        batch.set(eventPastRef, eventPast)

        // --- 4. ARAÇ TALEBİ (YENİ EKLENDİ) ---
        // Yazılım Kampı etkinliğine bağlı bir otobüs isteği
        val vehicleReqRef = db.collection("vehicleRequests").document("req_demo_1")
        val vehicleRequest = VehicleRequest(
            id = "req_demo_1",
            eventId = "event_pending", // Yazılım Kampı'na bağlıyoruz
            clubId = "club_yukek",
            vehicleType = "Otobüs (45 Kişilik)",
            pickupLocation = "Kadıköy Rıhtım",
            destination = "Yeditepe Üniversitesi",
            passengerCount = 40,
            notes = "Öğrenciler saat 09:00'da hazır olacak.",
            requestDate = Timestamp.now()
        )
        batch.set(vehicleReqRef, vehicleRequest)

        batch.commit()
            .addOnSuccessListener { onResult("Veritabanı güncellendi! (Araç talebi eklendi)") }
            .addOnFailureListener { e -> onResult("Hata: ${e.message}") }
    }

    private fun getDate(daysFromNow: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysFromNow)
        return calendar.time
    }
}