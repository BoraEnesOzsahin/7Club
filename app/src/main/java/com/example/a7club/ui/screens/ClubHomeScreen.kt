package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.a7club.data.models.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.*
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubHomeScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit,
    viewModel: StudentFlowViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val eventsState by viewModel.eventsState

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.7f),
                drawerContainerColor = VeryLightPurple
            ) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { 
                        navController.navigate(Routes.SettingsScreen.route)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) { Text("Ayarlar") }
            }
        }
    ) {
        Scaffold(
            containerColor = VeryLightPurple,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Etkinlikler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu", tint = DarkBlue) } },
                    actions = { 
                        IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) { 
                            Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue) 
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
                )
            },
            bottomBar = { 
                MainInitialBottomAppBar(navController = navController) 
            }
        ) { paddingValues ->
            ClubHomeContent(
                modifier = Modifier.padding(paddingValues),
                eventsState = eventsState,
                onRetry = viewModel::fetchEvents,
                onEventClick = { /* Aksiyon */ }
            )
        }
    }
}

@Composable
fun MainInitialBottomAppBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = LightPurple
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // GERİ GETİRİLDİ: Orijinal Öğrenci Görünümü butonları
                ClubMainNavItem(Icons.Default.Home, "Etkinlikler") { /* Zaten buradayız */ }
                ClubMainNavItem(Icons.Default.Explore, "Keşfet") { /* Keşfet Aksiyon */ }
                Spacer(modifier = Modifier.width(90.dp))
                ClubMainNavItem(Icons.Default.Groups, "Kulüpler") { navController.navigate(Routes.Clubs.route) }
                ClubMainNavItem(Icons.Default.Person, "Profil") { navController.navigate(Routes.Profile.route) }
            }
        }

        Surface(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clickable { 
                    // GERİ GETİRİLDİ: Orta tuş Yönetici Görünümüne geçiş yapar
                    navController.navigate(Routes.ClubProfileScreen.route) 
                },
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {}
    }
}

@Composable
fun ClubMainNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = label, tint = DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubHomeContent(
    modifier: Modifier = Modifier,
    eventsState: Resource<List<Event>>,
    onRetry: () -> Unit,
    onEventClick: (Event) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Business") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { Button(onClick = { 
                 datePickerState.selectedDateMillis?.let {
                    selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                showDatePicker = false
            }) { Text("Tamam") } },
            dismissButton = { Button(onClick = { showDatePicker = false }) { Text("İptal") } }
        ) { DatePicker(state = datePickerState) }
    }

    Column(modifier = modifier) {
        ClubDateCard(
            date = selectedDate,
            onDateClick = { showDatePicker = true },
            onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) },
            onNextDayClick = { selectedDate = selectedDate.plusDays(1) }
        )
        
        ClubCategoryChips(selectedCategory = selectedCategory, onCategorySelected = { selectedCategory = it })

        when (eventsState) {
            is Resource.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DarkBlue) }
            is Resource.Success -> {
                val filteredEvents = eventsState.data?.filter { event ->
                    val eventDate = Instant.ofEpochMilli(event.startTime).atZone(ZoneId.systemDefault()).toLocalDate()
                    eventDate.isEqual(selectedDate)
                } ?: emptyList()

                if (filteredEvents.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(16.dp), Alignment.Center) {
                        Text("Bu tarihte hiç etkinlik bulunmuyor.", color = DarkBlue)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(filteredEvents) { event ->
                            ClubEventCard(event = event, onClick = { onEventClick(event) })
                        }
                    }
                }
            }
            is Resource.Error -> {
                Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                    Text(text = eventsState.message ?: "Bilinmeyen bir hata oluştu.", color = DarkBlue)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRetry) { Text("Yeniden Dene") }
                }
            }
        }
    }
}

@Composable
fun ClubDateCard(date: LocalDate, onDateClick: () -> Unit, onPreviousDayClick: () -> Unit, onNextDayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).clickable(onClick = onDateClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDayClick) { Icon(Icons.Default.ArrowBack, "Önceki Gün", tint = DarkBlue) }
            Text(date.dayOfMonth.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Text(date.month.getDisplayName(TextStyle.FULL, Locale("tr")), fontSize = 16.sp, color = DarkBlue)
            IconButton(onClick = onNextDayClick) { Icon(Icons.Default.ArrowForward, "Sonraki Gün", tint = DarkBlue) }
        }
    }
}

@Composable
fun ClubCategoryChips(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = mapOf(
        "Business" to CategoryGreen,
        "Tech" to CategoryYellow,
        "Health" to CategoryBlue,
        "Art" to CategoryPink,
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(categories.keys.toList()) { category ->
            val backgroundColor = categories[category] ?: Color.Gray
            Button(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(selectedCategory == category) DarkBlue else backgroundColor,
                    contentColor = if(selectedCategory == category) Color.White else Color.Black
                )
            ) {
                Text(category)
            }
        }
    }
}

@Composable
fun ClubEventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.clubName, style = MaterialTheme.typography.bodyMedium, color = DarkBlue.copy(alpha = 0.7f))
        }
    }
}
