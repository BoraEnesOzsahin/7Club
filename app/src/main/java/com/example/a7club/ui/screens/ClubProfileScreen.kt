@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

@Composable
fun ClubProfileScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kulübüm", fontWeight = FontWeight.Bold, color = DarkBlue) },
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
            ClubAdminBottomAppBar(navController = navController, currentRoute = currentRoute)
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
fun ClubAdminBottomAppBar(navController: NavController, currentRoute: String?) {
    val LightPurpleBarColor = Color(0xFFD1C4E9)
    Box(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = LightPurpleBarColor
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AdminNavItem("Kulübüm", Icons.Default.Groups, currentRoute == Routes.ClubProfileScreen.route) { navController.navigate(Routes.ClubProfileScreen.route) }
                AdminNavItem("Formlar", Icons.Default.Assignment, currentRoute == Routes.Forms.route) { navController.navigate(Routes.Forms.route) }
                Spacer(modifier = Modifier.width(90.dp))
                AdminNavItem("Gönderiler", Icons.Default.Collections, currentRoute == Routes.ClubPosts.route) { navController.navigate(Routes.ClubPosts.route) }
                AdminNavItem("Etkinlikler", Icons.Default.EventAvailable, currentRoute == Routes.EventCalendarScreen.route) { navController.navigate(Routes.EventCalendarScreen.route) }
            }
        }
        Surface(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clickable { navController.navigate(Routes.ClubHomeScreen.route) },
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {}
    }
}

@Composable
fun AdminNavItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) DarkBlue else Color.Transparent
    val contentColor = if (isSelected) Color.White else DarkBlue

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .width(70.dp) // Genişliği sabitledik
    ) {
        Icon(icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(28.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            minLines = 2
        )
    }
}

@Composable
fun ClubAdminButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3EFFF), contentColor = DarkBlue),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}