package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.a7club.ui.viewmodels.AuthViewModel
@Composable
fun ProfileScreenContent(navController: NavController, authViewModel: AuthViewModel) {
    // Paylaştığın StudentProfileScreen içindeki Column içeriğini buraya koy.
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel // ViewModel parametresi eklendi
) {
    // Sayfa açıldığında veriyi çekmesi için tetikleyici
    LaunchedEffect(Unit) {
        authViewModel.fetchStudentProfile()
    }

    // ViewModel'daki isim değişkenini dinle (Canlı takip)
    val studentName by authViewModel.currentStudentName.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profilim",
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Menü açma işlemi */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menü",
                            tint = DarkBlue
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // BURAYI KONTROL ET: Tıklanınca bu log veya print çalışıyor mu?
                            println("Bildirim butonuna basıldı!")
                            navController.navigate(Routes.NotificationsScreen.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Bildirimler",
                            tint = DarkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LightPurple
                )
            )
        },
        bottomBar = {
            StudentBottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 1. Profil Resmi (Placeholder)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFECE6F0)) // Açık lila tonu
            )

            Spacer(modifier = Modifier.height(60.dp))

            // 2. İsim Kutusu (Veritabanından Gelen Veri)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(70.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = studentName, // BURASI ARTIK DİNAMİK
                    fontSize = 20.sp,
                    color = DarkBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Oturumu Kapat Butonu
            Button(
                onClick = {
                    authViewModel.signOut() // Çıkış fonksiyonunu tetikle
                    navController.navigate(Routes.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(70.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEDE7F6),
                    contentColor = DarkBlue
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = "Oturumu Kapat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}