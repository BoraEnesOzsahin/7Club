package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.theme._7ClubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel, showSnackbar: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlikler") },
                navigationIcon = { IconButton(onClick = { showSnackbar("Menü tıklandı") }) { Icon(Icons.Default.Menu, contentDescription = "Menu") } },
                actions = { IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) { Icon(Icons.Default.Notifications, contentDescription = "Notifications") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE0E0E0))
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFFE0E0E0)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { /* Already on this screen */ },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Etkinlikler") },
                        label = { Text("Etkinlikler") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { showSnackbar("Henüz hazır değil") },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Keşfet") },
                        label = { Text("Keşfet") }
                    )
                    Box(modifier = Modifier.weight(0.4f)){} // Placeholder for the triangle
                    NavigationBarItem(
                        selected = false,
                        onClick = { showSnackbar("Henüz hazır değil") },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Kulüpler") },
                        label = { Text("Kulüpler") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { showSnackbar("Henüz hazır değil") },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profil") },
                        label = { Text("Profil") }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = viewModel.searchQuery.value,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text("Ara...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE0E0E0),
                        unfocusedContainerColor = Color(0xFFE0E0E0),
                        disabledContainerColor = Color(0xFFE0E0E0),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                IconButton(onClick = { showSnackbar("Filtre tıklandı") }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Filter")
                }
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.filteredEvents.value) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = event.title)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    _7ClubTheme {
        EventsScreen(
            navController = rememberNavController(),
            viewModel = StudentFlowViewModel(),
            showSnackbar = {}
        )
    }
}
