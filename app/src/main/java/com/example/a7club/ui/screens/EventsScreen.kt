package com.example.a7club.ui.screens

import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme._7ClubTheme
// ViewModel is now imported from its own package
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel, showSnackbar: (String) -> Unit) {
    val eventsState by viewModel.eventsState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { /* TODO: Navigate to Settings */ },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080))
                ) { Text("Ayarlar") }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { /* TODO: Navigate to Event Calendar */ },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080))
                ) { Text("Etkinlik Takvimi") }
            }
        }
    ) {
        EventsScreenContent(
            navController = navController,
            eventsState = eventsState,
            onRetry = viewModel::fetchEvents,
            showSnackbar = showSnackbar,
            onMenuClick = { scope.launch { drawerState.open() } }
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
    onMenuClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("TÜMÜ") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF3F1FF),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Menu") } },
                actions = { IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) { Icon(Icons.Default.Notifications, "Notifications") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFE8E5FF))
            )
        },
        bottomBar = { BottomNav(navController = navController, showSnackbar = showSnackbar) }
    ) { paddingValues ->
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    }) { Text("Tamam") }
                },
                dismissButton = { Button(onClick = { showDatePicker = false }) { Text("İptal") } }
            ) { DatePicker(state = datePickerState) }
        }

        Column(modifier = Modifier.padding(paddingValues)) {
            DateCard(
                date = selectedDate,
                onDateClick = { showDatePicker = true },
                onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) },
                onNextDayClick = { selectedDate = selectedDate.plusDays(1) }
            )
            SearchAndFilterBar(onSearchClick = { showSnackbar("Arama tıklandı") }, onFilterClick = { showSnackbar("Filtre tıklandı") })
            CategoryChips(selectedCategory = selectedCategory, onCategorySelected = { selectedCategory = it })

            when (eventsState) {
                is Resource.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                is Resource.Success -> {
                    val filteredByCategory = if (selectedCategory == "TÜMÜ") {
                        eventsState.data ?: emptyList()
                    } else {
                        eventsState.data?.filter { event ->
                            val categoryWords = selectedCategory.split(" ")
                            categoryWords.all { word -> event.clubName.contains(word, ignoreCase = true) }
                        } ?: emptyList()
                    }
                    val filteredByDate = filteredByCategory.filter { event ->
                        val eventDate = Instant.ofEpochMilli(event.startTime).atZone(ZoneId.systemDefault()).toLocalDate()
                        eventDate.isEqual(selectedDate)
                    }
                    if (filteredByDate.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(16.dp), Alignment.Center) {
                            Text("Bu tarihte hiç etkinlik bulunmuyor.")
                        }
                    } else {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(filteredByDate) { event ->
                                EventCard(event = event, onClick = { navController.navigate(Routes.EventDetail.createRoute(event.id)) })
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                        Text(text = eventsState.message ?: "Bilinmeyen bir hata oluştu.")
                        Button(onClick = onRetry) { Text("Yeniden Dene") }
                    }
                }
            }
        }
    }
}

@Composable
fun DateCard(date: LocalDate, onDateClick: () -> Unit, onPreviousDayClick: () -> Unit, onNextDayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E5FF))
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDayClick) { Icon(Icons.Default.ArrowBack, "Önceki Gün") }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onDateClick)) {
                Text(date.dayOfMonth.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(date.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("tr")), fontSize = 16.sp)
            }
            IconButton(onClick = onNextDayClick) { Icon(Icons.Default.ArrowForward, "Sonraki Gün") }
        }
    }
}

@Composable
fun SearchAndFilterBar(onSearchClick: () -> Unit, onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, "Arama", tint = Color.Gray) }
        IconButton(onClick = onFilterClick) { Icon(Icons.Default.Settings, "Filtrele", tint = Color.Gray) }
    }
}

@Composable
fun CategoryChips(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("TÜMÜ", "EKONOMİ", "HUKUK", "BİLİŞİM", "SANAT", "KÜLTÜR VE ETKİNLİK", "ATATÜRKÇÜ DÜŞÜNCE KULÜBÜ", "DANS KULÜBÜ")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        items(categories) { category ->
            SuggestionChip(
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (selectedCategory == category) Color(0xFFD6C5FF) else Color(0xFFE8E5FF),
                    labelColor = Color.Black
                ),
                border = null
            )
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E5FF))
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(text = "Kulüp: ${event.clubName}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = "Mekan: ${event.location}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun BottomNav(navController: NavController, showSnackbar: (String) -> Unit) {
    val density = LocalDensity.current
    val customBarShape = remember {
        GenericShape { size, _ ->
            val fabRadius = with(density) { 40.dp.toPx() }
            val fabMargin = with(density) { 8.dp.toPx() }
            val cutoutRadius = fabRadius + fabMargin
            val center = size.width / 2f
            moveTo(0f, 0f)
            lineTo(center - cutoutRadius, 0f)
            arcTo(
                rect = Rect(left = center - cutoutRadius, top = -cutoutRadius, right = center + cutoutRadius, bottom = cutoutRadius),
                startAngleDegrees = 180f, sweepAngleDegrees = -180f, forceMoveTo = false
            )
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
    }
    BottomAppBar(
        containerColor = Color(0xFFE8E5FF),
        modifier = Modifier.clip(customBarShape),
        contentPadding = PaddingValues(0.dp)
    ) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, "Ana Sayfa") })
        NavigationBarItem(selected = false, onClick = { showSnackbar("Keşfet") }, icon = { Icon(Icons.Default.Search, "Keşfet") })
        Spacer(Modifier.weight(1f))
        NavigationBarItem(selected = false, onClick = { showSnackbar("Kulüpler") }, icon = { Icon(Icons.Default.Person, "Kulüpler") })
        NavigationBarItem(selected = false, onClick = { showSnackbar("Profil") }, icon = { Icon(Icons.Default.Person, "Profil") })
    }
}

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    _7ClubTheme {
        EventsScreenContent(
            navController = rememberNavController(),
            eventsState = Resource.Success(data = emptyList()),
            onRetry = {},
            showSnackbar = {},
            onMenuClick = {}
        )
    }
}
