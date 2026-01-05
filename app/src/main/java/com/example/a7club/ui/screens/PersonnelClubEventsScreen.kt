@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    val title = if (isPast) "Geçmiş Etkinlikler" else "Gelecek Etkinlikler"

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, color = DarkBlue) },
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
                        // Liste eleman kartını kullanıyoruz (Common UI veya Home'daki)
                        PersonnelListItemCard(
                            title = event.title,
                            clubName = event.clubName,
                            status = if (event.status == "PENDING") "Bekliyor" else if(event.status == "APPROVED") "Onaylandı" else "Reddedildi",
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