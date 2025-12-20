package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
fun ParticipantInfoFormScreen(navController: NavController, fromNewForm: Boolean) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val participants = remember { 
        mutableStateListOf(
            Pair("12345678910", "Fatma Zülal Baltacı"),
            Pair("12345678910", "Bora Enes Özşahin"),
            Pair("12345678910", "Yağmur Direkçi"),
            Pair("12345678910", "Sami Sidar"),
            Pair("12345678910", "Azra Sağdıç"),
            Pair("12345678910", "Neslihan ERDEM"),
            Pair("12345678910", "Sude ADISANOĞLU"),
            Pair("12345678910", "Ege ÖRTER"),
            Pair("12345678910", "Sevim Nazlı YEŞİLOVA")
        )
    }

    if (showConfirmDialog && fromNewForm) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bu formun iletilmesini\nistiyor musunuz?", textAlign = TextAlign.Center, color = Color(0xFF1A0273), fontWeight = FontWeight.Medium, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { showConfirmDialog = false; showSuccessDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0273))) { Text("Evet") }
                        Button(onClick = { showConfirmDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0273))) { Text("Hayır") }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color(0xFFBBDEFB),
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showSuccessDialog && fromNewForm) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Form başarıyla\ngönderildi.", color = Color(0xFF1A0273), fontWeight = FontWeight.Medium, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { showSuccessDialog = false; navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0273))) { Text("Tamam") }
                }
            },
            confirmButton = {},
            containerColor = Color(0xFFBBDEFB),
            shape = RoundedCornerShape(24.dp)
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
        bottomBar = { ParticipantFormBottomAppBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.Start).padding(top = 16.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9))) {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Katılımcı Bilgileri", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFFF))
            ) {
                LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    itemsIndexed(participants) { index, participant ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("${index + 1}", modifier = Modifier.width(30.dp), fontWeight = FontWeight.ExtraBold, color = DarkBlue, fontSize = 14.sp)
                            Text(participant.first, modifier = Modifier.width(110.dp), color = DarkBlue.copy(alpha = 0.8f), fontSize = 14.sp)
                            Text(participant.second, color = DarkBlue, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
            }

            // SADECE YENİ FORM YOLUNDA İLERLE BUTONU GÖRÜNÜR
            if (fromNewForm) {
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.align(Alignment.End).width(100.dp).height(45.dp).padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1C4E9))
                ) {
                    Text("İlerle", color = DarkBlue, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ParticipantFormBottomAppBar(navController: NavController) {
    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(modifier = Modifier.fillMaxWidth().height(75.dp).clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)), color = LightPurple) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                ParticipantNavItem(Icons.Default.Groups, "Kulübüm") { navController.navigate(Routes.ClubProfileScreen.route) }
                ParticipantNavItem(Icons.Default.Assignment, "Formlar") { navController.navigate(Routes.Forms.route) }
                Spacer(modifier = Modifier.width(90.dp))
                ParticipantNavItem(Icons.Default.Collections, "Gönderiler") { }
                ParticipantNavItem(Icons.Default.EventAvailable, "Etkinlikler") { navController.navigate(Routes.EventCalendarScreen.route) }
            }
        }
        Surface(modifier = Modifier.size(90.dp).align(Alignment.TopCenter).border(6.dp, Color.White, CircleShape).clickable { navController.navigate(Routes.ClubHomeScreen.route) }, shape = CircleShape, color = DarkBlue, shadowElevation = 8.dp) {}
    }
}

@Composable
fun ParticipantNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, null, tint = DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
