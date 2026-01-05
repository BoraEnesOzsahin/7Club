package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubEventFormsScreen(navController: NavController, eventName: String) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Form İşlemleri", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        // GÜNCELLEME: Ortak BottomBar kullanılıyor
        bottomBar = {
            ClubAdminBottomAppBar(navController, Routes.Forms.route)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Seçilen Etkinlik Başlığı
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFFF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Seçilen Etkinlik:", fontSize = 14.sp, color = Color.Gray)
                    Text(text = eventName, color = DarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Form Butonları
            FormActionButton(text = "Etkinlik Talep Formu (PDF)") {
                // Etkinlik Onay Formu sayfasına git
                navController.navigate(Routes.EventRequestForm.createRoute(eventName))
            }

            Spacer(modifier = Modifier.height(16.dp))

            FormActionButton(text = "Araç Talep Formu") {
                // Araç Talep sayfasına git
                navController.navigate(Routes.VehicleRequestForm.createRoute(eventName))
            }

            Spacer(modifier = Modifier.height(16.dp))

            FormActionButton(text = "Katılımcı Listesi") {
                // Katılımcı listesi (henüz mockup)
                navController.navigate(Routes.ParticipantInfoForm.createRoute(false))
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// BU DOSYAYA ÖZEL BUTON TASARIMI (KALACAK)
@Composable
fun FormActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD1C4E9),
            contentColor = DarkBlue
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// DİKKAT: 'fun AdminNavItem' BURADAN SİLİNDİ. (CommonAdminUI.kt kullanılıyor)