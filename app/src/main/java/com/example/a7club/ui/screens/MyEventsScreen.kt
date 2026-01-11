@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme._7ClubTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val myEventsLightPurple = Color(0xFFD1C4E9)
private val myEventsVeryLightGray = Color(0xFFF8F8F8)

// --- Veri Modeli ---
private data class ScreenEvent(val title: String, val club: String, val date: LocalDate)

@Composable
fun MyEventsScreen(navController: NavController) {
    val myEvents = remember {
        listOf(
            ScreenEvent("Yaratıcı Yazarlık Atölyesi", "Kültür Kulübü", LocalDate.now().plusDays(2)),
            ScreenEvent("Karaoke Gecesi", "Müzik Kulübü", LocalDate.now().plusDays(5)),
            ScreenEvent("Teknik Gezi", "Bilişim Kulübü", LocalDate.now().plusWeeks(2)),
            ScreenEvent("Psikoloji Semineri", "Psikoloji Kulübü", LocalDate.now().plusWeeks(3)),
            ScreenEvent("Kutu Oyunları", "Oyun Kulübü", LocalDate.now().plusMonths(1)),
            ScreenEvent("Münazara Ligi", "Münazara Kulübü", LocalDate.now().plusMonths(1).plusDays(3))
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedClubs by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }
    val listState = rememberLazyListState()

    val filteredEvents = remember(searchQuery, selectedClubs, selectedStartDate, selectedEndDate) {
        myEvents.filter { event ->
            val matchesSearch = event.title.contains(searchQuery, ignoreCase = true)
            val matchesClub = selectedClubs.isEmpty() || selectedClubs.contains(event.club)
            val matchesDate = (selectedStartDate == null || !event.date.isBefore(selectedStartDate)) &&
                    (selectedEndDate == null || !event.date.isAfter(selectedEndDate))
            matchesSearch && matchesClub && matchesDate
        }
    }

    if (showFilterDialog) {
        MyEventsFilterDialog(
            allClubs = myEvents.map { it.club }.distinct(),
            initialSelectedClubs = selectedClubs,
            initialStartDate = selectedStartDate,
            initialEndDate = selectedEndDate,
            onDismiss = { showFilterDialog = false },
            onApply = { clubs, start, end ->
                selectedClubs = clubs
                selectedStartDate = start
                selectedEndDate = end
                showFilterDialog = false
            }
        )
    }

    Scaffold(
        containerColor = myEventsVeryLightGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Katılacağım Etkinlikler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                actions = { IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Notifications, "Bildirimler", tint = DarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = myEventsLightPurple)
            )
        },
        bottomBar = {
            MyClubsAppBottomBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ara...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = DarkBlue) },
                trailingIcon = { IconButton(onClick = { showFilterDialog = true }) { Icon(Icons.Default.Tune, "Filtrele", tint = DarkBlue) } },
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 8.dp, bottom = 16.dp)
                ) {
                    items(filteredEvents) { event ->
                        MyEventCard(event = event)
                    }
                }

                MyEventsCustomScrollbar(
                    state = listState,
                    modifier = Modifier.padding(start = 4.dp).fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun MyEventCard(event: ScreenEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = myEventsLightPurple.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.club, color = DarkBlue.copy(alpha = 0.7f), fontSize = 14.sp)
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun MyEventsCustomScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val layoutInfo by remember { derivedStateOf { state.layoutInfo } }
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    val totalItemsCount = layoutInfo.totalItemsCount

    if (totalItemsCount == 0 || visibleItemsInfo.isEmpty() || visibleItemsInfo.size >= totalItemsCount) {
        return
    }

    BoxWithConstraints(modifier = modifier.width(12.dp)) {
        val trackHeightPx = constraints.maxHeight.toFloat()
        val viewportHeightPx = layoutInfo.viewportSize.height.toFloat()

        val avgItemHeight = remember(visibleItemsInfo) {
            visibleItemsInfo.sumOf { it.size }.toFloat() / visibleItemsInfo.size
        }

        val totalContentHeight = avgItemHeight * totalItemsCount
        val safeTotalContentHeight = totalContentHeight.coerceAtLeast(viewportHeightPx * 1.01f)

        val dragRatio = remember(safeTotalContentHeight, trackHeightPx) {
             safeTotalContentHeight / trackHeightPx
        }

        val thumbHeightPx = remember(viewportHeightPx, safeTotalContentHeight, trackHeightPx) {
            (viewportHeightPx / safeTotalContentHeight * trackHeightPx)
                .coerceAtLeast(with(density) { 36.dp.toPx() })
        }

        val firstVisibleItemIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }
        val firstVisibleItemScrollOffset by remember { derivedStateOf { state.firstVisibleItemScrollOffset } }

        val scrollProgress = remember(firstVisibleItemIndex, firstVisibleItemScrollOffset, avgItemHeight, safeTotalContentHeight, viewportHeightPx) {
            (firstVisibleItemIndex * avgItemHeight + firstVisibleItemScrollOffset) / (safeTotalContentHeight - viewportHeightPx)
        }
        
        val thumbOffsetPx = remember(trackHeightPx, thumbHeightPx, scrollProgress) {
            (trackHeightPx - thumbHeightPx) * scrollProgress.coerceIn(0f, 1f)
        }

        with(density) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10.dp))
                    .background(myEventsLightPurple.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .offset(y = thumbOffsetPx.toDp())
                    .height(thumbHeightPx.toDp())
                    .width(6.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(10.dp))
                    .background(myEventsLightPurple)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                state.scrollBy(delta * dragRatio)
                            }
                        }
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyEventsFilterDialog(
    allClubs: List<String>,
    initialSelectedClubs: Set<String>,
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?,
    onDismiss: () -> Unit,
    onApply: (Set<String>, LocalDate?, LocalDate?) -> Unit
) {
    var tempSelectedClubs by remember { mutableStateOf(initialSelectedClubs) }
    var tempStartDate by remember { mutableStateOf(initialStartDate) }
    var tempEndDate by remember { mutableStateOf(initialEndDate) }
    var showDatePicker by remember { mutableStateOf<String?>(null) }

    if (showDatePicker != null) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (if (showDatePicker == "start") tempStartDate else tempEndDate)?.atStartOfDay(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = null },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val newDate = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            if (showDatePicker == "start") tempStartDate = newDate else tempEndDate = newDate
                        }
                        showDatePicker = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) { Text("Seç") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = null }) { Text("İptal", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = myEventsVeryLightGray,
        title = { Text("Filtrele ve Sırala", fontWeight = FontWeight.Bold, color = DarkBlue) },
        text = {
            Column {
                Text("Kulübe Göre Filtrele", fontWeight = FontWeight.SemiBold, color = DarkBlue)
                LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                    items(allClubs) { club ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempSelectedClubs = if (tempSelectedClubs.contains(club)) tempSelectedClubs - club else tempSelectedClubs + club }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = tempSelectedClubs.contains(club),
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(checkedColor = DarkBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(club, color = DarkBlue)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tarihe Göre Filtrele", fontWeight = FontWeight.SemiBold, color = DarkBlue)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Button(onClick = { showDatePicker = "start" }, colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)) {
                        Text(tempStartDate?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "Başlangıç")
                    }
                    Button(onClick = { showDatePicker = "end" }, colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)) {
                        Text(tempEndDate?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "Bitiş")
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onApply(tempSelectedClubs, tempStartDate, tempEndDate) }, colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)) { Text("Uygula") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal", color = Color.Gray) } }
    )
}

@Preview(showBackground = true)
@Composable
private fun MyEventsScreenPreview() {
    _7ClubTheme {
        MyEventsScreen(rememberNavController())
    }
}
