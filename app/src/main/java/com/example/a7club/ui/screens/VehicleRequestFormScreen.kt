package com.example.a7club.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRequestFormScreen(navController: NavController) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) } 

    // PDF Seçici Launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = null,
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Formu göndermek\nistediğinizden emin\nmisiniz?", textAlign = TextAlign.Center, color = Color(0xFF1A0273), fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { showConfirmDialog = false; showSuccessDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0273)), shape = RoundedCornerShape(12.dp), modifier = Modifier.width(100.dp)) { Text("Evet", color = Color.White) }
                        Button(onClick = { showConfirmDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0273)), shape = RoundedCornerShape(12.dp), modifier = Modifier.width(100.dp)) { Text("Hayır", color = Color.White) }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color(0xFFBBDEFB),
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = null,
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Form başarıyla\ngönderildi.", textAlign = TextAlign.Center, color = Color(0xFF1A0273), fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { showSuccessDialog = false; navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0273)), shape = RoundedCornerShape(12.dp), modifier = Modifier.align(Alignment.End).width(100.dp)) { Text("Tamam", color = Color.White) }
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
        bottomBar = { VehicleFormBottomAppBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.Start).padding(top = 16.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9))) {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Araç Talep Formu", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedFileUri == null) {
                Button(
                    onClick = { pdfPickerLauncher.launch("application/pdf") }, 
                    modifier = Modifier.fillMaxWidth(0.9f).height(70.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0273))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Form Yükle", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(16.dp))
                        Box(modifier = Modifier.size(45.dp).background(Color.White, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.NoteAdd, "Yükle", tint = Color(0xFF1A0273), modifier = Modifier.size(30.dp))
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f).weight(0.7f).shadow(4.dp, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(100.dp), tint = Color(0xFF1A0273).copy(alpha = 0.3f))
                            Text("Araç_Talep_Belgesi.pdf", color = Color.Gray, fontSize = 14.sp)
                            TextButton(onClick = { selectedFileUri = null }) { Text("Değiştir", color = Color.Red) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // GÜNCELLENDİ: "Atla" yerine "İlerle" butonu eklendi ve aktif edildi
            Button(
                onClick = { if(selectedFileUri != null) showConfirmDialog = true },
                modifier = Modifier.align(Alignment.End).width(100.dp).height(45.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1C4E9)),
                enabled = selectedFileUri != null
            ) {
                Text("İlerle", color = DarkBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun VehicleFormBottomAppBar(navController: NavController) {
    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(modifier = Modifier.fillMaxWidth().height(75.dp).clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)), color = LightPurple) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                VehicleFormNavItem(Icons.Default.Groups, "Kulübüm") { navController.navigate(Routes.ClubProfileScreen.route) }
                VehicleFormNavItem(Icons.Default.Assignment, "Formlar") { navController.navigate(Routes.Forms.route) }
                Spacer(modifier = Modifier.width(90.dp))
                VehicleFormNavItem(Icons.Default.Collections, "Gönderiler") { }
                VehicleFormNavItem(Icons.Default.EventAvailable, "Etkinlikler") { navController.navigate(Routes.EventCalendarScreen.route) }
            }
        }
        Surface(modifier = Modifier.size(90.dp).align(Alignment.TopCenter).border(6.dp, Color.White, CircleShape).clickable { navController.navigate(Routes.ClubHomeScreen.route) }, shape = CircleShape, color = DarkBlue, shadowElevation = 8.dp) {}
    }
}

@Composable
fun VehicleFormNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, contentDescription = label, tint = DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
