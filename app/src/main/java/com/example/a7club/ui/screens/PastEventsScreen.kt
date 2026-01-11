package com.example.a7club.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.R
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme._7ClubTheme
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch

private val pastEventsLightPurple = Color(0xFFD1C4E9)
private val pastEventsVeryLightGray = Color(0xFFF8F8F8)

private data class PastEvent(val title: String, val club: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastEventsScreen(navController: NavController) {
    val pastEvents = remember {
        listOf(
            PastEvent("Takı Atölyesi", "Kültür ve Etkinlik Kulübü"),
            PastEvent("Oyun Gecesi", "Müzik Kulübü"),
            PastEvent("Quiz Night", "Bilişim Kulübü"),
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val filteredEvents = pastEvents.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.club.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = pastEventsVeryLightGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Geçmiş Etkinliklerim", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                actions = { IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Notifications, "Bildirimler", tint = DarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = pastEventsLightPurple)
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
                trailingIcon = { IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Tune, "Filtrele", tint = DarkBlue) } },
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
                        PastEventCard(event = event)
                    }
                }

                PastEventsCustomScrollbar(
                    state = listState,
                    modifier = Modifier.padding(start = 4.dp).fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun PastEventCard(event: PastEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = pastEventsLightPurple.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = getClubLogo_PastEvents(event.club)),
                contentDescription = "Kulüp Logosu",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = event.title, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.club, color = DarkBlue.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PastEventsCustomScrollbar(
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
                    .background(pastEventsLightPurple.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .offset(y = thumbOffsetPx.toDp())
                    .height(thumbHeightPx.toDp())
                    .width(6.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(10.dp))
                    .background(pastEventsLightPurple)
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

private fun getClubLogo_PastEvents(clubName: String): Int {
    return when (clubName) {
        "Bilişim Kulübü" -> R.drawable.bilisim_logo
        "Kültür ve Etkinlik Kulübü" -> R.drawable.yukek_logo
        "Psikoloji Kulübü" -> R.drawable.psikoloji_logo
        "Müzik Kulübü" -> R.drawable.muzik_logo

        else -> R.drawable.ic_launcher_background // Default logo
    }
}


@Preview(showBackground = true)
@Composable
fun PastEventsScreenPreview() {
    _7ClubTheme {
        PastEventsScreen(rememberNavController())
    }
}
