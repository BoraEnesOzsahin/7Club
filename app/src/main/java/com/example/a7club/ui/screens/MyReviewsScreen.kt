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

private val myReviewsLightPurple = Color(0xFFD1C4E9)
private val myReviewsVeryLightGray = Color(0xFFF8F8F8)

// --- Veri Modeli ---
private data class Review(val title: String, val club: String, val isReviewed: Boolean)

@Composable
fun MyReviewsScreen(navController: NavController) {
    val reviews = remember {
        mutableStateListOf(
            Review("Takı Atölyesi", "Kültür ve Etkinlik Kulübü", false),
            Review("Siyaset Psikolojisi Semineri", "Psikoloji Kulübü", false),
            Review("Oyun Gecesi", "Müzik Kulübü", true),
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedClubs by remember { mutableStateOf<Set<String>>(emptySet()) }
    var reviewedStatus by remember { mutableStateOf<Boolean?>(null) } // null: all, true: reviewed, false: not reviewed
    val listState = rememberLazyListState()

    val filteredReviews = remember(searchQuery, selectedClubs, reviewedStatus, reviews) {
        reviews.filter { review ->
            val matchesSearch = review.title.contains(searchQuery, ignoreCase = true) || review.club.contains(searchQuery, ignoreCase = true)
            val matchesClub = selectedClubs.isEmpty() || selectedClubs.contains(review.club)
            val matchesStatus = reviewedStatus == null || review.isReviewed == reviewedStatus
            matchesSearch && matchesClub && matchesStatus
        }
    }

    if (showFilterDialog) {
        MyReviewsFilterDialog(
            allClubs = reviews.map { it.club }.distinct(),
            initialSelectedClubs = selectedClubs,
            initialReviewedStatus = reviewedStatus,
            onDismiss = { showFilterDialog = false },
            onApply = { clubs, status ->
                selectedClubs = clubs
                reviewedStatus = status
                showFilterDialog = false
            }
        )
    }

    Scaffold(
        containerColor = myReviewsVeryLightGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Değerlendirmelerim", fontWeight = FontWeight.Bold, color = DarkBlue) },
                actions = { IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Notifications, "Bildirimler", tint = DarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = myReviewsLightPurple)
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
                    items(filteredReviews) { review ->
                        MyReviewCard(review = review, navController = navController) { eventName, clubName ->
                            val index = reviews.indexOfFirst { it.title == eventName && it.club == clubName }
                            if (index != -1) {
                                reviews[index] = reviews[index].copy(isReviewed = true)
                            }
                        }
                    }
                }

                MyReviewsCustomScrollbar(
                    state = listState,
                    modifier = Modifier.padding(start = 4.dp).fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun MyReviewCard(
    review: Review, 
    navController: NavController, 
    onReviewSubmitted: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = myReviewsLightPurple.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = review.title, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = review.club, color = DarkBlue.copy(alpha = 0.7f), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { 
                    navController.navigate(Routes.EventReview.createRoute(review.title, review.club))
                    onReviewSubmitted(review.title, review.club)
                },
                enabled = !review.isReviewed,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (review.isReviewed) myReviewsLightPurple else DarkBlue,
                    contentColor = if (review.isReviewed) DarkBlue.copy(alpha = 0.7f) else Color.White,
                    disabledContainerColor = myReviewsLightPurple,
                    disabledContentColor = DarkBlue.copy(alpha = 0.7f)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (review.isReviewed) "Değerlendirme\nYapıldı" else "Etkinliği\nDeğerlendir",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyReviewsFilterDialog(
    allClubs: List<String>,
    initialSelectedClubs: Set<String>,
    initialReviewedStatus: Boolean?,
    onDismiss: () -> Unit,
    onApply: (Set<String>, Boolean?) -> Unit
) {
    var tempSelectedClubs by remember { mutableStateOf(initialSelectedClubs) }
    var tempReviewedStatus by remember { mutableStateOf(initialReviewedStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = myReviewsVeryLightGray,
        title = { Text("Filtrele", fontWeight = FontWeight.Bold, color = DarkBlue) },
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
                Text("Değerlendirme Durumuna Göre Filtrele", fontWeight = FontWeight.SemiBold, color = DarkBlue)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    val statusOptions = listOf("Tümü", "Yapıldı", "Yapılmadı")
                    var selectedIndex by remember {
                        mutableStateOf(
                            when(tempReviewedStatus) {
                                true -> 1
                                false -> 2
                                null -> 0
                            }
                        )
                    }
                    
                    statusOptions.forEachIndexed { index, text ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                selectedIndex = index
                                tempReviewedStatus = when(index) {
                                    1 -> true
                                    2 -> false
                                    else -> null
                                }
                            }
                        ) {
                             RadioButton(
                                selected = selectedIndex == index, 
                                onClick = {
                                    selectedIndex = index
                                    tempReviewedStatus = when(index) {
                                        1 -> true
                                        2 -> false
                                        else -> null
                                    }
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
                            )
                            Text(text, color = DarkBlue)
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onApply(tempSelectedClubs, tempReviewedStatus) }, colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)) { Text("Uygula") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal", color = Color.Gray) } }
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun MyReviewsCustomScrollbar(
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
                    .background(myReviewsLightPurple.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .offset(y = thumbOffsetPx.toDp())
                    .height(thumbHeightPx.toDp())
                    .width(6.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(10.dp))
                    .background(myReviewsLightPurple)
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



@Preview(showBackground = true)
@Composable
private fun MyReviewsScreenPreview() {
    _7ClubTheme {
        MyReviewsScreen(rememberNavController())
    }
}
