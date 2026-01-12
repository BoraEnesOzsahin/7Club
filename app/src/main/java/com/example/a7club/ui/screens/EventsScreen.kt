@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Menu
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
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// --- GÜNCELLENMİŞ TEMA RENKLERİ (SS Analizi) ---
private val ThemeDarkBlue = Color(0xFF160092)    // Yazılar ve İkonlar
private val ThemeTopBarColor = Color(0xFFCCC2FF) // Üst Bar Arkaplanı (Lavanta)
private val ThemeCardColor = Color(0xFFE6E3F6)   // Kart Arkaplanı (Açık Lila)
private val ThemeBackgroundColor = Color.White   // Ana Arkaplan (Beyaz)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: StudentFlowViewModel = viewModel()
) {
    val eventsState by viewModel.eventsState

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
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ThemeDarkBlue)
                ) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = ThemeDarkBlue)
                ) { Text("İptal") }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White // Dialog arka planı
            )
        ) {
            DatePicker(
                state = datePickerState,
                // --- HATA DÜZELTİLDİ: Geçerli parametreler kullanıldı ---
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = ThemeDarkBlue,
                    headlineContentColor = ThemeDarkBlue,
                    weekdayContentColor = ThemeDarkBlue,
                    subheadContentColor = ThemeDarkBlue,
                    yearContentColor = ThemeDarkBlue,
                    currentYearContentColor = ThemeDarkBlue,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = ThemeDarkBlue,
                    dayContentColor = ThemeDarkBlue,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = ThemeDarkBlue,
                    todayContentColor = ThemeDarkBlue,
                    todayDateBorderColor = ThemeDarkBlue
                )
            )
        }
    }

    Scaffold(
        // 1. ANA ARKA PLAN BEYAZ
        containerColor = ThemeBackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Etkinlikler",
                        fontWeight = FontWeight.Bold,
                        color = ThemeDarkBlue
                    )
                },
                // SS'te sol tarafta menü ikonu var, ekleyelim (işlevsiz de olsa görsel bütünlük için)
                navigationIcon = {
                    IconButton(onClick = { /* Menü işlevi */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = ThemeDarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) {
                        Icon(
                            Icons.Default.Notifications,
                            "Notifications",
                            tint = ThemeDarkBlue
                        )
                    }
                },
                // 2. ÜST BAR RENGİ (Lavanta)
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ThemeTopBarColor,
                    scrolledContainerColor = ThemeTopBarColor
                )
            )
        },
        bottomBar = {
            // NOT: Alt barın rengini değiştirmek için StudentBottomAppBar dosyasını güncellemelisin.
            // O dosya ayrı olduğu için buradan müdahale edemiyorum ancak ana yapı bozulmaz.
            StudentBottomAppBar(navController = navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TARİH KARTI
            StudentHeaderDateCard(
                date = selectedDate,
                onDateClick = { showDatePicker = true },
                onPreviousDayClick = { selectedDate = selectedDate.minusDays(1) },
                onNextDayClick = { selectedDate = selectedDate.plusDays(1) }
            )

            when (val state = eventsState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ThemeDarkBlue)
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message ?: "Hata oluştu", color = Color.Red)
                    }
                }
                is Resource.Success -> {
                    val allEvents = state.data ?: emptyList()

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

                    if (filteredEvents.isNotEmpty()) {
                        Text(
                            text = "Toplam ${filteredEvents.size} Etkinlik",
                            color = ThemeDarkBlue.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                            tint = ThemeDarkBlue.copy(alpha = 0.5f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Bu tarihte planlanmış\netkinlik bulunmuyor.",
                                            color = ThemeDarkBlue.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filteredEvents) { event ->
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
        // 3. KART RENGİ (Açık Lila - Beyaz zeminde ayrışması için)
        colors = CardDefaults.cardColors(containerColor = ThemeCardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDayClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Önceki Gün", tint = ThemeDarkBlue)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onDateClick)
            ) {
                Text(
                    date.dayOfMonth.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeDarkBlue
                )
                Text(
                    date.format(monthFormatter).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale("tr")) else it.toString()
                    },
                    fontSize = 16.sp,
                    color = ThemeDarkBlue
                )
            }
            IconButton(onClick = onNextDayClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Sonraki Gün", tint = ThemeDarkBlue)
            }
        }
    }
}

@Composable
fun StudentCenteredCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        // 4. LİSTE KART RENGİ (Açık Lila)
        colors = CardDefaults.cardColors(containerColor = ThemeCardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ThemeDarkBlue,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.clubName,
                style = MaterialTheme.typography.bodyMedium,
                color = ThemeDarkBlue.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}