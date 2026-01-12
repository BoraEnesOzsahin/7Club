package com.example.a7club.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a7club.model.Event // Event Modeli
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCalendarScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    // Veritabanından gelecek liste
    var approvedEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Verileri Çek
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                // 1. Kulübü Bul
                val userDoc = db.collection("users").document(uid).get().await()
                val enrolledClubs = userDoc.get("enrolledClubs") as? List<String>
                val myClubId = enrolledClubs?.firstOrNull()

                if (myClubId != null) {
                    // 2. Sadece APPROVED (Onaylanmış) etkinlikleri çek
                    val snapshot = db.collection("events")
                        .whereEqualTo("clubId", myClubId)
                        .whereEqualTo("status", "APPROVED")
                        .get()
                        .await()

                    approvedEvents = snapshot.toObjects(Event::class.java)
                }
            } catch (e: Exception) {
                // Hata yönetimi
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                actions = {
                    IconButton(onClick = { /* Bildirimler */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = DarkBlue)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
            ClubAdminBottomAppBar(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- ARAMA KISMI (Dokunulmadı) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f).height(45.dp),
                    shape = RoundedCornerShape(25.dp),
                    color = Color(0xFFF3EFFF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = DarkBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Ara...", fontSize = 14.sp, color = Color.Gray) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    Icons.Default.Tune,
                    contentDescription = "Filter",
                    tint = DarkBlue,
                    modifier = Modifier.size(28.dp).clickable { /* Filtrele */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- LİSTELEME ---
            if (approvedEvents.isEmpty()) {
                Text("Henüz onaylanmış etkinlik yok.", color = Color.Gray, modifier = Modifier.padding(top = 20.dp))
            } else {
                approvedEvents
                    .filter { it.title.contains(searchQuery, ignoreCase = true) }
                    .forEach { event ->
                        Button(
                            onClick = {
                                navController.navigate(Routes.PersonnelEventDetail.createRoute(event.title, event.clubName))
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF3EFFF),
                                contentColor = DarkBlue
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(text = event.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}