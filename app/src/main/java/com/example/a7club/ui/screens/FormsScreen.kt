package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

@Composable
fun FormsScreen(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            ClubAdminBottomAppBar(navController = navController, currentRoute = currentRoute)
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
                    .background(LightPurple, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Formlar", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue, modifier = Modifier.align(Alignment.Center))
                Icon(Icons.Default.Notifications, "Bildirim", tint = DarkBlue, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp).size(28.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormMenuButton("Geçmiş Etkinlik\nFormları", Modifier.weight(1f)) { navController.navigate(Routes.PastEventForms.route) }
                    FormMenuButton("Onay Bekleyen\nFormlar", Modifier.weight(1f)) { navController.navigate(Routes.PendingEventForms.route) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormMenuButton("Yeni Form Oluştur", Modifier.weight(1f)) { navController.navigate(Routes.FormSelection.route) } // DÜZELTİLDİ
                    FormMenuButton("Reddedilen Etkinlik\nFormları", Modifier.weight(1f)) { navController.navigate(Routes.RejectedEventForms.route) }
                }
            }
        }
    }
}

@Composable
fun FormMenuButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) DarkBlue else LightPurple
    val textColor = if (isPressed) Color.White else DarkBlue

    Box(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, lineHeight = 18.sp)
    }
}