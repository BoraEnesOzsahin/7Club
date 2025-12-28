@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.R
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple

@Composable
fun PersonnelClubDetailScreen(
    navController: NavController,
    clubName: String
) {
    // Alt barın açık/kapalı durumunu yönetmek için state ekledik
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kulüpler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
            // DÜZELTME: Yeni parametre yapısına (isMenuExpanded, onMenuToggle) uygun hale getirildi
            PersonnelMainBottomBar(
                navController = navController,
                selectedIndex = 2,
                isMenuExpanded = isMenuExpanded,
                onMenuToggle = { isMenuExpanded = !isMenuExpanded },
                onIndexSelected = { index ->
                    isMenuExpanded = false
                    if (index == 0) navController.navigate(Routes.PersonnelHomeScreen.route)
                    if (index == 1) { /* Keşfet navigasyonu buraya */ }
                    if (index == 2) { /* Zaten kulüplerdeyiz */ }
                    if (index == 3) { /* Profil navigasyonu buraya */ }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Üst Kısım: Logo + İsim
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape)) {
                    val logoRes = when {
                        clubName.contains("Kültür") -> R.drawable.yukek_logo
                        clubName.contains("Bilişim") -> R.drawable.bilisim_logo
                        clubName.contains("Psikoloji") -> R.drawable.psikoloji_logo
                        clubName.contains("Müzik") -> R.drawable.muzik_logo
                        clubName.contains("Ticaret") -> R.drawable.ticaret_logo
                        else -> R.drawable.yukek_logo
                    }
                    Image(painter = painterResource(id = logoRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = clubName, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Kulüp hakkında genel bilgi", color = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Whatsapp grup linki", color = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Yaklaşan Etkinlik", color = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("X kulübü Y etkinliği", color = DarkBlue)
                }
            }
        }
    }
}
