package com.example.a7club.ui.screens


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.theme._7ClubTheme
import kotlinx.coroutines.launch
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// This is a stateful composable that holds the logic
@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel, showSnackbar: (String) -> Unit) {
    val eventsState by viewModel.eventsState

    // The drawer state is managed here
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- RENK PALETİ TANIMLAMALARI ---
    val DeepBlueColor = Color(0xFF160092)      // #160092 - Koyu Lacivert (Butonlar)
    val DrawerBackgroundColor = Color(0xFFEEEBFF) // #EEEBFF - Çekmece Arka Plan Rengi

    // The main layout containing the drawer and the screen content
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // TODO: Implement the drawer content as per the design
                Text("Drawer Content", modifier = Modifier.padding(16.dp))
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.7f), // Genişlik %70
                drawerContainerColor = DrawerBackgroundColor // Arka plan rengini ayarla
            ) {
                // `Box` kullanarak dalga görselini en alta, butonları en üste sabitliyoruz
                Box(modifier = Modifier.fillMaxSize()) {

                    // DALGA GÖRSELİ (En altta duracak)
                    WaveFooter(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Dalganın yüksekliği
                            .align(Alignment.BottomCenter)
                    )

                    // İÇERİK: Butonlar (En üstte duracak)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        Spacer(Modifier.height(12.dp))

                        // Ayarlar Butonu
                        Button(
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(Routes.Settings)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp), // Gölgeli efekt
                            colors = ButtonDefaults.buttonColors(containerColor = DeepBlueColor)
                        ) { Text("Ayarlar") }

                        Spacer(Modifier.height(8.dp))

                        // Etkinlik Takvimi Butonu
                        Button(
                            onClick = { /* Etkinlik Takvimi ekranına git */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp), // Gölgeli efekt
                            colors = ButtonDefaults.buttonColors(containerColor = DeepBlueColor)
                        ) { Text("Etkinlik Takvimi") }
                    }
                }
            }
        }
    ) {
        // Arkadaki ana ekran içeriği
        EventsScreenContent(
            navController = navController,
            eventsState = eventsState,
            onRetry = { viewModel.fetchEvents() },
            showSnackbar = showSnackbar,
            onMenuClick = { scope.launch { drawerState.open() } }
        )
    }
}

