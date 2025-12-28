package com.example.a7club.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun ClubAdminBottomAppBar(navController: NavController, currentRoute: String = "") {
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
                AdminNavItem(Icons.Default.Groups, "Kulübüm", isSelected = currentRoute == Routes.ClubProfileScreen.route) { 
                    navController.navigate(Routes.ClubProfileScreen.route) 
                }
                AdminNavItem(Icons.Default.Assignment, "Formlar", isSelected = currentRoute == Routes.Forms.route) { 
                    navController.navigate(Routes.Forms.route) 
                }
                Spacer(modifier = Modifier.width(90.dp))
                AdminNavItem(Icons.Default.Collections, "Gönderiler", isSelected = currentRoute == Routes.ClubPosts.route) { 
                    navController.navigate(Routes.ClubPosts.route) 
                }
                AdminNavItem(Icons.Default.EventAvailable, "Etkinlikler", isSelected = currentRoute == Routes.EventCalendarScreen.route) { 
                    navController.navigate(Routes.EventCalendarScreen.route) 
                }
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
fun AdminNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            icon, 
            contentDescription = label, 
            tint = if (isSelected) Color.White else DarkBlue, 
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label, 
            color = if (isSelected) Color.White else DarkBlue, 
            fontSize = 11.sp, 
            fontWeight = FontWeight.Bold
        )
    }
}
