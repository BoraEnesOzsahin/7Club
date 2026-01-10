@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.model.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.viewmodels.PersonnelViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PersonnelEventRequestsScreen(
    navController: NavController,
    viewModel: PersonnelViewModel = viewModel()
) {
    // --- VERİTABANI BAĞLANTISI ---
    val db = FirebaseFirestore.getInstance()
    var pendingEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Verileri Çekme Fonksiyonu
    fun fetchEvents() {
        db.collection("events")
            .whereEqualTo("status", "PENDING")
            .get()
            .addOnSuccessListener { snapshot ->
                pendingEvents = snapshot.toObjects(Event::class.java)
            }
    }

    // İlk açılışta verileri çek
    LaunchedEffect(Unit) {
        fetchEvents()
    }

    // ONAYLA ve REDDET İşlemleri
    fun updateStatus(event: Event, newStatus: String) {
        db.collection("events").document(event.id)
            .update("status", newStatus)
            .addOnSuccessListener {
                val msg = if (newStatus == "APPROVED") "Etkinlik Onaylandı" else "Etkinlik Reddedildi"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                fetchEvents() // Listeyi güncelle
            }
    }

    // Arama Filtresi
    val filteredEvents = pendingEvents.filter { event ->
        event.title.contains(searchQuery, ignoreCase = true) ||
                event.clubName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlik Talepleri", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Mevcut alt barın
            PersonnelRequestsBottomBar(navController, Routes.PersonnelEventRequests.route)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- ARAMA ÇUBUĞU ---
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
                IconButton(onClick = { }, modifier = Modifier.size(48.dp).background(VeryLightPurple, CircleShape)) {
                    Icon(Icons.Default.Tune, null, tint = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (filteredEvents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Bekleyen talep bulunamadı.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredEvents) { event ->
                        // Kart tasarımı aynı, sadece butonları ekledik
                        EventRequestCardWithActions(
                            event = event,
                            onApprove = { updateStatus(event, "APPROVED") },
                            onReject = { updateStatus(event, "REJECTED") },
                            onClick = { navController.navigate(Routes.PersonnelEventDetail.createRoute(event.title, event.clubName)) }
                        )
                    }
                }
            }
        }
    }
}

// Kart Tasarımı (Butonlu Versiyon)
@Composable
fun EventRequestCardWithActions(
    event: Event,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp) // Yüksekliği butonlar için artırdık
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = event.title, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 16.sp, textAlign = TextAlign.Center)
            Text(text = event.clubName, color = DarkBlue.copy(alpha = 0.7f), fontSize = 13.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(12.dp))

            // Onay/Red Butonları
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    shape = CircleShape,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reddet", fontSize = 12.sp)
                }

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Yeşil
                    shape = CircleShape,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Onayla", fontSize = 12.sp)
                }
            }
        }
    }
}