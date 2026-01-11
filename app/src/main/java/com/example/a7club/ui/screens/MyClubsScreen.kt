package com.example.a7club.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.a7club.R
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme._7ClubTheme
import kotlinx.coroutines.launch

private val myClubsLightPurple = Color(0xFFD1C4E9)
private val myClubsVeryLightGray = Color(0xFFF8F8F8)

data class ClubScreenClub(val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClubsScreen(navController: NavController) {
    val clubs: List<ClubScreenClub> = remember {
        listOf(
            ClubScreenClub("Kültür ve Etkinlik Kulübü"),
            ClubScreenClub("Bilişim Kulübü"),
            ClubScreenClub("Psikoloji Kulübü"),
            ClubScreenClub("Müzik Kulübü"),
            ClubScreenClub("Oyun Kulübü"),
            ClubScreenClub("Sinema Kulübü")
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedClubs by remember { mutableStateOf<Set<String>>(emptySet()) }
    val listState = rememberLazyListState()

    val filteredClubs: List<ClubScreenClub> = remember(searchQuery, selectedClubs) {
        clubs.filter { club ->
            val matchesSearch = club.name.contains(searchQuery, ignoreCase = true)
            val matchesClub = selectedClubs.isEmpty() || selectedClubs.contains(club.name)
            matchesSearch && matchesClub
        }
    }

    if (showFilterDialog) {
        MyClubsFilterDialog(
            allClubs = clubs.map { it.name },
            initialSelectedClubs = selectedClubs,
            onDismiss = { showFilterDialog = false },
            onApply = {
                selectedClubs = it
                showFilterDialog = false
            }
        )
    }

    Scaffold(
        containerColor = myClubsVeryLightGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kulüplerim", fontWeight = FontWeight.Bold, color = DarkBlue) },
                actions = {
                    IconButton(onClick = { /* TODO: Bildirimler */ }) {
                        Icon(Icons.Default.Notifications, "Bildirimler", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = myClubsLightPurple)
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
                trailingIcon = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.Tune, "Filtrele", tint = DarkBlue)
                    }
                },
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
                    items(filteredClubs) { club ->
                        ClubCard(club = club, onClick = { /* TODO: Kulüp detaylarına git */ })
                    }
                }

                MyClubsCustomScrollbar(
                    state = listState,
                    modifier = Modifier.padding(start = 4.dp).fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun ClubCard(club: ClubScreenClub, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = myClubsLightPurple.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = getClubLogo_MyClubs(club.name)),
                contentDescription = "Kulüp Logosu",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = club.name, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClubsFilterDialog(
    allClubs: List<String>,
    initialSelectedClubs: Set<String>,
    onDismiss: () -> Unit,
    onApply: (Set<String>) -> Unit
) {
    var tempSelectedClubs by remember { mutableStateOf(initialSelectedClubs) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = myClubsVeryLightGray,
        title = { Text("Filtrele", fontWeight = FontWeight.Bold, color = DarkBlue) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                item {
                    Text("Kulübe Göre Filtrele", fontWeight = FontWeight.SemiBold, color = DarkBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                }
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
        },
        confirmButton = { Button(onClick = { onApply(tempSelectedClubs) }, colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)) { Text("Uygula") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal", color = Color.Gray) } }
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MyClubsCustomScrollbar(
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
                    .background(myClubsLightPurple.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .offset(y = thumbOffsetPx.toDp())
                    .height(thumbHeightPx.toDp())
                    .width(6.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(10.dp))
                    .background(myClubsLightPurple)
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


private fun getClubLogo_MyClubs(clubName: String): Int {
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
fun MyClubsScreenPreview() {
    _7ClubTheme {
        MyClubsScreen(rememberNavController())
    }
}
