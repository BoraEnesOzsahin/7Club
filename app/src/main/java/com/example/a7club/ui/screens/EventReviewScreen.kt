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
import androidx.navigation.compose.rememberNavController
import com.example.a7club.R
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme._7ClubTheme
import kotlinx.coroutines.launch

private val eventReviewLightPurple = Color(0xFFD1C4E9)
private val eventReviewVeryLightGray = Color(0xFFF8F8F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventReviewScreen(navController: NavController, eventName: String, clubName: String) {

    var satisfactionRating by remember { mutableStateOf(0) }
    var participationRating by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = eventReviewVeryLightGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Değerlendirmelerim", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                actions = { IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Notifications, "Bildirimler", tint = DarkBlue) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = eventReviewLightPurple)
            )
        },
        bottomBar = {
            MyClubsAppBottomBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    contentPadding = PaddingValues(top = 16.dp, end = 8.dp, bottom = 16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBlue)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = getClubLogo_EventReview(clubName)),
                                contentDescription = "Kulüp Logosu",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(clubName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(eventName, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        ReviewQuestion(
                            question = "Etkinlikten ne kadar memnun kaldınız?",
                            rating = satisfactionRating,
                            onRatingChange = { satisfactionRating = it }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        ReviewQuestion(
                            question = "Bu kulübün başka bir etkinliğine katılır mıydınız?",
                            rating = participationRating,
                            onRatingChange = { participationRating = it }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { /* TODO: Submit review and pop back */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                        ) {
                            Text("Gönder", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
                EventReviewCustomScrollbar(
                    state = listState,
                    modifier = Modifier.padding(start = 4.dp).fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun ReviewQuestion(question: String, rating: Int, onRatingChange: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEDE7F6))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(question, color = DarkBlue, fontSize = 16.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        StarRating(rating = rating, onRatingChange = onRatingChange)
    }
}

@Composable
private fun StarRating(rating: Int, onRatingChange: (Int) -> Unit) {
    val lilacColor = Color(0xFFD1C4E9)
    Row(horizontalArrangement = Arrangement.Center) {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star $i",
                tint = if (i <= rating) lilacColor else Color.Gray,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onRatingChange(i) }
                    .padding(4.dp)
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun EventReviewCustomScrollbar(
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
                    .background(eventReviewLightPurple.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .offset(y = thumbOffsetPx.toDp())
                    .height(thumbHeightPx.toDp())
                    .width(6.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(10.dp))
                    .background(eventReviewLightPurple)
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

private fun getClubLogo_EventReview(clubName: String): Int {
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
fun EventReviewScreenPreview() {
    _7ClubTheme {
        EventReviewScreen(
            navController = rememberNavController(),
            eventName = "Yaratıcı Yazarlık Atölyesi",
            clubName = "Kültür ve Etkinlik Kulübü"
        )
    }
}
