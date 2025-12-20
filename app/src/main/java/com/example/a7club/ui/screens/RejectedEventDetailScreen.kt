package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectedEventDetailScreen(navController: NavController, eventName: String) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Formlar", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = { IconButton(onClick = { }) { Icon(Icons.Default.Menu, "Menu", tint = DarkBlue) } },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue) } },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(16.dp)),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = { ClubBottomAppBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.Start).padding(top = 16.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFFF))
            ) {
                Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                    Text(eventName, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFFF))
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Etkinlik Yeri: Yeditepe Üniversitesi Kampüsü", color = DarkBlue, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Etkinlik Saati: 06.00", color = DarkBlue, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("İletişim Tel No: 0526 254 2598", color = DarkBlue, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            RejectedDetailFormButton("Etkinlik Talep Formu") { }
            Spacer(modifier = Modifier.height(12.dp))
            RejectedDetailFormButton("Araç Talep Formu") { }
            Spacer(modifier = Modifier.height(12.dp))
            RejectedDetailFormButton("Katılımcı Bilgileri") { }
        }
    }
}

@Composable
fun RejectedDetailFormButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1C4E9), contentColor = DarkBlue),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
