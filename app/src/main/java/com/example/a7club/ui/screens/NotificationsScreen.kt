package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.a7club.ui.theme.VeryLightPurple // Arka plan için kullanabiliriz
import com.example.a7club.ui.theme._7ClubTheme
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: StudentFlowViewModel = viewModel()
) {
    // ViewModel'den gelen veri akışını izliyoruz
    val eventsState by viewModel.eventsState

    Scaffold(
        containerColor = Color.White, // Kartlar renkli olduğu için arka planı beyaz yapıyoruz, daha temiz durur.
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Bildirimler",
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = DarkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Bildirimler yüklenemedi.", color = Color.Gray)
                    }
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
                        Text("Henüz yeni bir bildirim yok.", color = Color.Gray)
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
                            NotificationCard(event = event)
                        }
                    }
                }
            }
        }
        // --- LOGIC BİTİŞİ ---
    }
}

// --- GÜNCELLENEN TASARIM ---
@Composable
fun NotificationCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        // Kart rengi LightPurple (hafif şeffaflık ile daha modern durur)
        colors = CardDefaults.cardColors(containerColor = LightPurple.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SOL TARAFTAKİ ZİL İKONU KUTUSU
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White), // İkonun arkası beyaz
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // SAĞ TARAFTAKİ METİN ALANI
            Column(
                modifier = Modifier.weight(1f) // Kalan alanı doldur
            ) {
                // Üst Başlık (Küçük ve silik)
                Text(
                    text = "Yeni Etkinlik Onaylandı! 🎉",
                    fontSize = 12.sp,
                    color = DarkBlue.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Ana Mesaj
                Text(
                    text = "${event.clubName}, \"${event.title}\" etkinliğini duyurdu.",
                    fontSize = 14.sp,
                    color = DarkBlue,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Tarih Bilgisi
                Text(
                    text = event.dateString, // "10 Nisan - 14:00" formatında gelir
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
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