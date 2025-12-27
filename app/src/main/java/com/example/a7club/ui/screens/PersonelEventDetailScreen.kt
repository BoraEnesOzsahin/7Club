@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.VeryLightPurple

@Composable
fun PersonnelEventDetailScreen(
    navController: NavController,
    eventName: String,
    clubName: String
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                }
                Box(modifier = Modifier.size(40.dp).background(Color(0xFFD32F2F), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = clubName, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 14.sp, style = MaterialTheme.typography.bodyLarge.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline))
            }
        },
        bottomBar = {
            // DÜZELTME: PersonnelMainBottomBar'a navController parametresi eklendi
            PersonnelMainBottomBar(
                navController = navController,
                selectedIndex = 0,
                onIndexSelected = { index ->
                   if (index == 0) navController.navigate("personnel_home_screen")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth().background(VeryLightPurple, RoundedCornerShape(12.dp)).padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = eventName.uppercase(), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "$eventName etkinliğine katılmak\nistediğine emin misiniz?", textAlign = TextAlign.Center, color = DarkBlue, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(40.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = DarkBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.width(100.dp)) { Text("Hayır") }
                        Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = DarkBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.width(100.dp)) { Text("Evet") }
                    }
                }
            }
        }
    }
}
