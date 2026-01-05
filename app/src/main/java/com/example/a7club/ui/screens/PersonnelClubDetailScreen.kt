@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

@Composable
fun PersonnelClubDetailScreen(
    navController: NavController,
    clubName: String
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(clubName, fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        // GÜNCELLEME: Ana Sayfa menüsü (Home Bar) kullanılıyor
        // selectedIndex = 2 (Kulüpler sekmesi aktif görünsün diye)
        bottomBar = {
            PersonnelHomeBottomBar(
                navController = navController,
                selectedIndex = 2,
                onIndexSelected = {
                    // Menüden başka bir yere tıklanırsa Ana Sayfaya dön
                    navController.navigate(Routes.PersonnelHomeScreen.route)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            ClubDetailMenuButton("Üye Listesi") {
                navController.navigate(Routes.PersonnelClubMembers.createRoute(clubName))
            }
            Spacer(modifier = Modifier.height(16.dp))

            ClubDetailMenuButton("Geçmiş Etkinlikler") {
                navController.navigate(Routes.PersonnelClubEvents.createRoute(clubName, true))
            }
            Spacer(modifier = Modifier.height(16.dp))

            ClubDetailMenuButton("Gelecek Etkinlikler") {
                navController.navigate(Routes.PersonnelClubEvents.createRoute(clubName, false))
            }
        }
    }
}

@Composable
fun ClubDetailMenuButton(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(55.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFEEEAFF)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}