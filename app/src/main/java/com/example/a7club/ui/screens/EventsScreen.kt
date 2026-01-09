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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController, viewModel: StudentFlowViewModel = viewModel()) {
    // ViewModel state
    val eventsState by viewModel.eventsState

    // --- Takvim State'i (Varsayılan: Bugün) ---
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // --- DatePicker Dialog ---
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
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Tarih Seçici Kart ---
            DateSelectorCard(
                date = selectedDate,
                onPreviousDay = { selectedDate = selectedDate.minusDays(1) },
                onNextDay = { selectedDate = selectedDate.plusDays(1) },
                onDateClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- ETKİNLİK LİSTESİ ---
            Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize()) {
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
                        val events = state.data ?: emptyList()

                        // --- FİLTRELEME: Seçilen Tarihe Göre ---
                        val filteredEvents = events.filter { event ->
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

                        if (filteredEvents.isEmpty()) {
                            // Boş Durum Gösterimi
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${selectedDate.dayOfMonth} ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale("tr"))}",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Onaylanmış etkinlik yok.",
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBlue
                                )
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(filteredEvents) { event ->
                                    StudentEventCard(event) {
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
}

// --- Tarih Seçici Bileşeni ---
@Composable
fun DateSelectorCard(
    date: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onDateClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDay) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Önceki Gün", tint = DarkBlue)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
                Text(
                    text = date.month.getDisplayName(TextStyle.FULL, Locale("tr")),
                    fontSize = 14.sp,
                    color = DarkBlue
                )
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("tr")),
                    fontSize = 12.sp,
                    color = DarkBlue.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = onNextDay) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Sonraki Gün", tint = DarkBlue)
            }
        }
    }
}

// --- Etkinlik Kartı Tasarımı ---
@Composable
fun StudentEventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tarih/Saat Kutusu
            Column(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(8.dp)
                    .width(60.dp), // Sabit genişlik hizalamayı düzeltir
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Saat bilgisini çekiyoruz. (Örn: "14:00")
                // Format "dd/MM/yyyy HH:mm" ise split(" ") boşluktan sonrasını alır.
                val timePart = event.dateString.split(" ").lastOrNull() ?: ""

                Text(text = "⏰", fontSize = 18.sp)
                // Eğer zaman formatı ":" içeriyorsa göster, yoksa gösterme
                if(timePart.contains(":")) {
                    Text(text = timePart, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Bilgiler
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkBlue, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.clubName, fontSize = 14.sp, color = DarkBlue.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.location, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}