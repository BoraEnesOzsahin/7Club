@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.R
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
    authViewModel: AuthViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedBottomTabIndex by remember { mutableIntStateOf(0) }

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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(65.dp).clickable {
                                navController.navigate(Routes.SettingsScreen.route)
                                scope.launch { drawerState.close() }
                            },
                            color = DarkBlue,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 8.dp
                        ) { Box(contentAlignment = Alignment.Center) { Text("Ayarlar", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) } }

                        Surface(
                            modifier = Modifier.fillMaxWidth().height(65.dp).clickable { scope.launch { drawerState.close() } },
                            color = DarkBlue,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 8.dp
                        ) { Box(contentAlignment = Alignment.Center) { Text("Etkinlik Takvimi", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) } }
                    }
                    Spacer(Modifier.weight(1f))
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(DarkBlue.copy(alpha = 0.1f))) 
                }
            }
        }
    ) {
        Scaffold(
            containerColor = VeryLightPurple,
            topBar = {
                val title = when(selectedBottomTabIndex) {
                    0 -> "Etkinlikler"
                    1 -> "Keşfet"
                    2 -> "Kulüpler"
                    else -> "Profilim"
                }
                CenterAlignedTopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } },
                    actions = { IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) { Icon(Icons.Default.Notifications, "Notifications") } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
                )
            },
            bottomBar = {
                PersonnelMainBottomBar(
                    navController = navController,
                    selectedIndex = selectedBottomTabIndex,
                    onIndexSelected = { index -> selectedBottomTabIndex = index }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (selectedBottomTabIndex) {
                    0 -> PersonnelEventsTab(navController)
                    1 -> PersonnelDiscoverTab(navController)
                    2 -> PersonnelClubsTab(navController)
                    3 -> PersonnelProfileScreen(navController, authViewModel)
                }
            }
        }
    }
}

@Composable
fun PersonnelMainBottomBar(
    navController: NavController,
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit,
    onCenterClick: () -> Unit = {},
    initialIsExpanded: Boolean = false
) {
    var isMenuExpanded by remember { mutableStateOf(initialIsExpanded) }

    Box(
        modifier = Modifier.fillMaxWidth().height(110.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = LightPurple
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isMenuExpanded) {
                    PersonnelNavItem(Icons.Default.Home, "Etkinlikler", selectedIndex == 0) { onIndexSelected(0) }
                    PersonnelNavItem(Icons.Default.Explore, "Keşfet", selectedIndex == 1) { onIndexSelected(1) }
                    Spacer(modifier = Modifier.width(60.dp))
                    PersonnelNavItem(Icons.Default.Groups, "Kulüpler", selectedIndex == 2) { onIndexSelected(2) }
                    PersonnelNavItem(Icons.Default.Person, "Profil", selectedIndex == 3) { onIndexSelected(3) }
                } else {
                    PersonnelNavItem(Icons.Default.EventNote, "Yeni Etkinlik\nTalepleri", false) { 
                        navController.navigate(Routes.PersonnelEventRequests.route)
                    }
                    PersonnelNavItem(Icons.Default.AllInbox, "Tüm Etkinlik\nTalepleri", false) { }
                    Spacer(modifier = Modifier.width(60.dp))
                    PersonnelNavItem(Icons.Default.Diversity3, "Kulüpler", false) { 
                        isMenuExpanded = false
                        onIndexSelected(2) 
                    }
                    PersonnelNavItem(Icons.Default.History, "Geçmiş\nEtkinlikler", false) { 
                        navController.navigate(Routes.PersonnelPastEvents.route)
                    }
                }
            }
        }
        
        Surface(
            modifier = Modifier
                .size(85.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clickable { isMenuExpanded = !isMenuExpanded },
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {}
    }
}

@Composable
fun PersonnelEventsTab(
    navController: NavController,
    viewModel: PersonnelViewModel = viewModel()
) {
    val pendingEvents by viewModel.pendingEvents.collectAsState()
    var selectedCategory by remember { mutableStateOf("Tümü") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column {
        PersonnelHeaderDateCard(date = selectedDate, onDateClick = { }, onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) }, onNextDayClick = { selectedDate = selectedDate.plusDays(1) })
        PersonnelHeaderSearchBar(onSearchClick = { }, onFilterClick = { })
        
        val categories = listOf("Tümü", "Business", "Tech", "Health", "Art")
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Button(
                    onClick = { selectedCategory = category },
                    colors = ButtonDefaults.buttonColors(containerColor = if(isSelected) DarkBlue else Color(0xFFD1C4E9)),
                    shape = RoundedCornerShape(16.dp)
                ) { Text(text = category, color = if(isSelected) Color.White else DarkBlue) }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(pendingEvents) { event ->
                PersonnelListItemCard(
                    title = event.title,
                    clubName = event.clubName,
                    status = "Beklemede",
                    onClick = { navController.navigate(Routes.PersonnelEventDetail.createRoute(event.title, event.clubName)) }
                )
            }
            if (pendingEvents.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("Onay bekleyen yeni etkinlik yok.", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable fun PersonnelDiscoverTab(navController: NavController) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Keşfet Sayfası") } }
@Composable fun PersonnelClubsTab(navController: NavController) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Kulüpler Sayfası") } }

@Composable
fun PersonnelNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(75.dp).clickable(onClick = onClick).padding(vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = if(isSelected) Color.White else DarkBlue, modifier = Modifier.size(26.dp))
        Text(text = label, color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun PersonnelListItemCard(title: String, clubName: String, status: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = VeryLightPurple)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = title, fontWeight = FontWeight.Bold, color = DarkBlue)
                Surface(color = Color(0xFFFFCC80), shape = RoundedCornerShape(8.dp)) { Text(text = status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color(0xFFE65100), fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            }
            Text(text = "Kulüp: $clubName", color = DarkBlue.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun PersonnelHeaderDateCard(date: LocalDate, onDateClick: () -> Unit, onPreviousDayClick: () -> Unit, onNextDayClick: () -> Unit) {
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL", Locale("tr"))
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = LightPurple)) {
        Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onPreviousDayClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Önceki Gün") }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onDateClick)) {
                Text(date.dayOfMonth.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                Text(date.format(monthFormatter).replaceFirstChar { it.uppercase() }, fontSize = 16.sp, color = DarkBlue)
            }
            IconButton(onClick = onNextDayClick) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Sonraki Gün") }
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
