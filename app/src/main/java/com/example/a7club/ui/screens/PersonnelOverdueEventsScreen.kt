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
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.model.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.VeryLightPurple
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PersonnelOverdueEventsScreen(
    navController: NavController
) {
    // --- VERİTABANI BAĞLANTISI ---
    val db = FirebaseFirestore.getInstance()
    var overdueEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Verileri Çek (Şimdilik tüm PENDING olanları çekiyoruz)
    LaunchedEffect(Unit) {
        db.collection("events")
            .whereEqualTo("status", "PENDING")
            .get()
            .addOnSuccessListener { snapshot ->
                overdueEvents = snapshot.toObjects(Event::class.java)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    val filteredEvents = overdueEvents.filter { event ->
        event.title.contains(searchQuery, ignoreCase = true) ||
                event.clubName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gecikmiş Talepler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Talepler menüsü (Requests Bar)
            PersonnelRequestsBottomBar(navController, Routes.PersonnelOverdueEvents.route)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // ARAMA ÇUBUĞU (TASARIM KORUNDU)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Ara...", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VeryLightPurple,
                        unfocusedContainerColor = VeryLightPurple,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = DarkBlue) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = { }, modifier = Modifier.size(45.dp).background(VeryLightPurple, CircleShape)) {
                    Icon(Icons.Default.Tune, null, tint = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            } else if (filteredEvents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Bekleyen talep bulunmuyor.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                    items(filteredEvents) { event ->
                        // HATA BURADAYDI: Artık bu fonksiyon aşağıda tanımlı
                        EventRequestCard(event) {
                            navController.navigate(Routes.PersonnelEventDetail.createRoute(event.title, event.clubName))
                        }
                    }
                }
            }
        }
    }
}

// --- EKSİK OLAN KART TASARIMI ---
@Composable
fun EventRequestCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.clubName,
                color = DarkBlue.copy(alpha = 0.7f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}