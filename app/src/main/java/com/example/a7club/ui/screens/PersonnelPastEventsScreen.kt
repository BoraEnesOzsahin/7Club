@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun PersonnelPastEventsScreen(
    navController: NavController,
    viewModel: PersonnelViewModel = viewModel()
) {
    val pastEvents by viewModel.pastEvents.collectAsState()
    
    // DÜZELTME: Bu ekran bir yönetim ekranı olduğu için bar hep genişlemiş (true) kalmalı
    var isMenuExpanded by remember { mutableStateOf(true) }

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
                    Text("Geçmiş Etkinlikler", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkBlue)
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
                    if (index == 0) navController.navigate(Routes.PersonnelHomeScreen.createRoute(0))
                    if (index == 1) navController.navigate(Routes.PersonnelHomeScreen.createRoute(1))
                    if (index == 2) navController.navigate(Routes.PersonnelHomeScreen.createRoute(2))
                    if (index == 3) navController.navigate(Routes.PersonnelHomeScreen.createRoute(3))
                    if (index == 4) navController.navigate(Routes.PersonnelHomeScreen.createRoute(4))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = DarkBlue)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (pastEvents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Henüz geçmiş bir etkinlik bulunmuyor.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                    items(pastEvents) { event ->
                        PastEventCard(event)
                    }
                }
            }
        }
    }
}

@Composable
fun PastEventCard(event: com.example.a7club.model.Event) {
    val statusColor = when(event.status) {
        "Verified" -> Color(0xFF4CAF50)
        "Rejected" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(event.title, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 16.sp)
                Text(event.clubName, color = DarkBlue.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            Surface(
                color = statusColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (event.status == "Verified") "ONAYLANDI" else "REDDEDİLDİ",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
