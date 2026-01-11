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

/**
 * Öğrenci akışı için, diğer kullanıcı rolleriyle tutarlı alt navigasyon barı.
 * Ortada sadece renk içeren bir buton ve yanlarda diğer navigasyon hedeflerini içerir.
 *
 * @param navController Ana navigasyon yöneticisi.
 */
@Composable
fun StudentBottomAppBar(navController: NavController) {
    val lightPurpleBarColor = Color(0xFFD1C4E9)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val onMiddleButtonClick = {
        val destination = if (currentRoute != Routes.MyEvents.route) {
            Routes.MyEvents.route
        } else {
            Routes.Events.route
        }
        navController.navigate(destination) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Ana barın kendisi
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
                StudentMainNavItem(icon = Icons.Default.Event, label = "Etkinlikler") { navController.navigate(Routes.Events.route) }
                StudentMainNavItem(icon = Icons.Default.Explore, label = "Keşfet") { /* TODO */ }
                Spacer(modifier = Modifier.width(90.dp)) // Orta buton için boşluk
                StudentMainNavItem(icon = Icons.Default.Groups, label = "Kulüplerim") { navController.navigate(Routes.MyClubs.route) }
                StudentMainNavItem(icon = Icons.Default.Person, label = "Profilim") { /* TODO */ }
            }
        }

        // Ortadaki sade buton (İkon kaldırıldı)
        Surface(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, Color.White, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onMiddleButtonClick),
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) { 
            // İkon kaldırıldığı için içi boş bırakıldı.
        }
    }
}

/**
 * StudentBottomAppBar içindeki her bir navigasyon öğesi.
 * 'AdminNavItem' referans alınarak tasarlanmıştır.
 */
@Composable
private fun StudentMainNavItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = label, tint = DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
