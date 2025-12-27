package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: StudentFlowViewModel,
    showSnackbar: (String) -> Unit
) {
    val eventsState by viewModel.eventsState
    val searchQuery by viewModel.searchQuery
    // Kategoriler
    val categories = listOf("Tümü", "Teknoloji", "Sanat", "Spor", "Müzik", "Kariyer")
    var selectedCategory by remember { mutableStateOf("Tümü") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F1FF)) // Arka plan rengi
    ) {
        // --- 1. ARAMA VE FİLTRELEME ALANI ---
        SearchAndFilterBar(
            onSearchClick = {
                showSnackbar("Arama özelliği çalışıyor")
            },
            onFilterClick = {
                // Hata veren satır burasıydı, şimdi düzeldi
                navController.navigate(Routes.FilterScreen.route)
            }
        )

        // --- 2. KATEGORİLER (Yatay Kaydırma) ---
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            edgePadding = 16.dp,
            containerColor = Color.Transparent,
            contentColor = DarkBlue,
            divider = {},
            indicator = {}
        ) {
            categories.forEach { category ->
                SuggestionChip(
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedCategory == category) DarkBlue else Color.White,
                        labelColor = if (selectedCategory == category) Color.White else DarkBlue
                    ),
                    border = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 3. ETKİNLİK LİSTESİ ---
        when (val state = eventsState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message ?: "Bir hata oluştu", color = Color.Red)
                }
            }
            is Resource.Success -> {
                val events = state.data ?: emptyList()
                // Basit filtreleme mantığı
                val filteredEvents = events.filter { event ->
                    (selectedCategory == "Tümü" || event.clubName.contains(selectedCategory, ignoreCase = true)) &&
                            (searchQuery.isBlank() || event.title.contains(searchQuery, ignoreCase = true))
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredEvents) { event ->
                        EventCard(event = event) {
                            // Detay sayfasına git
                            navController.navigate(Routes.EventDetail.createRoute(event.id))
                        }
                    }
                }
            }
        }
    }
}

// --- YARDIMCI BİLEŞENLER (Import sorunu olmaması için aynı dosyaya koyduk) ---

@Composable
fun SearchAndFilterBar(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Arama Alanı
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Ara",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Etkinlik veya kulüp ara...",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Filtre Butonu
        Surface(
            modifier = Modifier
                .size(50.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clickable(onClick = onFilterClick),
            shape = RoundedCornerShape(16.dp),
            color = DarkBlue
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filtrele",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.clubName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LightPurple,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkBlue
                )
            }
        }
    }
}