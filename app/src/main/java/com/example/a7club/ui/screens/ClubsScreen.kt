package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // BU IMPORT ÇOK ÖNEMLİ
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.model.Club
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

// Kart Rengi (Etkinlik ekranıyla uyumlu)
private val CardBackgroundColor = Color(0xFFF3EFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen(
    navController: NavController,
    viewModel: StudentFlowViewModel = viewModel()
) {
    val clubsState by viewModel.clubsState
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("Kulüpler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                    actions = {
                        IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) {
                            Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )

                // Arama Çubuğu
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Kulüp ara...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = DarkBlue) },
                    shape = RoundedCornerShape(32.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF8F8F8),
                        focusedContainerColor = Color(0xFFF8F8F8),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        },
        bottomBar = {
            StudentBottomAppBar(navController = navController)
        }
    ) { paddingValues ->

        // --- LİSTELEME MANTIĞI ---
        when (val state = clubsState) {
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
                val allClubs = state.data ?: emptyList()

                // Filtreleme
                val filteredClubs = allClubs.filter { club ->
                    club.name.contains(searchQuery, ignoreCase = true)
                }

                if (filteredClubs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Listelenecek kulüp bulunamadı.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // --- DÜZELTİLEN KISIM BURASI ---
                        // items(filteredEvents = ...) HATALIYDI.
                        // Doğrusu items(liste) { öğe -> ... } şeklindedir.
                        items(filteredClubs) { club ->
                            StudentClubCard(club) {
                                // Detay sayfasına gitmek istersen burayı açabilirsin
                                // navController.navigate(Routes.ClubDetail.createRoute(club.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- KULÜP KARTI TASARIMI ---
@Composable
fun StudentClubCard(club: Club, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo Alanı
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (club.name.isNotEmpty()) {
                    Text(
                        text = club.name.first().uppercase(),
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                } else {
                    Icon(Icons.Default.Groups, null, tint = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // İsim Alanı
            Text(
                text = club.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}