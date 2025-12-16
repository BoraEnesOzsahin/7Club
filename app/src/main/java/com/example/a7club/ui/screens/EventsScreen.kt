package com.example.a7club.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme._7ClubTheme

@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel, showSnackbar: (String) -> Unit) {
    val eventsState by viewModel.eventsState
    val searchQuery = viewModel.searchQuery

    EventsScreenContent(
        navController = navController,
        eventsState = eventsState,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onRetry = viewModel::fetchEvents,
        showSnackbar = showSnackbar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreenContent(
    navController: NavController,
    eventsState: Resource<List<Event>>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    showSnackbar: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlikler") },
                navigationIcon = { IconButton(onClick = { showSnackbar("Menü tıklandı") }) { Icon(Icons.Default.Menu, contentDescription = "Menu") } },
                actions = {
                    IconButton(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                    }
                    IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE0E0E0))
            )
        },
        bottomBar = { BottomNav(showSnackbar) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SearchBar(searchQuery, onSearchQueryChanged, showSnackbar)

            when (eventsState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val events = eventsState.data?.filter { 
                        it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
                    } ?: emptyList()
                    
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(events) { event ->
                            EventCard(event = event, onClick = {
                                navController.navigate(Routes.EventDetail.createRoute(event.id))
                            })
                        }
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = eventsState.message ?: "Bilinmeyen bir hata oluştu.")
                            Button(onClick = onRetry) {
                                Text("Yeniden Dene")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(text = event.clubName, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit, showSnackbar: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChanged,
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
}

@Composable
fun BottomNav(showSnackbar: (String) -> Unit) {
    BottomAppBar(containerColor = Color(0xFFE0E0E0)) {
        Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
            NavigationBarItem(selected = true, onClick = { /* no-op */ }, icon = { Icon(Icons.Default.DateRange, "Etkinlikler") }, label = { Text("Etkinlikler") })
            NavigationBarItem(selected = false, onClick = { showSnackbar("Henüz hazır değil") }, icon = { Icon(Icons.Default.Search, "Keşfet") }, label = { Text("Keşfet") })
            NavigationBarItem(selected = false, onClick = { showSnackbar("Henüz hazır değil") }, icon = { Icon(Icons.Default.Star, "Kulüpler") }, label = { Text("Kulüpler") })
            NavigationBarItem(selected = false, onClick = { showSnackbar("Henüz hazır değil") }, icon = { Icon(Icons.Default.AccountCircle, "Profil") }, label = { Text("Profil") })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview_Success() {
    _7ClubTheme {
        val sampleEvents = listOf(
            Event(id = "1", title = "Bahar Konseri", clubName = "Müzik Kulübü"),
            Event(id = "2", title = "Yazılım Atölyesi", clubName = "Bilgisayar Kulübü")
        )
        EventsScreenContent(
            navController = rememberNavController(),
            eventsState = Resource.Success(sampleEvents),
            searchQuery = "",
            onSearchQueryChanged = {},
            onRetry = {},
            showSnackbar = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview_Loading() {
    _7ClubTheme {
        EventsScreenContent(
            navController = rememberNavController(),
            eventsState = Resource.Loading(),
            searchQuery = "",
            onSearchQueryChanged = {},
            onRetry = {},
            showSnackbar = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview_Error() {
    _7ClubTheme {
        EventsScreenContent(
            navController = rememberNavController(),
            eventsState = Resource.Error("Etkinlikler yüklenemedi."),
            searchQuery = "",
            onSearchQueryChanged = {},
            onRetry = {},
            showSnackbar = {}
        )
    }
}
