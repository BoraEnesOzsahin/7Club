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
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp)) { PersonnelWaveBackground(modifier = Modifier.align(Alignment.BottomCenter)) }
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
    initialIsExpanded: Boolean = false // Varsayılan durum eklendi
) {
    // initialIsExpanded ile sayfa ilk açıldığında durumun ne olacağını belirliyoruz
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
                    // İŞLEM MODU (FOTOĞRAFTAKİ GİBİ)
                    PersonnelNavItem(Icons.Default.EventNote, "Yeni Etkinlik\nTalepleri", false) { 
                        navController.navigate(Routes.PersonnelEventRequests.route)
                    }
                    PersonnelNavItem(Icons.Default.AllInbox, "Tüm Etkinlik\nTalepleri", false) { }
                    Spacer(modifier = Modifier.width(60.dp))
                    PersonnelNavItem(Icons.Default.Diversity3, "Kulüpler", false) { 
                        isMenuExpanded = false
                        onIndexSelected(2) 
                    }
                    PersonnelNavItem(Icons.Default.History, "Geçmiş\nEtkinlikler", false) { }
                }
            }
        }
        
        Surface(
            modifier = Modifier
                .size(85.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clickable { 
                    isMenuExpanded = !isMenuExpanded 
                    onCenterClick()
                },
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {}
    }
}

@Composable
fun PersonnelNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(75.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = label, 
            tint = DarkBlue, 
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label, 
            color = DarkBlue, 
            fontSize = 10.sp, 
            lineHeight = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PersonnelEventsTab(navController: NavController) {
    var selectedCategory by remember { mutableStateOf("Business") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { Button(onClick = { datePickerState.selectedDateMillis?.let { selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }; showDatePicker = false }) { Text("Tamam") } },
            dismissButton = { Button(onClick = { showDatePicker = false }) { Text("İptal") } }
        ) { DatePicker(state = datePickerState) }
    }

    Column {
        PersonnelHeaderDateCard(date = selectedDate, onDateClick = { showDatePicker = true }, onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) }, onNextDayClick = { selectedDate = selectedDate.plusDays(1) })
        PersonnelHeaderSearchBar(onSearchClick = { }, onFilterClick = { })
        PersonnelCategorySelector(selectedCategory = selectedCategory, onCategorySelected = { category -> selectedCategory = category })

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val items = when(selectedCategory) {
                "Business" -> listOf(PersonnelTaskData("Liderlik Zirvesi", "İşletme Kulübü", "Onay Bekliyor"), PersonnelTaskData("Kariyer 101", "Girişimcilik Kulübü", "Onay Bekliyor"))
                "Tech" -> listOf(PersonnelTaskData("Yapay Zeka Semineri", "Bilişim Kulübü", "Onay Bekliyor"), PersonnelTaskData("Hackathon 2025", "IEEE Kulübü", "Onay Bekliyor"))
                "Health" -> listOf(PersonnelTaskData("Sağlıklı Yaşam Yürüyüşü", "Spor Kulübü", "Onay Bekliyor"), PersonnelTaskData("Kan Bağışı Kampanyası", "Kızılay Kulübü", "Onay Bekliyor"))
                "Art" -> listOf(PersonnelTaskData("Modern Sanat Sergisi", "Sanat Kulübü", "Onay Bekliyor"), PersonnelTaskData("Tiyatro Gösterimi", "Tiyatro Kulübü", "Onay Bekliyor"))
                else -> emptyList()
            }
            items(items) { item ->
                PersonnelListItemCard(
                    title = item.title,
                    clubName = item.club,
                    status = item.status,
                    onClick = { navController.navigate(Routes.PersonnelEventDetail.createRoute(item.title, item.club)) }
                )
            }
        }
    }
}

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
                if (selectedTab == 0) { items(3) { PersonnelAnnouncementCard() } } else { items(2) { PersonnelPostCard() } }
            }
            Box(modifier = Modifier.padding(start = 8.dp).width(6.dp).fillMaxHeight(0.6f).clip(RoundedCornerShape(4.dp)).background(Color(0xFFD1C4E9)).align(Alignment.CenterVertically)) { Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(DarkBlue, RoundedCornerShape(4.dp))) }
        }
    }
}

@Composable
fun PersonnelClubsTab(navController: NavController) {
    val clubs = listOf(
        ClubItem("Kültür ve Etkinlik Kulübü", R.drawable.yukek_logo),
        ClubItem("Bilişim Kulübü", R.drawable.bilisim_logo),
        ClubItem("Psikoloji Kulübü", R.drawable.psikoloji_logo),
        ClubItem("Müzik Kulübü", R.drawable.muzik_logo),
        ClubItem("Uluslararası Ticaret Kulübü", R.drawable.ticaret_logo)
    )
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(clubs) { club ->
            Card(
                modifier = Modifier.fillMaxWidth().height(80.dp).clickable { navController.navigate(Routes.PersonnelClubDetail.createRoute(club.name)) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape)) { Image(painter = painterResource(id = club.logoRes), contentDescription = club.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = club.name, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 14.sp)
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
                Box(modifier = Modifier.size(24.dp).clip(CircleShape)) { Image(painter = painterResource(id = R.drawable.yukek_logo), contentDescription = "Logo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
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
                Box(modifier = Modifier.size(32.dp).clip(CircleShape)) { Image(painter = painterResource(id = R.drawable.yukek_logo), contentDescription = "Logo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Açıklama", color = Color.Black, fontSize = 14.sp)
            }
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
                Text(date.format(monthFormatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("tr")) else it.toString() }, fontSize = 16.sp, color = DarkBlue)
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

@Composable
fun PersonnelCategorySelector(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("Business" to Color(0xFFAED581), "Tech" to Color(0xFFFFF176), "Health" to Color(0xFFFFB74D), "Art" to Color(0xFFF8BBD0))
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
                Surface(color = Color(0xFFFFCC80), shape = RoundedCornerShape(8.dp)) { Text(text = status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color(0xFFE65100), fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Kulüp: $clubName", style = MaterialTheme.typography.bodyMedium, color = DarkBlue.copy(alpha = 0.8f))
        }
    }
}

data class PersonnelTaskData(val title: String, val club: String, val status: String)
data class ClubItem(val name: String, val logoRes: Int)
