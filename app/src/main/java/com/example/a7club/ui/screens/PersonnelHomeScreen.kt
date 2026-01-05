@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.R
import com.example.a7club.model.Club
import com.example.a7club.model.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.viewmodels.AuthViewModel
import com.example.a7club.ui.viewmodels.PersonnelViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PersonnelHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    initialTabIndex: Int = 0,
    personnelViewModel: PersonnelViewModel = viewModel()
) {
    val recentEvents by personnelViewModel.recentPendingEvents.collectAsState()
    val clubs by personnelViewModel.clubs.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedBottomTabIndex by remember { mutableIntStateOf(initialTabIndex) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = VeryLightPurple,
                modifier = Modifier.fillMaxWidth(0.72f),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(Modifier.weight(1f))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        DrawerMenuButton("Ayarlar") {
                            navController.navigate(Routes.SettingsScreen.route)
                            scope.launch { drawerState.close() }
                        }
                        DrawerMenuButton("Etkinlik Takvimi") {
                            scope.launch { drawerState.close() }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(LightPurple.copy(alpha = 0.3f)))
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                val title = when(selectedBottomTabIndex) {
                    0 -> "Etkinlikler"
                    1 -> "Keşfet"
                    2 -> "Kulüpler"
                    else -> "Profilim"
                }
                CenterAlignedTopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold, color = DarkBlue) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu", tint = DarkBlue) } },
                    actions = { IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) { Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue) } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            // GÜNCEL: 1. Mod (Ana Sayfa) Alt Barı
            bottomBar = {
                PersonnelHomeBottomBar(
                    navController = navController,
                    selectedIndex = selectedBottomTabIndex,
                    onIndexSelected = { index -> selectedBottomTabIndex = index }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (selectedBottomTabIndex) {
                    0 -> PersonnelEventsTab(navController, recentEvents)
                    1 -> PersonnelDiscoverTab(navController)
                    2 -> PersonnelClubsTab(navController, clubs)
                    3 -> PersonnelProfileScreen(navController, authViewModel)
                }
            }
        }
    }
}

// ----------------------------------------------------
// SEKME 1: ETKİNLİKLER (Events)
// ----------------------------------------------------
@Composable
fun PersonnelEventsTab(navController: NavController, events: List<Event>) {
    var selectedCategory by remember { mutableStateOf("Tümü") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val filteredEvents = if (selectedCategory == "Tümü") {
        events
    } else {
        events.filter { it.category == selectedCategory || it.category.isBlank() }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { Button(onClick = { datePickerState.selectedDateMillis?.let { selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }; showDatePicker = false }) { Text("Tamam") } },
            dismissButton = { Button(onClick = { showDatePicker = false }) { Text("İptal") } }
        ) { DatePicker(state = datePickerState) }
    }

    Column {
        PersonnelHeaderDateCard(
            date = selectedDate,
            onDateClick = { showDatePicker = true },
            onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) },
            onNextDayClick = { selectedDate = selectedDate.plusDays(1) }
        )
        PersonnelHeaderSearchBar(onSearchClick = { }, onFilterClick = { })
        PersonnelCategorySelector(selectedCategory = selectedCategory, onCategorySelected = { category -> selectedCategory = category })

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (events.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("Bekleyen etkinlik yok.", color = Color.Gray)
                    }
                }
            } else {
                items(filteredEvents) { event ->
                    PersonnelListItemCard(
                        title = event.title,
                        clubName = event.clubName,
                        status = "Onay Bekliyor",
                        onClick = { navController.navigate(Routes.PersonnelEventDetail.createRoute(event.title, event.clubName)) }
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// SEKME 2: KEŞFET (Discover)
// ----------------------------------------------------
@Composable
fun PersonnelDiscoverTab(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = { selectedTab = 0 }, colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 0) DarkBlue else Color(0xFFD1C4E9), contentColor = if (selectedTab == 0) Color.White else DarkBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f).height(40.dp)) { Text("Duyurular", fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = { selectedTab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 1) DarkBlue else Color(0xFFD1C4E9), contentColor = if (selectedTab == 1) Color.White else DarkBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f).height(40.dp)) { Text("Gönderiler", fontWeight = FontWeight.Bold) }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                if (selectedTab == 0) {
                    items(3) { PersonnelAnnouncementCard() }
                } else {
                    items(2) { PersonnelPostCard() }
                }
            }
            Box(modifier = Modifier.padding(start = 8.dp).width(6.dp).fillMaxHeight(0.6f).clip(RoundedCornerShape(4.dp)).background(Color(0xFFD1C4E9)).align(Alignment.CenterVertically)) {
                Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(DarkBlue, RoundedCornerShape(4.dp)))
            }
        }
    }
}

