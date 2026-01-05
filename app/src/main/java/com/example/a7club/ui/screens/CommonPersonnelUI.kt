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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

// --------------------------------------------------------
// 1. ANA SAYFA ALTBARI (HOME MODE)
// --------------------------------------------------------
@Composable
fun PersonnelHomeBottomBar(
    navController: NavController,
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit
) {
    BasePersonnelBottomBar(
        onCenterClick = { navController.navigate(Routes.PersonnelEventRequests.route) } // Taleplere git
    ) {
        // Sol Taraf
        PersonnelNavItem(
            icon = Icons.Default.Home,
            label = "Etkinlikler",
            isSelected = selectedIndex == 0,
            onClick = { onIndexSelected(0) }
        )
        PersonnelNavItem(
            icon = Icons.Default.Explore,
            label = "Keşfet",
            isSelected = selectedIndex == 1,
            onClick = { onIndexSelected(1) }
        )

        Spacer(modifier = Modifier.width(80.dp)) // Orta Boşluk

        // Sağ Taraf
        PersonnelNavItem(
            icon = Icons.Default.Groups,
            label = "Kulüpler",
            isSelected = selectedIndex == 2,
            onClick = { onIndexSelected(2) }
        )
        PersonnelNavItem(
            icon = Icons.Default.Person,
            label = "Profil",
            isSelected = selectedIndex == 3,
            onClick = { onIndexSelected(3) }
        )
    }
}

// --------------------------------------------------------
// 2. TALEPLER ALTBARI (REQUESTS MODE) - 2. Görsel
// --------------------------------------------------------
@Composable
fun PersonnelRequestsBottomBar(
    navController: NavController,
    currentRoute: String
) {
    BasePersonnelBottomBar(
        onCenterClick = { navController.navigate(Routes.PersonnelHomeScreen.route) } // Eve dön
    ) {
        // Sol Taraf
        PersonnelNavItem(
            icon = Icons.Default.Event, // Takvim/Plus ikonu temsili
            label = "Yeni Etkinlik\nTalepleri",
            isSelected = currentRoute == Routes.PersonnelEventRequests.route,
            onClick = { navController.navigate(Routes.PersonnelEventRequests.route) }
        )
        PersonnelNavItem(
            icon = Icons.Default.AllInbox, // Kutu ikonu temsili
            label = "Tüm Etkinlik\nTalepleri",
            isSelected = currentRoute == Routes.PersonnelOverdueEvents.route,
            onClick = { navController.navigate(Routes.PersonnelOverdueEvents.route) }
        )

        Spacer(modifier = Modifier.width(80.dp)) // Orta Boşluk

        // Sağ Taraf
        PersonnelNavItem(
            icon = Icons.Default.Groups, // Network ikonu temsili
            label = "Kulüpler",
            isSelected = false, // Genelde bu ekranda seçili olmaz veya bir yere bağlanır
            onClick = {
                // Home ekranındaki Kulüpler sekmesine yönlendiriyoruz (Parametre ile)
                navController.navigate(Routes.PersonnelHomeScreen.route)
            }
        )
        PersonnelNavItem(
            icon = Icons.Default.History, // Saat ikonu temsili
            label = "Geçmiş\nEtkinlikler",
            isSelected = currentRoute == Routes.PersonnelPastEvents.route,
            onClick = { navController.navigate(Routes.PersonnelPastEvents.route) }
        )
    }
}

// --------------------------------------------------------
// ORTAK TASARIM ALTYAPISI
// --------------------------------------------------------
@Composable
private fun BasePersonnelBottomBar(
    onCenterClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Mor Arka Plan
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = LightPurple
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp), // Padding azaltıldı, metinler sığsın diye
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }

        // Orta Yuvarlak Buton (Lacivert)
        Surface(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clickable { onCenterClick() },
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {
            // Boş bırakılabilir veya ikon konulabilir
        }
    }
}

@Composable
fun PersonnelNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
            .width(70.dp) // Genişlik sabitlendi, metinler alt alta düzgün dursun
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) DarkBlue else DarkBlue.copy(alpha = 0.5f),
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            color = if (isSelected) DarkBlue else DarkBlue.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}