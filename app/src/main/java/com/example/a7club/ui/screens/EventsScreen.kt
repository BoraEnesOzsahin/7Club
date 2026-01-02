package com.example.a7club.ui.screens

import android.app.Activity
import android.content.Context
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.R
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

// --- TASARIM RENKLERİ ---
val TargetDarkBlue = Color(0xFF160092)
val TargetLightPurple = Color(0xFFCCC2FF)
val TargetBackground = Color(0xFFEEEBFF)

// --- KULÜP ADI ÇEVİRİ MANTIĞI ---
@Composable
fun getTranslatedClubName(clubName: String): String {
    val language = Locale.getDefault().language
    if (language != "en") return clubName

    return when {
        clubName.contains("EKONOMİ", ignoreCase = true) -> "ECONOMY AND FINANCE"
        clubName.contains("BİLİŞİM", ignoreCase = true) -> "I.T. CLUB"
        clubName.contains("HUKUK", ignoreCase = true) -> "LAW CLUB"
        clubName.contains("SANAT", ignoreCase = true) -> "ART CLUB"
        else -> clubName.replace("KULÜBÜ", "CLUB").replace("Kulübü", "Club")
    }
}

@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel, showSnackbar: (String) -> Unit) {
    val eventsState by viewModel.eventsState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isSettingsVisible by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = TargetBackground) {
                Column(Modifier.fillMaxWidth().padding(top = 80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    DrawerMenuButton(stringResource(R.string.settings)) {
                        isSettingsVisible = true
                        scope.launch { drawerState.close() }
                    }
                    Spacer(Modifier.height(20.dp))
                    DrawerMenuButton(stringResource(R.string.event_calendar)) {
                        isSettingsVisible = false
                        scope.launch { drawerState.close() }
                    }
                }
            }
        }
    ) {
        if (isSettingsVisible) {
            SettingsContent(onBackClick = { isSettingsVisible = false })
        } else {
            EventsScreenContent(navController, eventsState, { viewModel.fetchEvents() }, { scope.launch { drawerState.open() } })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreenContent(navController: NavController, eventsState: Resource<List<Event>>, onRetry: () -> Unit, onMenuClick: () -> Unit) {
    val allLabel = stringResource(R.string.cat_all)
    var selectedCategory by remember { mutableStateOf(allLabel) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        containerColor = TargetBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.events), fontWeight = FontWeight.Bold, color = TargetDarkBlue) },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, null, tint = TargetDarkBlue) } },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = TargetDarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TargetLightPurple)
            )
        },
        bottomBar = { CustomBottomBar() }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            DateCard(selectedDate, { selectedDate = selectedDate.minusDays(1) }, { selectedDate = selectedDate.plusDays(1) })
            CategoryChips(selectedCategory) { selectedCategory = it }
            EventListArea(eventsState, selectedCategory, selectedDate, navController, onRetry)
        }
    }
}

@Composable
fun CustomBottomBar() {
    Box(modifier = Modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(75.dp).shadow(15.dp, RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)),
            color = TargetLightPurple,
            shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
        ) {
            Row(Modifier.fillMaxSize().padding(horizontal = 20.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                BottomNavItem(Icons.Default.Home, stringResource(R.string.events))
                BottomNavItem(Icons.Default.Explore, stringResource(R.string.explore))
                Spacer(Modifier.width(50.dp))
                BottomNavItem(Icons.Default.Groups, stringResource(R.string.clubs))
                BottomNavItem(Icons.Default.Person, stringResource(R.string.profile))
            }
        }
        // ORTA BUTON (ARTI KALDIRILDI)
        Box(
            modifier = Modifier
                .offset(y = (-35).dp)
                .size(60.dp)
                .background(TargetBackground, CircleShape)
                .padding(5.dp)
                .background(TargetDarkBlue, CircleShape)
        )
    }
}

@Composable
fun EventListArea(state: Resource<List<Event>>, cat: String, date: LocalDate, nav: NavController, retry: () -> Unit) {
    val clubLabel = stringResource(R.string.club_label)
    val allLabel = stringResource(R.string.cat_all)

    when (state) {
        is Resource.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = TargetDarkBlue) }
        is Resource.Success -> {
            val list = state.data?.filter { event ->
                val matchesDate = Instant.ofEpochMilli(event.startTime).atZone(ZoneId.systemDefault()).toLocalDate() == date
                val matchesCat = if (cat == allLabel) true else event.clubName.contains(cat, ignoreCase = true)
                matchesDate && matchesCat
            } ?: emptyList()

            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {
                items(list) { event ->
                    Card(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { nav.navigate(Routes.EventDetail.createRoute(event.id)) },
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(event.title, fontWeight = FontWeight.Bold, color = TargetDarkBlue, fontSize = 16.sp)
                            // KULÜP ADI İNGİLİZCEYE ÇEVRİLİYOR
                            Text("$clubLabel ${getTranslatedClubName(event.clubName)}", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        else -> Box(Modifier.fillMaxSize(), Alignment.Center) { Button(onClick = retry) { Text("Hata Oluştu") } }
    }
}

@Composable
fun CategoryChips(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf(
        stringResource(R.string.cat_all), stringResource(R.string.cat_economy),
        stringResource(R.string.cat_law), stringResource(R.string.cat_it),
        stringResource(R.string.cat_music), stringResource(R.string.cat_sports)
    )
    LazyRow(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(categories) { category ->
            SuggestionChip(
                onClick = { onSelect(category) },
                label = { Text(category) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (selected == category) TargetDarkBlue else Color.Transparent,
                    labelColor = if (selected == category) Color.White else TargetDarkBlue
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = TargetDarkBlue, borderWidth = 1.dp)
            )
        }
    }
}

@Composable
fun DateCard(date: LocalDate, onPrev: () -> Unit, onNext: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("LLLL", Locale.getDefault())
    Card(Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(TargetLightPurple), shape = RoundedCornerShape(24.dp)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onClick = onPrev) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TargetDarkBlue) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(date.dayOfMonth.toString(), fontSize = 34.sp, fontWeight = FontWeight.Bold, color = TargetDarkBlue)
                Text(date.format(formatter).replaceFirstChar { it.uppercase() }, color = TargetDarkBlue)
            }
            IconButton(onClick = onNext) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TargetDarkBlue) }
        }
    }
}

@Composable
fun DrawerMenuButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(56.dp), colors = ButtonDefaults.buttonColors(TargetDarkBlue), shape = RoundedCornerShape(18.dp)) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = TargetDarkBlue, modifier = Modifier.size(26.dp))
        Text(label, fontSize = 11.sp, color = TargetDarkBlue)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(onBackClick: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        containerColor = TargetBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold, color = TargetDarkBlue) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TargetDarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TargetLightPurple)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Button(onClick = { updateLocale(context, "en") }, colors = ButtonDefaults.buttonColors(TargetDarkBlue)) { Text("English") }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { updateLocale(context, "tr") }, colors = ButtonDefaults.buttonColors(TargetDarkBlue)) { Text("Türkçe") }
        }
    }
}

fun updateLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
    (context as? Activity)?.recreate()
}