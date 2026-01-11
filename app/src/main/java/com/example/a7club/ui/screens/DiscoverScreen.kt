

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue

// Tasarım Renkleri
private val InactiveTabColor = Color(0xFFD1C4E9) // Soluk Mor
private val CardBackgroundColor = Color(0xFFF3EFFF) // Kartlar için açık lila

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(navController: NavController) {
    // 0 -> Duyurular, 1 -> Gönderiler
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("Keşfet", fontWeight = FontWeight.Bold, color = DarkBlue) },
                    actions = {
                        IconButton(onClick = { navController.navigate(Routes.NotificationsScreen.route) }) {
                            Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )

                // Arama Çubuğu
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Duyuru veya gönderi ara...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = DarkBlue) },
                    shape = RoundedCornerShape(32.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF8F8F8), // Çok açık gri
                        focusedContainerColor = Color(0xFFF8F8F8),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        },
        bottomBar = {
            StudentBottomAppBar(navController = navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- TAB BUTONLARI (DUYURULAR / GÖNDERİLER) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Duyurular Butonu
                Button(
                    onClick = { selectedTab = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) DarkBlue else InactiveTabColor,
                        contentColor = if (selectedTab == 0) Color.White else DarkBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                ) {
                    Text("Duyurular", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Gönderiler Butonu
                Button(
                    onClick = { selectedTab = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 1) DarkBlue else InactiveTabColor,
                        contentColor = if (selectedTab == 1) Color.White else DarkBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                ) {
                    Text("Gönderiler", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- İÇERİK LİSTESİ ---
            Row(modifier = Modifier.fillMaxSize()) {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    if (selectedTab == 0) {
                        // DUYURULAR LİSTESİ
                        items(4) {
                            StudentAnnouncementCard()
                        }
                    } else {
                        // GÖNDERİLER LİSTESİ
                        items(3) {
                            StudentPostCard()
                        }
                    }
                }

                // Sağdaki Dekoratif Çizgi
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .width(4.dp)
                        .fillMaxHeight(0.8f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(InactiveTabColor.copy(alpha = 0.5f))
                        .align(Alignment.Top)
                )
            }
        }
    }
}

// --- YARDIMCI KARTLAR ---

@Composable
fun StudentAnnouncementCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("K", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Kültür ve Etkinlik Kulübü", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 13.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text("2s önce", color = Color.Gray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp).background(DarkBlue.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth(0.6f).height(12.dp).background(DarkBlue.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
fun StudentPostCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(InactiveTabColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Bilişim Kulübü", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 14.sp)
                    Text("Yeni etkinliğimizden kareler!", color = DarkBlue.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}