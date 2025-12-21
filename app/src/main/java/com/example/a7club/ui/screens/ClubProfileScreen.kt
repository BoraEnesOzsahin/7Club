package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme._7ClubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubProfileScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kulübüm", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = DarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", tint = DarkBlue)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
             ClubAdminBottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(Color(0xFFB04A33), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("YUKEK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(48.dp))

            ClubAdminButton(text = "Üyeler") {
                navController.navigate(Routes.MembersScreen.route)
            }

            Spacer(modifier = Modifier.height(16.dp))

            ClubAdminButton(text = "İletişim Bilgileri") {
                navController.navigate(Routes.ContactInfoScreen.route)
            }
        }
    }
}

@Composable
fun ClubAdminBottomAppBar(navController: NavController) {
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
                AdminNavItem(Icons.Default.Groups, "Kulübüm") { /* Buradayız */ }
                AdminNavItem(Icons.Default.Assignment, "Formlar") { navController.navigate(Routes.Forms.route) }
                Spacer(modifier = Modifier.width(90.dp))
                // GÜNCELLENDİ: "Gönderiler" butonu artık yeni sayfaya gidiyor
                AdminNavItem(Icons.Default.Collections, "Gönderiler") { navController.navigate(Routes.ClubPosts.route) }
                AdminNavItem(Icons.Default.EventAvailable, "Etkinlikler") { navController.navigate(Routes.EventCalendarScreen.route) }
            }
        }
        Surface(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clickable { 
                    // Orta tuş: Ana Sayfaya (Öğrenci Görünümüne) geri döner
                    navController.navigate(Routes.ClubHomeScreen.route) 
                },
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {}
    }
}

@Composable
fun AdminNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = label, tint = DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ClubAdminButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF3EFFF),
            contentColor = DarkBlue
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
