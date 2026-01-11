package com.example.a7club.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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
import com.example.a7club.data.Resource
import com.example.a7club.model.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

private val veryLightGray = Color(0xFFF8F8F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel = viewModel()) {
    val eventsState by viewModel.eventsState
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = veryLightGray, // Arka plan rengi güncellendi
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlik Talepleri", fontWeight = FontWeight.Bold, color = DarkBlue) },
                actions = { IconButton(onClick = { /* TODO: Bildirimler */ }) { Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
            StudentBottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- ARAMA ÇUBUĞU ---
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ara...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = DarkBlue) },
                trailingIcon = { IconButton(onClick = { /* TODO: Filtreleme */ }) { Icon(Icons.Default.Tune, "Filtrele", tint = DarkBlue) } },
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- ETKİNLİK LİSTESİ ---
            when (val state = eventsState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DarkBlue)
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message ?: "Hata oluştu", color = Color.Red)
                    }
                }
                is Resource.Success -> {
                    val events = state.data ?: emptyList()
                    val filteredEvents = events.filter { it.title.contains(searchQuery, ignoreCase = true) }

                    if (filteredEvents.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Gösterilecek etkinlik bulunmuyor.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(filteredEvents) { event ->
                                StudentEventCard(event) {
                                    navController.navigate(Routes.EventDetail.createRoute(event.id))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ETKİNLİK KARTI TASARIMI ---
@Composable
fun StudentEventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.clubName,
                fontSize = 14.sp,
                color = DarkBlue.copy(alpha = 0.7f)
            )
        }
    }
}
