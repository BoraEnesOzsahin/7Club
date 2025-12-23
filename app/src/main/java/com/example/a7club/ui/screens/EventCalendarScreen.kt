package com.example.a7club.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCalendarScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val events = listOf(
        "Yaratıcı Yazarlık Atölyesi",
        "Coffee Talks",
        "90's Event",
        "İstiklal Gezisi",
        "Quiz Night",
        "Seramik Atölyesi"
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { /* Menü */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = DarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Bildirimler */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = DarkBlue)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
            ClubAdminBottomAppBar(navController = navController, currentRoute = Routes.EventCalendarScreen.route)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    shape = RoundedCornerShape(25.dp),
                    color = Color(0xFFF3EFFF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = DarkBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Ara...", fontSize = 14.sp, color = Color.Gray) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Icon(
                    Icons.Default.Tune,
                    contentDescription = "Filter",
                    tint = DarkBlue,
                    modifier = Modifier.size(28.dp).clickable { /* Filtrele */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            events.filter { it.contains(searchQuery, ignoreCase = true) }.forEach { eventName ->
                Button(
                    onClick = { 
                        // GÜNCELLEME: Snackbar yerine yeni sayfaya navigasyon eklendi
                        navController.navigate(Routes.ClubEventForms.createRoute(eventName)) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF3EFFF),
                        contentColor = DarkBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(text = eventName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
