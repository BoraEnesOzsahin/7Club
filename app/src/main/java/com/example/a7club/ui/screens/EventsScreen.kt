package com.example.a7club.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// RENK PALETİ - GÖRSELDEN ALINAN TAM KODLAR
val TargetDarkBlue = Color(0xFF160092)
val TargetLightPurple = Color(0xFFCCC2FF)
val TargetBackground = Color(0xFFEEEBFF)
val TargetWaveMiddle = Color(0xFF775CFF)
val TargetWaveSoft = Color(0xFFE6E3F6)

@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: StudentFlowViewModel,
    showSnackbar: (String) -> Unit
) {
    val eventsState by viewModel.eventsState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isSettingsVisible by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = TargetBackground,
                modifier = Modifier.fillMaxWidth(0.85f).fillMaxHeight(),
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DrawerMenuButton("Ayarlar") {
                            isSettingsVisible = true
                            scope.launch { drawerState.close() }
                        }
                        Spacer(Modifier.height(20.dp))
                        DrawerMenuButton("Etkinlik Takvimi") {
                            isSettingsVisible = false
                            scope.launch { drawerState.close() }
                        }
                    }
                    // TAM UYARLANMIŞ KATMANLI DALGALAR
                    TargetWaveFooter(Modifier.align(Alignment.BottomCenter))
                }
            }
        }
    ) {
        if (isSettingsVisible) {
            SettingsContent(onBackClick = { isSettingsVisible = false })
        } else {
            EventsScreenContent(
                navController = navController,
                eventsState = eventsState,
                onRetry = { viewModel.fetchEvents() },
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }
    }
}

@Composable
fun TargetWaveFooter(modifier: Modifier = Modifier) {
    // Görseldeki 3 farklı tonu ve kavisli yapıyı oluşturmak için Canvas kullanıyoruz
    Canvas(modifier = modifier.fillMaxWidth().height(220.dp)) {
        val width = size.width
        val height = size.height

        // 1. Katman: En alttaki açık dalga (Açık Mavi/Mor tonu)
        val path1 = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.45f)
            quadraticBezierTo(width * 0.25f, height * 0.35f, width * 0.5f, height * 0.55f)
            quadraticBezierTo(width * 0.75f, height * 0.75f, width, height * 0.5f)
            lineTo(width, height)
            close()
        }
        drawPath(path1, color = TargetWaveSoft)

        // 2. Katman: Orta dalga (Canlı Mor tonu - #775CFF)
        val path2 = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.65f)
            quadraticBezierTo(width * 0.35f, height * 0.5f, width * 0.65f, height * 0.8f)
            quadraticBezierTo(width * 0.85f, height * 0.95f, width, height * 0.75f)
            lineTo(width, height)
            close()
        }
        drawPath(path2, color = TargetWaveMiddle)

        // 3. Katman: En üstteki koyu dalga (Koyu Lacivert - #160092)
        val path3 = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.82f)
            quadraticBezierTo(width * 0.45f, height * 0.75f, width * 0.75f, height * 0.92f)
            quadraticBezierTo(width * 0.9f, height * 0.98f, width, height * 0.88f)
            lineTo(width, height)
            close()
        }
        drawPath(path3, color = TargetDarkBlue)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreenContent(
    navController: NavController,
    eventsState: Resource<List<Event>>,
    onRetry: () -> Unit,
    onMenuClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("TÜMÜ") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        containerColor = TargetBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold, color = TargetDarkBlue) },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, null, tint = TargetDarkBlue) } },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = TargetDarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TargetLightPurple)
            )
        },
        bottomBar = { CustomBottomBar() }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            DateCard(selectedDate, { selectedDate = selectedDate.minusDays(1) }, { selectedDate = selectedDate.plusDays(1) })
            CategoryChips(selectedCategory) { selectedCategory = it }
            EventListArea(eventsState, selectedCategory, selectedDate, navController, onRetry)
        }
    }
}

