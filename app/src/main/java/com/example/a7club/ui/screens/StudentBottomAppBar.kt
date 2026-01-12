package com.example.a7club.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue

@Composable
fun StudentBottomAppBar(navController: NavController) {
    val lightPurpleBarColor = Color(0xFFD1C4E9)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // --- AKILLI ORTA BUTON MANTIĞI ---
    val onMiddleButtonClick = {
        // "Genel" (Public) sayılan rotaların listesi
        val publicScreens = listOf(
            Routes.Events.route,          // Ana Etkinlikler
            Routes.Clubs.route,           // Tüm Kulüpler
            Routes.Discover.route,        // Keşfet
            Routes.StudentProfile.route,  // Profil
            Routes.EventDetail.route
        )

        // MANTIK:
        // Eğer şu an GENEL bir sayfadaysak -> KİŞİSEL Alana (SS'teki my_events_screen) git.
        if (currentRoute in publicScreens) {
            // BURASI: Ekran görüntüsündeki "navController.navigate" işleminin yapıldığı yer.
            // Routes yapını bozmamak için string yerine Routes objesini kullandık.
            navController.navigate(Routes.MyEvents.route) {
                popUpTo(Routes.Events.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
        // Eğer GENEL sayfada DEĞİLSEK (Zaten MyEvents'te isek) -> Direkt ANA SAYFAYA dön.
        else {
            navController.navigate(Routes.Events.route) {
                // Stack'i tamamen temizleyip Events'e oturuyoruz.
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Arka Plandaki Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = lightPurpleBarColor,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Etkinlikler (Genel Akış)
                StudentMainNavItem(
                    icon = Icons.Default.Event,
                    label = "Etkinlikler",
                    isSelected = currentRoute == Routes.Events.route
                ) {
                    navController.navigate(Routes.Events.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                // 2. Keşfet
                StudentMainNavItem(
                    icon = Icons.Default.Explore,
                    label = "Keşfet",
                    isSelected = currentRoute == Routes.Discover.route
                ) {
                    navController.navigate(Routes.Discover.route)
                }

                Spacer(modifier = Modifier.width(90.dp)) // Orta buton için boşluk

                // 3. Kulüpler (TÜM KULÜPLER)
                StudentMainNavItem(
                    icon = Icons.Default.Groups,
                    label = "Kulüpler",
                    isSelected = currentRoute == Routes.Clubs.route
                ) {
                    navController.navigate(Routes.Clubs.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                // 4. Profil
                StudentMainNavItem(
                    icon = Icons.Default.Person,
                    label = "Profilim",
                    isSelected = currentRoute == Routes.StudentProfile.route
                ) {
                    navController.navigate(Routes.StudentProfile.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }

        // ORTA TUŞ (Koyu Lacivert)
        Surface(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onMiddleButtonClick), // Tıklama özelliği buraya bağlandı
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {
            // İkon yok (Tasarım tercihi olarak boş bırakıldı)
        }
    }
}

@Composable
private fun StudentMainNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor = if (isSelected) DarkBlue else DarkBlue.copy(alpha = 0.6f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(28.dp))
        Text(text = label, color = iconColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}