@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(modifier = Modifier.background(LightPurple).padding(top = 32.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) { Icon(Icons.Default.Menu, null, tint = DarkBlue) }
                    Text("Kulüpler", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkBlue)
                    IconButton(onClick = { }) { Icon(Icons.Default.Notifications, null, tint = DarkBlue) }
                }
            }
        },
        bottomBar = {
            PersonnelMainBottomBar(
                navController = navController,
                selectedIndex = -1,
                isMenuExpanded = isMenuExpanded,
                onMenuToggle = { isMenuExpanded = !isMenuExpanded },
                onIndexSelected = { index ->
                    isMenuExpanded = false
                    navController.navigate(Routes.PersonnelHomeScreen.createRoute(index))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Geri Butonu
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kulüp İsim Kutusu
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEEEAFF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = clubName,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Üyeler
            ClubDetailMenuButton("Üyeler") { 
                navController.navigate(Routes.PersonnelClubMembers.createRoute(clubName))
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // DÜZELTME: Geçmiş Etkinlikler navigasyonu eklendi
            ClubDetailMenuButton("Geçmiş Etkinlikler") { 
                navController.navigate(Routes.PersonnelClubEvents.createRoute(clubName, true))
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // DÜZELTME: Gelecek Etkinlikler navigasyonu eklendi
            ClubDetailMenuButton("Gelecek Etkinlikler") { 
                navController.navigate(Routes.PersonnelClubEvents.createRoute(clubName, false))
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            ClubDetailMenuButton("Etkinlik Değerlendirme Sonuçları") { /* Aksiyon */ }
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
            Text(
                text = text,
                color = DarkBlue,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
