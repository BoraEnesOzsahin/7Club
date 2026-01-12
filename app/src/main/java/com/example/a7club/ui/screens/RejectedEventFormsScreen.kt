@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.model.Event // Event modelinin importu
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun RejectedEventFormsScreen(navController: NavController) {
    // --- VERİTABANI BAĞLANTISI ---
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    // Listeyi artık String değil, Event objesi olarak tutuyoruz
    var rejectedEvents by remember { mutableStateOf<List<Event>>(emptyList()) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                // 1. Kulübü bul
                val userDoc = db.collection("users").document(uid).get().await()
                val enrolledClubs = userDoc.get("enrolledClubs") as? List<String>
                val myClubId = enrolledClubs?.firstOrNull()

                if (myClubId != null) {
                    // 2. Statüsü "REJECTED" olanları çek
                    val snapshot = db.collection("events")
                        .whereEqualTo("clubId", myClubId)
                        .whereEqualTo("status", "REJECTED")
                        .get()
                        .await()

                    rejectedEvents = snapshot.toObjects(Event::class.java)
                }
            } catch (e: Exception) {
                // Hata durumu (sessiz kalabilir veya loglanabilir)
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reddedilen Formlar", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            ClubAdminBottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Veritabanından gelen listeyi basıyoruz
                items(rejectedEvents) { event ->
                    RejectedEventCard(event.title) {
                        // Detay sayfasına giderken eventId veya ismi gönderilebilir
                        navController.navigate(Routes.RejectedEventDetail.createRoute(event.title))
                    }
                }
            }
        }
    }
}

@Composable
fun RejectedEventCard(eventName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Kırmızımsı arka plan
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = eventName, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 16.sp)
            Icon(Icons.Default.Cancel, contentDescription = null, tint = Color.Red)
        }
    }
}