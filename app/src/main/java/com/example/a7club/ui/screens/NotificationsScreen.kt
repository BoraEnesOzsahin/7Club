package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.data.Resource
import com.example.a7club.model.Event
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.theme._7ClubTheme
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: StudentFlowViewModel = viewModel() // ViewModel bağlantısı eklendi
) {
    // ViewModel'den gelen veri akışını izliyoruz
    val eventsState by viewModel.eventsState

    Scaffold(
        containerColor = VeryLightPurple,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bildirimler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LightPurple
                )
            )
        }
    ) { paddingValues ->

        // --- LOGIC BAŞLANGICI ---
        when (val state = eventsState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Bildirimler yüklenemedi.", color = Color.Red)
                }
            }
            is Resource.Success -> {
                val events = state.data ?: emptyList()

                // 1. Sadece 'APPROVED' olanları filtrele
                // 2. 'sortedByDescending' ile en son eklenen (tarihi en yeni) en üstte olsun
                val approvedEvents = events
                    .filter { it.status == "APPROVED" }
                    .sortedByDescending { it.timestamp }

                if (approvedEvents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text("Henüz onaylanmış bir etkinlik bildirimi yok.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(approvedEvents) { event ->
                            // Her bir onaylanmış etkinlik için kart oluşturuyoruz
                            NotificationCard(event = event)
                        }
                    }
                }
            }
        }
        // --- LOGIC BİTİŞİ ---
    }
}

// Tasarım yapısı aynı kaldı, sadece içeriği Event objesinden alacak şekilde güncelledim.
@Composable
fun NotificationCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Başlık (Etkinlik Adı)
            Text(
                text = "📢 ${event.title}",
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Mesaj İçeriği
            Text(
                text = "${event.clubName} etkinliği onaylandı ve takvime eklendi.",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarih Bilgisi
            Text(
                text = "📅 Tarih: ${event.dateString}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkBlue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    _7ClubTheme {
        NotificationsScreen(navController = rememberNavController())
    }
}