package com.example.a7club.ui.screens
// Bu satırları diğer importların arasına yapıştır
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource // BU IMPORT ÖNEMLİ
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.R // BU IMPORT ÖNEMLİ
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import com.example.a7club.ui.navigation.Routes

// --- RENK PALETİ ---
val ClubLightPurple = Color(0xFFCCC2FF)
val ClubDarkBlue = Color(0xFF160092)
val ClubSurface = Color(0xFFE6E3F6)
val SharedBgColor = Color(0xFFEEEBFF)

@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: StudentFlowViewModel,
    showSnackbar: (String) -> Unit
) {
    val eventsState by viewModel.eventsState
    val searchQuery by viewModel.searchQuery

    // KATEGORİLER XML'DEN
    val categories = listOf(
        stringResource(R.string.cat_all),
        stringResource(R.string.cat_economy),
        stringResource(R.string.cat_law),
        stringResource(R.string.cat_it),
        stringResource(R.string.cat_music),
        stringResource(R.string.cat_sports)
    )
    val defaultCategory = stringResource(R.string.cat_all)
    var selectedCategory by remember { mutableStateOf(defaultCategory) }

    // Dil değişince kategoriyi sıfırla
    LaunchedEffect(defaultCategory) {
        if (!categories.contains(selectedCategory)) {
            selectedCategory = defaultCategory
        }
    }

    var selectedBottomTabIndex by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                CustomTopBar(
                    onNotificationClick = {
                        navController.navigate(Routes.StudentNotifications)
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                DateSelectorCard()
                Spacer(modifier = Modifier.height(16.dp))

                // Arama Barı
                Surface(
                    modifier = Modifier.fillMaxWidth().height(50.dp).clickable { showSnackbar("Arama aktif") },
                    shape = RoundedCornerShape(25.dp),
                    color = SharedBgColor
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // SEARCH HINT XML'DEN
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_hint), tint = ClubDarkBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        // FILTER XML'DEN
                        Icon(Icons.Default.Tune, contentDescription = stringResource(R.string.filter), tint = ClubDarkBlue, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Liste
                when (val state = eventsState) {
                    is Resource.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ClubDarkBlue) }
                    is Resource.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = state.message ?: "Hata", color = Color.Red) }
                    is Resource.Success -> {
                        val events = state.data ?: emptyList()
                        val filteredEvents = events.filter { event ->
                            (selectedCategory == defaultCategory || event.clubName.contains(selectedCategory, ignoreCase = true)) &&
                                    (searchQuery.isBlank() || event.title.contains(searchQuery, ignoreCase = true))
                        }
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 110.dp)
                        ) {
                            items(filteredEvents) { event ->
                                EventCard(event = event) { navController.navigate(Routes.EventDetail.createRoute(event.id)) }
                            }
                        }
                    }
                }
            }
        }

        // ALT BAR
        StudentBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedIndex = selectedBottomTabIndex,
            onIndexSelected = { index ->
                selectedBottomTabIndex = index
                if (index == 3) navController.navigate(Routes.StudentProfileScreen.route)
            },
            onCenterClick = {}
        )
    }
}

// --- ÜST BAR (GÜNCELLENDİ: stringResource KULLANIYOR) ---
@Composable
fun CustomTopBar(onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ClubLightPurple)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.size(48.dp))

        // BAŞLIK: "Events" / "Etkinlikler"
        Text(
            text = stringResource(R.string.events),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = ClubDarkBlue
        )

        IconButton(onClick = onNotificationClick) {
            // İKON AÇIKLAMASI: "Notifications" / "Bildirimler"
            Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.notifications), tint = ClubDarkBlue, modifier = Modifier.size(28.dp))
        }
    }
}

// --- ALT BAR (GÜNCELLENDİ: stringResource KULLANIYOR) ---
@Composable
fun StudentBottomBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit,
    onCenterClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = ClubLightPurple
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ETİKETLER XML'DEN GELİYOR
                BottomBarItem(Icons.Default.Home, stringResource(R.string.events), true) { onIndexSelected(0) }
                BottomBarItem(Icons.Default.Explore, stringResource(R.string.explore), true) { onIndexSelected(1) }
                Spacer(modifier = Modifier.width(60.dp))
                // BURASI ÖNEMLİ: stringResource(R.string.clubs)
                BottomBarItem(Icons.Default.Groups, stringResource(R.string.clubs), true) { onIndexSelected(2) }
                BottomBarItem(Icons.Default.Person, stringResource(R.string.profile), true) { onIndexSelected(3) }
            }
        }

        Surface(
            modifier = Modifier.size(75.dp).align(Alignment.TopCenter).offset(y = 5.dp).clickable { onCenterClick() },
            shape = CircleShape, color = ClubDarkBlue, border = BorderStroke(5.dp, Color.White), shadowElevation = 8.dp
        ) {}
    }
}

// ... (Geri kalan EventCard ve DateSelectorCard aynı kalabilir, sadece stringResource'lara dikkat et)
@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(90.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SharedBgColor), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = ClubDarkBlue, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(text = event.clubName, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = ClubDarkBlue, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun BottomBarItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(imageVector = icon, contentDescription = label, tint = ClubDarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ClubDarkBlue)
    }
}

@Composable
fun DateSelectorCard() {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = ClubSurface) {
        Row(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Prev", tint = ClubDarkBlue)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "13", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = ClubDarkBlue)
                Text(text = stringResource(R.string.date_month), style = MaterialTheme.typography.bodyMedium, color = ClubDarkBlue.copy(alpha = 0.7f))
            }
            Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = ClubDarkBlue)
        }
    }
}