@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    // Verileri Dinle
    val allCalendarEvents by personnelViewModel.allEventsForCalendar.collectAsState()
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
                    // --- DEĞİŞİKLİK BURADA: navigationIcon parametresi kaldırıldı ---
                    // Sol üstteki hamburger menü butonu artık yok.
                    actions = {
                        IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) {
                            Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
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
                    // TAB 0: ETKİNLİKLER
                    0 -> PersonnelEventsTab(navController, allCalendarEvents)
                    // TAB 1: KEŞFET
                    1 -> PersonnelDiscoverTab(navController)
                    // TAB 2: KULÜPLER
                    2 -> PersonnelClubsTab(navController, clubs)
                    // TAB 3: PROFİL
                    3 -> PersonnelProfileScreen(navController, authViewModel)
                }
            }
        }
    }
}

// ----------------------------------------------------
// SEKME 1: ETKİNLİKLER (AJANDA)
// ----------------------------------------------------
@Composable
fun PersonnelEventsTab(navController: NavController, allEvents: List<Event>) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Filtreleme: Seçilen güne göre
    val dailyEvents = allEvents.filter { event ->
        val eventTimestamp = event.timestamp
        if (eventTimestamp != null) {
            val eventDate = Instant.ofEpochMilli(eventTimestamp.seconds * 1000)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            eventDate.isEqual(selectedDate)
        } else {
            false
        }
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
        // Tarih Kartı
        PersonnelHeaderDateCard(
            date = selectedDate,
            onDateClick = { showDatePicker = true },
            onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) },
            onNextDayClick = { selectedDate = selectedDate.plusDays(1) }
        )

        // İstatistik Başlığı
        if(dailyEvents.isNotEmpty()){
            Text(
                text = "Toplam ${dailyEvents.size} Etkinlik",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
            if (dailyEvents.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.EventBusy, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Bu tarihte etkinlik yok.", color = Color.Gray)
                        }
                    }
                }
            } else {
                items(dailyEvents) { event ->
                    PersonnelAgendaCard(event = event) {
                        navController.navigate(Routes.PersonnelEventDetail.createRoute(event.title, event.clubName))
                    }
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
fun PersonnelAgendaCard(event: Event, onClick: () -> Unit) {
    val statusColor = when (event.status.uppercase()) {
        "APPROVED" -> Color(0xFF4CAF50)
        "PENDING" -> Color(0xFFFF9800)
        "REJECTED" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val statusText = when (event.status.uppercase()) {
        "APPROVED" -> "ONAYLI"
        "PENDING" -> "BEKLİYOR"
        "REJECTED" -> "REDDEDİLDİ"
        else -> event.status
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(statusColor)
            )

            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkBlue)
                    val timePart = event.dateString.split(" ").lastOrNull() ?: ""
                    Text(text = timePart, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = event.clubName, style = MaterialTheme.typography.bodyMedium, color = DarkBlue.copy(alpha = 0.7f), modifier = Modifier.weight(1f))
                    Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                        Text(text = statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
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