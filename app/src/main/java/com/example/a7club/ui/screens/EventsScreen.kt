package com.example.a7club.ui.screens
// (Home ve Person zaten vardır)
// Home ve Person zaten ekli olmalı
// Diğer importların zaten vardır...

import kotlinx.coroutines.launch // BU SATIRI EKLE

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.data.Resource
import com.example.a7club.data.models.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme._7ClubTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel, showSnackbar: (String) -> Unit) {
    val eventsState by viewModel.eventsState

    // 1. Çekmece menünün durumunu (açık/kapalı) yönetmek için state oluşturuyoruz.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // 2. State'i açıp kapatmak için coroutine scope'a ihtiyacımız var.
    val scope = rememberCoroutineScope()

    // 3. ModalNavigationDrawer, çekmece menüyü ve ana içeriği birleştirir.
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // --- BURASI AÇILAN MENÜNÜN İÇERİĞİ ---
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.7f) // Genişliği ekranın %70'i yap
            ) {
                // Başlık veya boşluk
                Spacer(Modifier.height(12.dp))

                // Ayarlar butonu
                Button(
                    onClick = { /* Ayarlar ekranına git */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080))
                ) {
                    Text("Ayarlar")
                }

                Spacer(Modifier.height(8.dp))

                // Etkinlik Takvimi butonu
                Button(
                    onClick = { /* Etkinlik Takvimi ekranına git */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080))
                ) {
                    Text("Etkinlik Takvimi")
                }

                // İsteğe bağlı olarak dalga görseli de buraya eklenebilir.
            }
        }
    ) {
        // --- BURASI ARKADAKİ ANA EKRAN İÇERİĞİ ---
        EventsScreenContent(
            navController = navController,
            eventsState = eventsState,
            onRetry = viewModel::fetchEvents,
            showSnackbar = showSnackbar,
            onMenuClick = {
                // Hamburger menü ikonuna tıklandığında çekmeceyi aç
                scope.launch {
                    drawerState.open()
                }
            }
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
    onMenuClick: () -> Unit // Menü tıklama aksiyonunu dışarıdan alıyoruz
) {
    var selectedCategory by remember { mutableStateOf("TÜMÜ") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF3F1FF),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold) },
                // Tıklama aksiyonunu `onMenuClick` ile bağlıyoruz
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "Menu") } },
                actions = {
                    IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFE8E5FF))
            )
        },
        bottomBar = {
            // BottomNav fonksiyonun burada olduğunu varsayıyorum
            BottomNav(navController = navController, showSnackbar = showSnackbar)
        }
        // Diğer Scaffold parametreleri (floatingActionButton vs.) buraya eklenebilir.
    ) { paddingValues ->

        // --- Sayfa içeriği burada başlıyor ve değişmiyor ---

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
                    }) {
                        Text("Tamam")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("İptal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Column(modifier = Modifier.padding(paddingValues)) {
            DateCard(
                date = selectedDate,
                onDateClick = { showDatePicker = true },
                onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) },
                onNextDayClick = { selectedDate = selectedDate.plusDays(1) }
            )
            SearchAndFilterBar(onSearchClick = { showSnackbar("Arama tıklandı") }, onFilterClick = { showSnackbar("Filtre tıklandı") })
            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { category -> selectedCategory = category }
            )

            when (eventsState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val filteredByCategory = if (selectedCategory == "TÜMÜ") {
                        eventsState.data ?: emptyList()
                    } else {
                        eventsState.data?.filter { event ->
                            val categoryWords = selectedCategory.split(" ")
                            categoryWords.all { word ->
                                event.clubName.contains(word, ignoreCase = true)
                            }
                        } ?: emptyList()
                    }

                    val filteredByDate = filteredByCategory.filter { event ->
                        val eventDate = Instant.ofEpochMilli(event.startTime).atZone(ZoneId.systemDefault()).toLocalDate()
                        eventDate.isEqual(selectedDate)
                    }

                    if (filteredByDate.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Bu tarihte hiç etkinlik bulunmuyor.")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredByDate) { event ->
                                EventCard(event = event, onClick = {
                                    navController.navigate(Routes.EventDetail.createRoute(event.id))
                                })
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = eventsState.message ?: "Bilinmeyen bir hata oluştu.")
                            Button(onClick = onRetry) { Text("Yeniden Dene") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateCard(date: LocalDate, onDateClick: () -> Unit, onPreviousDayClick: () -> Unit, onNextDayClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E5FF))
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDayClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Önceki Gün")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onDateClick)
            ) {
                Text(date.dayOfMonth.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(date.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("tr")), fontSize = 16.sp)
            }
            IconButton(onClick = onNextDayClick) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Sonraki Gün")
            }
        }
    }
}

@Composable
fun SearchAndFilterBar(onSearchClick: () -> Unit, onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, contentDescription = "Arama", tint = Color.Gray)
        }
        IconButton(onClick = onFilterClick) {
            Icon(Icons.Default.Settings, contentDescription = "Filtrele", tint = Color.Gray)
        }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E5FF))
    ) {
        Column(modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Kulüp: ${event.clubName}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Mekan: ${event.location}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// EventsScreen.kt içinde

@Composable
fun BottomNav(navController: NavController, showSnackbar: (String) -> Unit) {
    val density = LocalDensity.current

    // Barın şeklini (Oyuğu) oluşturan çizim kodu
    val customBarShape = remember {
        GenericShape { size, _ ->
            val fabRadius = with(density) { 40.dp.toPx() } // Oyuğun yarıçapı
            val fabMargin = with(density) { 8.dp.toPx() }  // Buton etrafındaki boşluk

            val cutoutRadius = fabRadius + fabMargin
            val center = size.width / 2f

            moveTo(0f, 0f)
            lineTo(center - cutoutRadius, 0f)

            // Yarım daire (Oyuk) çizimi
            arcTo(
                rect = Rect(
                    left = center - cutoutRadius,
                    top = -cutoutRadius,
                    right = center + cutoutRadius,
                    bottom = cutoutRadius
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )

            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
    }

    BottomAppBar(
        containerColor = Color(0xFFE8E5FF),
        modifier = Modifier.clip(customBarShape), // Oluşturulan şekli bara uygula
        contentPadding = PaddingValues(0.dp)
    ) {
        // SOL İKONLAR
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { showSnackbar("Keşfet") },
            icon = { Icon(Icons.Default.Search, contentDescription = "Keşfet") }
        )

        // ORTA BOŞLUK (Buton için yer ayırır)
        Spacer(Modifier.weight(1f))

        // SAĞ İKONLAR (Fazla buton kaldırıldı)
        NavigationBarItem(
            selected = false,
            onClick = { showSnackbar("Kulüpler") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Kulüpler") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { showSnackbar("Profil") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    _7ClubTheme {
        EventsScreenContent(
            navController = rememberNavController(),
            eventsState = Resource.Success(data = emptyList()), // Boş veri ile önizleme
            onRetry = {},
            showSnackbar = {},
            onMenuClick = {} // EKSİK OLAN PARAMETRE EKLENDİ
        )
    }
}
