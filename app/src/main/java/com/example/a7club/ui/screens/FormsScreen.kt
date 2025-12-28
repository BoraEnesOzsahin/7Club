package com.example.a7club.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormsScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showSelectionDialog by remember { mutableStateOf(false) }

    val pendingEvents = listOf("Quiz Night", "Seramik Atölyesi")
    val pastEvents = listOf("Yaratıcı Yazarlık Atölyesi", "Coffee Talks", "90's Event", "İstiklal Gezisi")
    val rejectedEvents = listOf("Takı Atölyesi", "Oyun Gecesi")

    if (showSelectionDialog) {
        AlertDialog(
            onDismissRequest = { showSelectionDialog = false },
            title = { Text("Form Tipi Seçin", color = DarkBlue, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { 
                            showSelectionDialog = false
                            navController.navigate(Routes.EventRequestForm.route) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1C4E9), contentColor = DarkBlue)
                    ) { Text("Etkinlik Talep Formu") }
                    
                    Button(
                        onClick = { 
                            showSelectionDialog = false
                            navController.navigate(Routes.VehicleRequestForm.route) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1C4E9), contentColor = DarkBlue)
                    ) { Text("Araç Talep Formu") }

                    Button(
                        onClick = { 
                            showSelectionDialog = false
                            navController.navigate(Routes.ParticipantInfoForm.createRoute(fromNewForm = true)) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1C4E9), contentColor = DarkBlue)
                    ) { Text("Katılımcı Bilgileri Formu") }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSelectionDialog = false }) { Text("İptal", color = Color.Gray) }
            },
            containerColor = Color.White
        )
    }

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
        bottomBar = { 
            FormsBottomAppBar(navController = navController) 
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
            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FormMenuButton(text = "Geçmiş Etkinlik\nFormları", isSelected = selectedCategory == "PAST") { 
                    selectedCategory = if (selectedCategory == "PAST") null else "PAST"
                }
                FormMenuButton(text = "Onay Bekleyen\nFormlar", isSelected = selectedCategory == "PENDING") { 
                    selectedCategory = if (selectedCategory == "PENDING") null else "PENDING"
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FormMenuButton(text = "Yeni Form Oluştur") { showSelectionDialog = true }
                FormMenuButton(text = "Reddedilen Etkinlik\nFormları", isSelected = selectedCategory == "REJECTED") { 
                    selectedCategory = if (selectedCategory == "REJECTED") null else "REJECTED"
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            val currentList = when (selectedCategory) {
                "PAST" -> pastEvents
                "PENDING" -> pendingEvents
                "REJECTED" -> rejectedEvents
                else -> emptyList()
            }

            currentList.forEach { eventName ->
                Button(
                    onClick = { 
                        when(selectedCategory) {
                            "PAST" -> navController.navigate(Routes.PastEventDetail.createRoute(eventName))
                            "PENDING" -> navController.navigate(Routes.PendingEventDetail.createRoute(eventName))
                            "REJECTED" -> navController.navigate(Routes.RejectedEventDetail.createRoute(eventName))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.85f).height(60.dp).padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3EFFF), contentColor = DarkBlue),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = eventName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FormsBottomAppBar(navController: NavController) {
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
                FormsNavItem(Icons.Default.Groups, "Kulübüm") { navController.navigate(Routes.ClubProfileScreen.route) }
                FormsNavItem(Icons.Default.Assignment, "Formlar") { /* Zaten buradayız */ }
                Spacer(modifier = Modifier.width(90.dp))
                // GÜNCELLENDİ: Navigasyon rotası eklendi
                FormsNavItem(Icons.Default.Collections, "Gönderiler") { navController.navigate(Routes.ClubPosts.route) }
                FormsNavItem(Icons.Default.EventAvailable, "Etkinlikler") { navController.navigate(Routes.EventCalendarScreen.route) }
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
fun FormsNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
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
fun FormMenuButton(text: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 150.dp, height = 80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF1A0273) else Color(0xFFD1C4E9),
            contentColor = if (isSelected) Color.White else DarkBlue
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(text = text, textAlign = TextAlign.Center, fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
    }
}