@Composable
fun CustomBottomBar() {
    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(85.dp).shadow(12.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
            color = TargetLightPurple,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp, vertical = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                BottomNavItem(Icons.Default.Home, "Etkinlikler")
                BottomNavItem(Icons.Default.Explore, "Keşfet")
                Spacer(modifier = Modifier.width(60.dp))
                BottomNavItem(Icons.Default.Groups, "Kulüpler")
                BottomNavItem(Icons.Default.Person, "Profil")
            }
        }
        Box(modifier = Modifier.offset(y = (-45).dp).size(65.dp).background(TargetBackground, CircleShape).padding(6.dp).background(TargetDarkBlue, CircleShape))
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = TargetDarkBlue, modifier = Modifier.size(26.dp))
        Text(label, fontSize = 11.sp, color = TargetDarkBlue, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CategoryChips(selected: String, onSelect: (String) -> Unit) {
    val cats = listOf("TÜMÜ", "EKONOMİ", "HUKUK", "BİLİŞİM")
    LazyRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(cats) { cat ->
            SuggestionChip(
                onClick = { onSelect(cat) },
                label = { Text(cat) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (selected == cat) TargetDarkBlue else Color.Transparent,
                    labelColor = if (selected == cat) Color.White else TargetDarkBlue
                )
            )
        }
    }
}

// DİĞER FONKSİYONLAR (SettingsContent, LanguageButton, EventListArea, DrawerMenuButton, DateCard) AYNI ŞEKİLDE KORUNMUŞTUR.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(onBackClick: () -> Unit) {
    var selectedLanguage by remember { mutableStateOf("Türkçe") }
    Scaffold(
        containerColor = TargetBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ayarlar", fontWeight = FontWeight.Bold, color = TargetDarkBlue) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TargetDarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TargetLightPurple)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Dil Seçimi", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TargetDarkBlue)
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                LanguageButton("İngilizce", selectedLanguage == "İngilizce") { selectedLanguage = "İngilizce" }
                Spacer(Modifier.width(16.dp))
                LanguageButton("Türkçe", selectedLanguage == "Türkçe") { selectedLanguage = "Türkçe" }
            }
        }
    }
}

@Composable
fun LanguageButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) TargetDarkBlue else TargetLightPurple),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(130.dp)
    ) { Text(text, color = if (isSelected) Color.White else TargetDarkBlue) }
}

@Composable
fun EventListArea(state: Resource<List<Event>>, cat: String, date: LocalDate, nav: NavController, retry: () -> Unit) {
    when (state) {
        is Resource.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = TargetDarkBlue) }
        is Resource.Success -> {
            val list = state.data?.filter {
                Instant.ofEpochMilli(it.startTime).atZone(ZoneId.systemDefault()).toLocalDate() == date && (cat == "TÜMÜ" || it.clubName.contains(cat, true))
            } ?: emptyList()
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {
                items(list) { event ->
                    Card(Modifier.fillMaxWidth().padding(8.dp).clickable { nav.navigate(Routes.EventDetail.createRoute(event.id)) }, colors = CardDefaults.cardColors(Color.White)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(event.title, fontWeight = FontWeight.Bold, color = TargetDarkBlue)
                            Text("Kulüp: ${event.clubName}", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
        else -> Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) { Button(onClick = retry) { Text("Dene") } }
    }
}

@Composable
fun DrawerMenuButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(56.dp).shadow(8.dp, RoundedCornerShape(18.dp)), colors = ButtonDefaults.buttonColors(TargetDarkBlue), shape = RoundedCornerShape(18.dp)) { Text(text, color = Color.White, fontWeight = FontWeight.Bold) }
}

@Composable
fun DateCard(date: LocalDate, onPrev: () -> Unit, onNext: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("LLLL", Locale("tr"))
    Card(Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(TargetLightPurple)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onClick = onPrev) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TargetDarkBlue) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(date.dayOfMonth.toString(), fontSize = 34.sp, fontWeight = FontWeight.Bold, color = TargetDarkBlue)
                Text(date.format(formatter), color = TargetDarkBlue)
            }
            IconButton(onClick = onNext) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TargetDarkBlue) }
        }
    }
}