// ----------------------------------------------------
// SEKME 3: KULÜPLER (Clubs)
// ----------------------------------------------------
@Composable
fun PersonnelClubsTab(navController: NavController, clubs: List<Club>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (clubs.isEmpty()) {
            item { Text("Henüz kulüp bulunmamaktadır.", modifier = Modifier.padding(16.dp)) }
        }
        items(clubs) { club ->
            Card(
                modifier = Modifier.fillMaxWidth().height(80.dp).clickable { navController.navigate(Routes.PersonnelClubDetail.createRoute(club.name)) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        Text(club.name.take(1).uppercase(), color = DarkBlue, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = club.name, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 14.sp)
                }
            }
        }
    }
}

// ----------------------------------------------------
// YARDIMCI BİLEŞENLER
// ----------------------------------------------------

@Composable
fun DrawerMenuButton(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(65.dp).clickable(onClick = onClick).padding(horizontal = 24.dp),
        color = DarkBlue,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PersonnelHeaderDateCard(date: LocalDate, onDateClick: () -> Unit, onPreviousDayClick: () -> Unit, onNextDayClick: () -> Unit) {
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL", Locale("tr"))
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = LightPurple)) {
        Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onPreviousDayClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Önceki Gün", tint = DarkBlue) }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onDateClick)) {
                Text(date.dayOfMonth.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                Text(date.format(monthFormatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("tr")) else it.toString() }, fontSize = 16.sp, color = DarkBlue)
            }
            IconButton(onClick = onNextDayClick) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Sonraki Gün", tint = DarkBlue) }
        }
    }
}

@Composable
fun PersonnelHeaderSearchBar(onSearchClick: () -> Unit, onFilterClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, "Arama", tint = DarkBlue) }
        IconButton(onClick = onFilterClick) { Icon(Icons.Default.Tune, "Filtrele", tint = DarkBlue) }
    }
}

@Composable
fun PersonnelCategorySelector(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("Tümü" to Color(0xFFE0E0E0), "Business" to Color(0xFFAED581), "Tech" to Color(0xFFFFF176), "Health" to Color(0xFFFFB74D), "Art" to Color(0xFFF8BBD0))
    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(categories) { (name, color) ->
            val isSelected = selectedCategory == name
            Button(
                onClick = { onCategorySelected(name) },
                colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.Black),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(40.dp).then(if(isSelected) Modifier.border(2.dp, DarkBlue, RoundedCornerShape(16.dp)) else Modifier)
            ) { Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
        }
    }
}

@Composable
fun PersonnelListItemCard(title: String, clubName: String, status: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = VeryLightPurple)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkBlue)
                Surface(color = Color(0xFFFFCC80), shape = RoundedCornerShape(8.dp)) {
                    Text(text = status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color(0xFFE65100), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Kulüp: $clubName", style = MaterialTheme.typography.bodyMedium, color = DarkBlue.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun PersonnelAnnouncementCard() {
    Card(modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.White))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kültür ve Etkinlik Kulübü", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(0.9f).height(10.dp).background(Color(0xFFD1C4E9), RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth(0.6f).height(10.dp).background(Color(0xFFD1C4E9), RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
fun PersonnelPostCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Color(0xFFD1C4E9)), contentAlignment = Alignment.Center) { Text("Fotoğraf", color = DarkBlue) }
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFD1C4E9).copy(alpha = 0.5f)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Açıklama", color = Color.Black, fontSize = 14.sp)
            }
        }
    }
}