package com.example.a7club.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
fun PastEventDetailScreen(navController: NavController, eventName: String) {
    var viewFormType by remember { mutableStateOf<String?>(null) }

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
        bottomBar = { PastEventBottomAppBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AKILLI GERİ OKU
            IconButton(
                onClick = { if (viewFormType != null) viewFormType = null else navController.popBackStack() }, 
                modifier = Modifier.align(Alignment.Start).padding(top = 16.dp)
            ) {
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

            if (viewFormType == null) {
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
                DetailFormButton("Etkinlik Talep Formu") { viewFormType = "Etkinlik" }
                Spacer(modifier = Modifier.height(12.dp))
                DetailFormButton("Araç Talep Formu") { viewFormType = "Araç" }
                Spacer(modifier = Modifier.height(12.dp))
                DetailFormButton("Katılımcı Bilgileri") { 
                    navController.navigate(Routes.ParticipantInfoForm.createRoute(fromNewForm = false)) 
                }
            } else {
                Text("${viewFormType} Formu İçeriği", fontWeight = FontWeight.Bold, color = DarkBlue, modifier = Modifier.padding(bottom = 16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().weight(0.8f).shadow(4.dp, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(120.dp), tint = Color(0xFF1A0273).copy(alpha = 0.3f))
                            Text("${viewFormType}_Belgesi_Arsiv.pdf", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PastEventBottomAppBar(navController: NavController) {
    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(modifier = Modifier.fillMaxWidth().height(75.dp).clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)), color = LightPurple) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                PastNavItem(Icons.Default.Groups, "Kulübüm") { navController.navigate(Routes.ClubProfileScreen.route) }
                PastNavItem(Icons.Default.Assignment, "Formlar") { navController.navigate(Routes.Forms.route) }
                Spacer(modifier = Modifier.width(90.dp))
                PastNavItem(Icons.Default.Collections, "Gönderiler") { }
                PastNavItem(Icons.Default.EventAvailable, "Etkinlikler") { navController.navigate(Routes.EventCalendarScreen.route) }
            }
        }
        Surface(modifier = Modifier.size(90.dp).align(Alignment.TopCenter).border(6.dp, Color.White, CircleShape).clickable { navController.navigate(Routes.ClubHomeScreen.route) }, shape = CircleShape, color = DarkBlue, shadowElevation = 8.dp) {}
    }
}

@Composable
fun PastNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, null, tint = DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DetailFormButton(text: String, onClick: () -> Unit) {
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
