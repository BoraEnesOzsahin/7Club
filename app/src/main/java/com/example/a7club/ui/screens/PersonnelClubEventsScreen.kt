@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun PersonnelClubEventsScreen(
    navController: NavController,
    clubName: String,
    isPast: Boolean, // true: Geçmiş, false: Gelecek
    viewModel: PersonnelViewModel = viewModel()
) {
    // Verileri yükle
    LaunchedEffect(clubName, isPast) {
        viewModel.fetchClubEvents(clubName, isPast)
    }

    val events by viewModel.currentClubEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val titleText = if (isPast) "Geçmiş Etkinlikler" else "Gelecek Etkinlikler"

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(titleText, fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DarkBlue)
            } else if (events.isEmpty()) {
                Text(
                    text = "Bu kategoride etkinlik bulunamadı.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(events) { event ->
                        // Liste eleman kartını çağırıyoruz (Aşağıda tanımlandı)
                        PersonnelListItemCard(
                            title = event.title,
                            clubName = event.clubName,
                            status = when(event.status) {
                                "PENDING" -> "Bekliyor"
                                "APPROVED" -> "Onaylandı"
                                "REJECTED" -> "Reddedildi"
                                else -> event.status
                            },
                            onClick = {
                                navController.navigate(Routes.PersonnelEventDetail.createRoute(event.title, event.clubName))
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- EKSİK OLAN KART TASARIMI (BURAYA EKLENDİ) ---
@Composable
fun PersonnelListItemCard(title: String, clubName: String, status: String, onClick: () -> Unit) {
    // Duruma göre renk seçimi
    val statusColor = when (status) {
        "Onaylandı", "APPROVED" -> Color(0xFF4CAF50) // Yeşil
        "Reddedildi", "REJECTED" -> Color(0xFFF44336) // Kırmızı
        else -> Color(0xFFFF9800)        // Turuncu (Bekliyor)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        // VeryLightPurple (0xFFEEEBFF) yerine benzer bir ton veya tema rengini kullanıyoruz
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFFF))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
                // Durum Badge'i
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kulüp: $clubName",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkBlue.copy(alpha = 0.8f)
            )
        }
    }
}