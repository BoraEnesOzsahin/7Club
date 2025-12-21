package com.example.a7club.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.R
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubPostsScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Gönderiler") }

    Scaffold(
        containerColor = VeryLightPurple,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gönderiler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Menü Aksiyonu */ }) {
                        Icon(Icons.Default.Menu, "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Bildirim Aksiyonu */ }) {
                        Icon(Icons.Default.Notifications, "Notifications")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
            PostsBottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sekmeler (Duyurular / Gönderiler)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TabItem(
                    text = "Duyurular",
                    isSelected = selectedTab == "Duyurular",
                    onClick = { selectedTab = "Duyurular" }
                )
                Spacer(modifier = Modifier.width(8.dp))
                TabItem(
                    text = "Gönderiler",
                    isSelected = selectedTab == "Gönderiler",
                    onClick = { selectedTab = "Gönderiler" }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ekleme Butonu (+)
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                tint = DarkBlue,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { /* Yeni Gönderi Ekleme */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gönderi Listesi
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(3) { // Örnek veri
                    PostCard()
                }
            }
        }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) DarkBlue else Color(0xFFD1C4E9),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else DarkBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PostCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9).copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Görsel Alanı (Placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
            ) {
                // Buraya gerçek görsel gelecek
                Text("Görsel Alanı", modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Kulüp Logosu (Placeholder)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Women in Business Kulübü",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkBlue
                )
            }
            
            Text(
                text = "Etkinliğimize davetlisiniz!",
                fontSize = 13.sp,
                color = DarkBlue,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    }
}

@Composable
fun PostsBottomAppBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = LightPurple
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostNavItem(Icons.Default.Groups, "Kulübüm") { navController.navigate(Routes.ClubHomeScreen.route) }
                PostNavItem(Icons.Default.Assignment, "Formlar") { navController.navigate(Routes.Forms.route) }
                Spacer(modifier = Modifier.width(90.dp))
                PostNavItem(Icons.Default.Collections, "Gönderiler", isSelected = true) { /* Buradayız */ }
                PostNavItem(Icons.Default.EventAvailable, "Etkinlikler") { navController.navigate(Routes.EventCalendarScreen.route) }
            }
        }

        Surface(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clickable { navController.navigate(Routes.ClubProfileScreen.route) },
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {}
    }
}

@Composable
fun PostNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = label, tint = DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
