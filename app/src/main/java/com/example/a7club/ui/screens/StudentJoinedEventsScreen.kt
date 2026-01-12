@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

@Composable
fun StudentJoinedEventsScreen(
    navController: NavController,
    studentFlowViewModel: StudentFlowViewModel = viewModel()
) {
    // 1. Sayfa açıldığında veritabanından güncel listeyi iste
    LaunchedEffect(Unit) {
        studentFlowViewModel.fetchMyJoinedEvents()
    }

    // 2. ViewModel'daki listeyi dinle
    val myEvents by studentFlowViewModel.joinedEventsState

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Katıldığım Etkinlikler",
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        // Alt barı buraya da eklemelisin ki navigasyon kaybolmasın
        // bottomBar = { StudentBottomAppBar(navController) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)) // Hafif gri arka plan
        ) {
            if (myEvents.isEmpty()) {
                // --- LİSTE BOŞSA GÖSTERİLECEK ---
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Henüz bir etkinliğe katılmadınız.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                // --- LİSTE DOLUYSA GÖSTERİLECEK ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(myEvents) { event ->
                        JoinedEventCard(
                            title = event.title,
                            date = event.dateString,
                            location = event.location,
                            clubName = event.clubName,
                            onClick = {
                                // İstersen buradan tekrar detay sayfasına yönlendirebilirsin
                                // navController.navigate("event_detail/${event.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

// Sadece bu sayfada kullanılacak basit bir kart tasarımı
@Composable
fun JoinedEventCard(
    title: String,
    date: String,
    location: String,
    clubName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol tarafta tarih kutusu
            Card(
                colors = CardDefaults.cardColors(containerColor = LightPurple.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.padding(12.dp).size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Sağ tarafta bilgiler
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkBlue
                )
                Text(
                    text = clubName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$date • $location",
                    fontSize = 12.sp,
                    color = DarkBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}