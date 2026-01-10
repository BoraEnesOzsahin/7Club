package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun FormsScreen(navController: NavController) {
    val HeaderColor = Color(0xFFD1C4E9)
    val ButtonLightColor = Color(0xFFD1C4E9)
    val ButtonDarkColor = DarkBlue

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            // ClubProfileScreen içindeki ortak yapıyı kullanıyoruz
            ClubAdminBottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(HeaderColor, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Menu, "Menu", tint = DarkBlue, modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp).size(28.dp))
                Text("Formlar", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue, modifier = Modifier.align(Alignment.Center))
                Icon(Icons.Default.Notifications, "Bildirim", tint = DarkBlue, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp).size(28.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormMenuButton("Geçmiş Etkinlik\nFormları", ButtonLightColor, DarkBlue, Modifier.weight(1f)) { navController.navigate(Routes.PastEventForms.route) }
                    FormMenuButton("Onay Bekleyen\nFormlar", ButtonLightColor, DarkBlue, Modifier.weight(1f)) { navController.navigate(Routes.PendingEventForms.route) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormMenuButton("Yeni Form Oluştur", ButtonDarkColor, Color.White, Modifier.weight(1f)) { navController.navigate(Routes.CreateEvent.route) }
                    FormMenuButton("Reddedilen Etkinlik\nFormları", ButtonLightColor, DarkBlue, Modifier.weight(1f)) { navController.navigate(Routes.RejectedEventForms.route) }
                }
            }
        }
    }
}

@Composable
fun FormMenuButton(text: String, backgroundColor: Color, textColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, lineHeight = 18.sp)
    }
}