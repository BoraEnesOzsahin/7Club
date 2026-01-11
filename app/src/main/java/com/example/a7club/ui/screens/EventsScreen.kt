package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.model.Event
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Hedeflenen Kart Rengi (Personel ekranlarında kullanılan özel ton)
private val CardBackgroundColor = Color(0xFFF3EFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel = viewModel()) {
    val eventsState by viewModel.eventsState

    // Tarih Seçimi (Varsayılan: Bugün)
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // --- DATE PICKER DIALOG ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
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
            dismissButton = {
                Button(onClick = { showDatePicker = false }) { Text("İptal") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        containerColor = Color.White, // Arka plan BEYAZ yapıldı
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) {
                        Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            StudentBottomAppBar(navController = navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 3. TARİH KARTI (GÜNLÜK TAKVİM)
            // Header rengi LightPurple olarak kalabilir veya CardBackground yapılabilir.
            // Öne çıkması için LightPurple daha iyidir.
            StudentHeaderDateCard(
                date = selectedDate,
                onDateClick = { showDatePicker = true },
                onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) },
                onNextDayClick = { selectedDate = selectedDate.plusDays(1) }
            )

            // --- LİSTELEME MANTIĞI ---
            when (val state = eventsState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DarkBlue)
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message ?: "Hata oluştu", color = Color.Red)
                    }
                }
                is Resource.Success -> {
                    val allEvents = state.data ?: emptyList()

                    // Filtreleme: Sadece Onaylı + Tarih
                    val filteredEvents = allEvents.filter { event ->
                        val isApproved = event.status == "APPROVED"

                        val isSameDay = event.timestamp?.let { ts ->
                            val eventDate = Instant.ofEpochMilli(ts.seconds * 1000)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            eventDate.isEqual(selectedDate)
                        } ?: false

                        isApproved && isSameDay
                    }.sortedBy { it.timestamp }

                    // İstatistik Yazısı
                    if (filteredEvents.isNotEmpty()) {
                        Text(
                            text = "Toplam ${filteredEvents.size} Etkinlik",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).align(Alignment.CenterHorizontally)
                        )
                    }

                    // Liste
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Kartlar arası boşluk
                    ) {
                        if (filteredEvents.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.EventBusy,
                                            null,
                                            tint = Color.LightGray,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Bu tarihte planlanmış\netkinlik bulunmuyor.",
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filteredEvents) { event ->
                                // GÜNCELLENMİŞ RENKLİ KART
                                StudentCenteredCard(event) {
                                    navController.navigate(Routes.EventDetail.createRoute(event.id))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- BİLEŞENLER ---

@Composable
fun StudentHeaderDateCard(
    date: LocalDate,
    onDateClick: () -> Unit,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit
) {
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL", Locale("tr"))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple) // Burası başlık olduğu için biraz daha koyu kalabilir
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDayClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Önceki Gün", tint = DarkBlue)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onDateClick)
            ) {
                Text(
                    date.dayOfMonth.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
                Text(
                    date.format(monthFormatter).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale("tr")) else it.toString()
                    },
                    fontSize = 16.sp,
                    color = DarkBlue
                )
            }
            IconButton(onClick = onNextDayClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Sonraki Gün", tint = DarkBlue)
            }
        }
    }
}

// --- GÜNCELLENEN KART TASARIMI ---
// Renk: CardBackgroundColor (0xFFF3EFFF)
// Düzen: Ortalı ve Alt Alta
@Composable
fun StudentCenteredCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor), // <-- RENGİ GÜNCELLEDİM
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat görünüm için gölgeyi kaldırdım (isteğe bağlı)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp), // İç boşlukları biraz artırdım
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Etkinlik Adı
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Kulüp Adı
            Text(
                text = event.clubName,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkBlue.copy(alpha = 0.7f), // Biraz daha opak
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}