// This is the stateless composable that only displays the UI

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

    // --- RENK PALETİ ---
    val BarBackgroundColor = Color(0xFFCCC2FF) // #CCC2FF - Açık Mor (Bar Rengi)
    val DeepBlueColor = Color(0xFF160092)      // #160092 - Koyu Lacivert (Buton ve İkonlar)
    val BackgroundColor = Color(0xFFEEEBFF)    // #EEEBFF - Arka plan

    Scaffold(
        containerColor = VeryLightPurple,
        containerColor = BackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Menu") } },
                actions = { IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) { Icon(Icons.Default.Notifications, "Notifications") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
            // TopAppBar yerine tam kontrol için Row kullanıyoruz
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp) // Barın yüksekliği
                    .background(BarBackgroundColor) // Arka plan rengi
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) // Köşeleri yuvarlatma
                    .padding(horizontal = 8.dp), // İkonların kenarlara yapışmaması için
                horizontalArrangement = Arrangement.SpaceBetween, // İçeriği kenarlara yasla, ortala
                verticalAlignment = Alignment.CenterVertically // İçeriği dikeyde tam ortala
            ) {
                // SOL İKON (Hamburger)
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = DeepBlueColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // ORTADAKİ BAŞLIK
                Text(
                    text = "Etkinlikler",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = DeepBlueColor
                )

                // SAĞ İKON (Zil)
                IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = DeepBlueColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        // --- 1. BUTON (FAB) ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSnackbar("Etkinlik Oluştur") },
                shape = CircleShape,
                containerColor = DeepBlueColor,
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                modifier = Modifier
                    .offset(y = 50.dp)
                    .size(80.dp)
            ) {}
        },
        floatingActionButtonPosition = FabPosition.Center,

        // --- 2. ALT BAR (BOTTOM BAR) ---
        bottomBar = {
            // TODO: Replace with the actual BottomNav implementation for students
            BottomAppBar(containerColor = LightPurple) {
                Text("Student Bottom Nav Placeholder")
            }
            BottomAppBar(
                containerColor = BarBackgroundColor,
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier
                    .height(80.dp)
                    .clip(BottomAppBarCutoutShape({ 80.dp.toPx() }, { 8.dp.toPx() }))
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        IconButton({/* Ana Sayfa */}) { Icon(Icons.Default.Home, "Ana Sayfa", tint = DeepBlueColor, modifier = Modifier.size(32.dp)) }
                        IconButton({ showSnackbar("Keşfet") }) { Icon(Icons.Default.Explore, "Keşfet", tint = DeepBlueColor, modifier = Modifier.size(32.dp)) }
                    }
                    Spacer(Modifier.size(80.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        IconButton({ navController.navigate(Routes.ClubCommitteeLogin) }) { Icon(Icons.Default.Groups, "Kulüpler", tint = DeepBlueColor, modifier = Modifier.size(32.dp)) }
                        IconButton({ navController.navigate(Routes.PersonnelLogin) }) { Icon(Icons.Default.Person, "Profil", tint = DeepBlueColor, modifier = Modifier.size(32.dp)) }
                    }
                }
            }
        }
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
                confirmButton = { Button({datePickerState.selectedDateMillis?.let { selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }; showDatePicker = false}) { Text("Tamam") } },
                dismissButton = { Button({ showDatePicker = false }) { Text("İptal") } }
            ) { DatePicker(state = datePickerState) }
        }

        Column(Modifier.padding(paddingValues)) {
            DateCard(selectedDate, { showDatePicker = true }, { selectedDate = selectedDate.minusDays(1) }, { selectedDate = selectedDate.plusDays(1) })
            SearchAndFilterBar({ showSnackbar("Arama tıklandı") }, { showSnackbar("Filtre tıklandı") })
            CategoryChips(selectedCategory) { selectedCategory = it }
        Column(modifier = Modifier.padding(paddingValues)) {
            DateCard(
                date = selectedDate,
                onDateClick = { showDatePicker = true },
                onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) },
                onNextDayClick = { selectedDate = selectedDate.plusDays(1) }
            )
            SearchAndFilterBar(
                onSearchClick = { showSnackbar("Arama tıklandı") },
                onFilterClick = { showSnackbar("Filtre tıklandı") }
            )
            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { category -> selectedCategory = category }
            )

            when (eventsState) {
                is Resource.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is Resource.Success -> {
                    val filteredByCategory = if (selectedCategory == "TÜMÜ") eventsState.data ?: emptyList() else eventsState.data?.filter { it.clubName.contains(selectedCategory, ignoreCase = true) } ?: emptyList()
                    val filteredByDate = filteredByCategory.filter { Instant.ofEpochMilli(it.startTime).atZone(ZoneId.systemDefault()).toLocalDate().isEqual(selectedDate) }
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
                        Box(Modifier.fillMaxSize().padding(16.dp), Alignment.Center) { Text("Bu tarihte hiç etkinlik bulunmuyor.") }
                    } else {
                        LazyColumn(Modifier.fillMaxSize()) { items(filteredByDate) { event -> EventCard(event) { navController.navigate("${Routes.EventDetail}/${event.id}") } } }
                    }
                }
                is Resource.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(eventsState.message ?: "Bilinmeyen bir hata oluştu."); Button({ onRetry() }) { Text("Yeniden Dene") } } }
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredByDate) { event ->
                                EventCard(event = event, onClick = { navController.navigate(Routes.EventDetail.createRoute(event.id)) })
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
    Card(Modifier.fillMaxWidth().padding(16.dp), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color(0xFFE8E5FF))) {
        Row(Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onPreviousDayClick) { Icon(Icons.Default.ArrowBack, "Önceki Gün") }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onDateClick)) {
    // Using a formatter is a more robust way to get the localized month name.
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL", Locale("tr"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDayClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Önceki Gün") }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onDateClick)) {
                Text(date.dayOfMonth.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                // Use the formatter for a more reliable month display, and capitalize it for consistency.
                Text(date.format(monthFormatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("tr")) else it.toString() }, fontSize = 16.sp)
            }
            IconButton(onNextDayClick) { Icon(Icons.Default.ArrowForward, "Sonraki Gün") }
            IconButton(onClick = onNextDayClick) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Sonraki Gün") }
        }
    }
}

@Composable
fun SearchAndFilterBar(onSearchClick: () -> Unit, onFilterClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        IconButton(onSearchClick) { Icon(Icons.Default.Search, "Arama", tint = Color.Gray) }
        IconButton(onFilterClick) { Icon(Icons.Default.Settings, "Filtrele", tint = Color.Gray) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, "Arama") }
        IconButton(onClick = onFilterClick) { Icon(Icons.Default.Settings, "Filtrele") }
    }
}

