package com.example.a7club.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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

    var showJoinDialog by remember { mutableStateOf<Event?>(null) }

    if (showJoinDialog != null) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = null },
            title = { Text(text = "Onay", color = DarkBlue) },
            text = { Text("${showJoinDialog?.title} etkinliğine katılmak istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = { 
                        showSnackbar("${showJoinDialog?.title} etkinliğine katıldınız!")
                        showJoinDialog = null 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) { Text("Evet") }
            },
            dismissButton = {
                 Button(
                    onClick = { showJoinDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = LightPurple)
                ) { Text("Hayır", color = DarkBlue) }
            },
            containerColor = VeryLightPurple
        )
    }

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
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { 
                        navController.navigate(Routes.EventCalendarScreen.route)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) { Text("Etkinlik Takvimi") }
            }
        }
    ) {
        Scaffold(
            containerColor = VeryLightPurple,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Etkinlikler", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } },
                    actions = { 
                        // UPDATED: Navigate to NotificationsScreen
                        IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) { 
                            Icon(Icons.Default.Notifications, "Notifications") 
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
                )
            },
            bottomBar = { ClubBottomAppBar(navController = navController) },
            floatingActionButton = { FloatingActionButton(onClick = {}, shape = CircleShape, containerColor = DarkBlue) {} },
            floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            ClubHomeContent(
                modifier = Modifier.padding(paddingValues),
                eventsState = eventsState,
                onRetry = viewModel::fetchEvents,
                onEventClick = { event -> showJoinDialog = event }
            )
        }
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
        Row(Modifier.padding(horizontal = 16.dp)) {
            Icon(Icons.Default.Search, "Search", tint = Color.Gray)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Tune, "Filter", tint = Color.Gray) 
        }
        
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
                        Text("Bu tarihte hiç etkinlik bulunmuyor.")
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
                    Text(text = eventsState.message ?: "Bilinmeyen bir hata oluştu.")
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
            IconButton(onClick = onPreviousDayClick) { Icon(Icons.Default.ArrowBack, "Önceki Gün") }
            Text(date.dayOfMonth.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Text(date.month.getDisplayName(TextStyle.FULL, Locale("tr")), fontSize = 16.sp, color = DarkBlue)
            IconButton(onClick = onNextDayClick) { Icon(Icons.Default.ArrowForward, "Sonraki Gün") }
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
                    containerColor = backgroundColor,
                    contentColor = if(selectedCategory == category) Color.White else Color.Black
                ),
                elevation = if(selectedCategory == category) ButtonDefaults.buttonElevation(4.dp) else ButtonDefaults.buttonElevation(0.dp)
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
            Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.clubName, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ClubBottomAppBar(navController: NavController) {
    BottomAppBar(containerColor = LightPurple, contentColor = DarkBlue) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, "Ana Sayfa") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = DarkBlue))
        NavigationBarItem(selected = false, onClick = { /* to Duyurular */ }, icon = { Icon(Icons.Default.Campaign, "Duyurular") }, colors = NavigationBarItemDefaults.colors(unselectedIconColor = DarkBlue.copy(alpha = 0.7f)))
        Spacer(Modifier.weight(1f))
        NavigationBarItem(selected = false, onClick = { /* to Kulüpler */ }, icon = { Icon(Icons.Default.Groups, "Kulüpler") }, colors = NavigationBarItemDefaults.colors(unselectedIconColor = DarkBlue.copy(alpha = 0.7f)))
        NavigationBarItem(
            selected = false, 
            onClick = { navController.navigate(Routes.ClubProfileScreen.route) },
            icon = { Icon(Icons.Default.Person, "Profilim") }, 
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = DarkBlue.copy(alpha = 0.7f))
        )
    }
}

// Corrected Preview: Now calls the stateless ClubHomeContent directly with fake data.
@Preview(showBackground = true, name = "Club Home Screen - Success")
@Composable
fun ClubHomeScreenPreview_Success() {
    _7ClubTheme {
        ClubHomeContent(
            eventsState = Resource.Success(
                listOf(
                    Event(id = "1", title = "Yaratıcı Yazarlık Atölyesi", clubName = "Kültür ve Etkinlik Kulübü", location = "B-101", startTime = System.currentTimeMillis()),
                    Event(id = "2", title = "Müzik Kulübü Karaoke Gecesi", clubName = "Müzik Kulübü", location = "Konferans Salonu", startTime = System.currentTimeMillis()),
                    Event(id = "3", title = "X Kulübü Y Etkinliği", clubName = "X Kulübü", location = "Amfi Tiyatro", startTime = System.currentTimeMillis())
                )
            ),
            onRetry = {},
            onEventClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Club Home Screen - Empty")
@Composable
fun ClubHomeScreenPreview_Empty() {
    _7ClubTheme {
        ClubHomeContent(
            eventsState = Resource.Success(emptyList()),
            onRetry = {},
            onEventClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Club Home Screen - Loading")
@Composable
fun ClubHomeScreenPreview_Loading() {
    _7ClubTheme {
        ClubHomeContent(
            eventsState = Resource.Loading(),
            onRetry = {},
            onEventClick = {}
        )
    }
}
