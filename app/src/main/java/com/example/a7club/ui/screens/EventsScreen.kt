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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.a7club.model.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.viewmodels.AuthViewModel
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    navController: NavController, 
    viewModel: StudentFlowViewModel, 
    showSnackbar: (String) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val eventsState by viewModel.eventsState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // DÜZELTME: Artık State'i ViewModel'den alıyoruz (Pürüzsüz Navigasyon için)
    val selectedTab by viewModel.selectedTab

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = VeryLightPurple,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Spacer(Modifier.height(24.dp))
                Text("Menü", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall, color = DarkBlue, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = LightPurple)
                Spacer(Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text("Ayarlar") },
                    selected = false,
                    onClick = { navController.navigate(Routes.SettingsScreen.route); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Settings, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Oturumu Kapat") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            authViewModel.resetLoginState()
                            navController.navigate(Routes.RoleSelection.route) { popUpTo(0) { inclusive = true } }
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        EventsScreenContent(
            navController = navController,
            eventsState = eventsState,
            onRetry = { viewModel.fetchEvents() },
            showSnackbar = showSnackbar,
            onMenuClick = { scope.launch { drawerState.open() } },
            selectedTab = selectedTab,
            onTabSelected = { viewModel.selectedTab.intValue = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreenContent(
    navController: NavController,
    eventsState: Resource<List<Event>>,
    onRetry: () -> Unit,
    showSnackbar: (String) -> Unit,
    onMenuClick: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("TÜMÜ") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = VeryLightPurple,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if(selectedTab == 0) "Etkinlikler" else "Keşfet", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Menu") } },
                actions = { IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) { Icon(Icons.Default.Notifications, "Notifications") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = { 
            StudentMainBottomAppBar(
                navController = navController, 
                selectedIndex = selectedTab, 
                onTabSelected = onTabSelected 
            ) 
        }
    ) { paddingValues ->
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                        showDatePicker = false
                    }) { Text("Tamam") }
                }
            ) { DatePicker(state = datePickerState) }
        }

        Box(modifier = Modifier.padding(paddingValues)) {
            if (selectedTab == 0) {
                Column {
                    DateCard(date = selectedDate, onDateClick = { showDatePicker = true }, onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) }, onNextDayClick = { selectedDate = selectedDate.plusDays(1) })
                    SearchAndFilterBar(onSearchClick = { }, onFilterClick = { })
                    CategoryChips(selectedCategory = selectedCategory, onCategorySelected = { selectedCategory = it })

                    when (eventsState) {
                        is Resource.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        is Resource.Success -> {
                            val filteredByCategory = if (selectedCategory == "TÜMÜ") {
                                eventsState.data ?: emptyList()
                            } else {
                                eventsState.data?.filter { it.clubName.contains(selectedCategory, ignoreCase = true) } ?: emptyList()
                            }
                            
                            val filteredByDate = filteredByCategory.filter { event ->
                                event.timestamp?.let { 
                                    val eventDate = Instant.ofEpochMilli(it.seconds * 1000).atZone(ZoneId.systemDefault()).toLocalDate()
                                    eventDate.isEqual(selectedDate)
                                } ?: false
                            }

                            if (filteredByDate.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Bu tarihte hiç etkinlik bulunmuyor.") }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(filteredByDate) { event ->
                                        EventCard(event = event, onClick = { navController.navigate(Routes.EventDetail.createRoute(event.id)) })
                                    }
                                }
                            }
                        }
                        is Resource.Error -> {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Hata oluştu.")
                                Button(onClick = onRetry) { Text("Yeniden Dene") }
                            }
                        }
                    }
                }
            } else {
                DiscoverTabContent()
            }
        }
    }
}

@Composable
fun DiscoverTabContent() {
    var discoverSubTab by rememberSaveable { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { discoverSubTab = 0 }, colors = ButtonDefaults.buttonColors(containerColor = if (discoverSubTab == 0) DarkBlue else Color(0xFFD1C4E9)), modifier = Modifier.weight(1f)) { Text("Duyurular") }
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = { discoverSubTab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = if (discoverSubTab == 1) DarkBlue else Color(0xFFD1C4E9)), modifier = Modifier.weight(1f)) { Text("Gönderiler") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (discoverSubTab == 0) {
                items(5) { AnnouncementCard() }
            } else {
                items(3) { PostCard() }
            }
        }
    }
}

@Composable
fun AnnouncementCard() {
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
fun PostCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Color(0xFFD1C4E9)), contentAlignment = Alignment.Center) { Text("Görsel") }
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Kulüp Gönderisi Açıklaması", color = Color.Black, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun StudentMainBottomAppBar(navController: NavController, selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(modifier = Modifier.fillMaxWidth().height(75.dp).clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)), color = LightPurple) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                StudentNavItem(Icons.Default.Home, "Etkinlikler", selectedIndex == 0) { onTabSelected(0) }
                StudentNavItem(Icons.Default.Explore, "Keşfet", selectedIndex == 1) { onTabSelected(1) }
                Spacer(modifier = Modifier.width(90.dp))
                StudentNavItem(Icons.Default.Groups, "Kulüpler", false) { 
                    navController.navigate(Routes.Clubs.route) { launchSingleTop = true }
                }
                StudentNavItem(Icons.Default.Person, "Profil", false) { 
                    navController.navigate(Routes.Profile.route) { launchSingleTop = true }
                }
            }
        }
        Surface(modifier = Modifier.size(90.dp).align(Alignment.TopCenter).border(6.dp, Color.White, CircleShape).clickable { 
            navController.navigate(Routes.ClubProfileScreen.route) { launchSingleTop = true }
        }, shape = CircleShape, color = DarkBlue, shadowElevation = 8.dp) {}
    }
}

@Composable
fun StudentNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, contentDescription = label, tint = if (isSelected) Color.White else DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DateCard(date: LocalDate, onDateClick: () -> Unit, onPreviousDayClick: () -> Unit, onNextDayClick: () -> Unit) {
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL", Locale("tr"))
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = LightPurple)) {
        Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onPreviousDayClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Önceki") }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onDateClick)) {
                Text(date.dayOfMonth.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(date.format(monthFormatter).replaceFirstChar { it.uppercase() }, fontSize = 16.sp)
            }
            IconButton(onClick = onNextDayClick) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Sonraki") }
        }
    }
}

@Composable
fun SearchAndFilterBar(onSearchClick: () -> Unit, onFilterClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, "Arama") }
        IconButton(onClick = onFilterClick) { Icon(Icons.Default.Settings, "Filtrele") }
    }
}

@Composable
fun CategoryChips(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("TÜMÜ", "EKONOMİ", "HUKUK", "BİLİŞİM", "SANAT", "KÜLTÜR VE ETKİNLİK")
    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
        items(categories) { category ->
            SuggestionChip(onClick = { onCategorySelected(category) }, label = { Text(category) }, colors = SuggestionChipDefaults.suggestionChipColors(containerColor = if (selectedCategory == category) LightPurple else VeryLightPurple))
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = VeryLightPurple)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Kulüp: ${event.clubName}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Mekan: ${event.location}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