@Composable
fun CategoryChips(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("TÜMÜ", "EKONOMİ", "HUKUK", "BİLİŞİM", "SANAT", "KÜLTÜR VE ETKİNLİK", "ATATÜRKÇÜ DÜŞÜNCE KULÜBÜ", "DANS KULÜBÜ")
    val categories = listOf("TÜMÜ", "EKONOMİ", "HUKUK", "BİLİŞİM", "SANAT", "KÜLTÜR VE ETKİNLİK")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        items(categories) { category ->
            SuggestionChip(
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = SuggestionChipDefaults.suggestionChipColors(if (selectedCategory == category) Color(0xFFD6C5FF) else Color(0xFFE8E5FF), Color.Black),
                border = null
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (selectedCategory == category) LightPurple else VeryLightPurple
                )
            )
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Kulüp: ${event.clubName}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Mekan: ${event.location}", style = MaterialTheme.typography.bodyMedium)
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick), RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color(0xFFE8E5FF))) {
        Column(Modifier.padding(20.dp).fillMaxWidth()) {
            Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Kulüp: ${event.clubName}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("Mekan: ${event.location}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// --- ŞEKİL SINIFI ---
class BottomAppBarCutoutShape(private val fabSize: Density.() -> Float, private val fabMargin: Density.() -> Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(Path().apply {
            val fabSizePx = fabSize(density); val fabMarginPx = fabMargin(density); val diameter = fabSizePx + fabMarginPx * 2
            val radius = diameter / 2f; val width = size.width; val height = size.height; val centerX = width / 2f
            moveTo(0f, 0f); lineTo(centerX - radius, 0f); cubicTo(centerX - radius, 0f, centerX - radius, radius, centerX, radius)
            cubicTo(centerX, radius, centerX + radius, radius, centerX + radius, 0f); lineTo(width, 0f); lineTo(width, height); lineTo(0f, height); close()
        })
    }
}

// --- DALGA ÇİZİMİ ---
// --- DALGA ÇİZİMİ (İSTEĞE GÖRE BİREBİR GÜNCELLENDİ) ---
@Composable
fun WaveFooter(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 1. Dalga (EN ARKADAKİ ve EN YÜKSEK - Açık Mor/Lila)
        val path2 = Path().apply {
            moveTo(0f, height * 0.3f) // Daha yüksekten başla
            cubicTo(width * 0.25f, height * 0.6f, width * 0.5f, height * 0.1f, width * 0.75f, height * 0.4f)
            cubicTo(width, height * 0.7f, width, height, width, height)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path2,
            color = Color(0xFF775CFF).copy(alpha = 0.5f)
        )

        // 2. Dalga (ORTADAKİ - Koyu Lacivert/Mor)
        val path1 = Path().apply {
            moveTo(0f, height * 0.5f) // Orta yükseklikten başla
            cubicTo(width * 0.3f, height * 0.3f, width * 0.6f, height * 0.8f, width, height * 0.4f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path1,
            color = Color(0xFF160092).copy(alpha = 0.8f) // Daha belirgin
        )

        // 3. Dalga (EN ÖNDEKİ ve EN KISA - Turkuaz)
        val path3 = Path().apply {
            moveTo(0f, height * 0.7f) // En alçaktan başla
            cubicTo(width * 0.2f, height * 0.6f, width * 0.4f, height * 0.9f, width * 0.6f, height * 0.8f)
            cubicTo(width * 0.8f, height * 0.7f, width, height * 0.9f, width, height * 0.9f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path3,
            color = Color(0xFFBBDDF2).copy(alpha = 0.9f) // Turkuaz rengi
        )
    }
}


// --- ÖNİZLEME FONKSİYONU ---
@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    _7ClubTheme {
        EventsScreenContent(
            navController = rememberNavController(),
            eventsState = Resource.Success(
                listOf(
                    Event(id = "1", title = "Yaratıcı Yazarlık Atölyesi", clubName = "Kültür ve Etkinlik Kulübü", location = "B-101", startTime = System.currentTimeMillis()),
                    Event(id = "2", title = "Müzik Kulübü Karaoke Gecesi", clubName = "Müzik Kulübü", location = "Konferans Salonu", startTime = System.currentTimeMillis())
                )
            ),
            eventsState = Resource.Success(data = emptyList()),
            onRetry = {},
            showSnackbar = {},
            onMenuClick = {}
        )
    }
}
