package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.RateReview
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue


data class MyClubsBottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun MyClubsAppBottomBar(navController: NavController) {
    val lightPurpleBarColor = Color(0xFFD1C4E9)
    val veryLightGray = Color(0xFFF8F8F8)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        MyClubsBottomNavItem(icon = Icons.Default.Schedule, label = "Katılacağım Etkinlikler", route = Routes.MyEvents.route),
        MyClubsBottomNavItem(icon = Icons.Default.Groups, label = "Kulüplerim", route = Routes.MyClubs.route),
        MyClubsBottomNavItem(icon = Icons.Outlined.RateReview, label = "Değerlendirmelerim", route = Routes.MyReviews.route),
        MyClubsBottomNavItem(icon = Icons.Default.History, label = "Geçmiş Etkinliklerim", route = Routes.PastEvents.route)
    )

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
                val firstHalf = items.subList(0, 2)
                val secondHalf = items.subList(2, 4)

                val navigateTo = { route: String ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                firstHalf.forEach { item ->
                    MyClubsAppNavItem(item = item, isSelected = currentRoute == item.route) {
                        navigateTo(item.route)
                    }
                }

                Spacer(modifier = Modifier.width(90.dp))

                secondHalf.forEach { item ->
                    MyClubsAppNavItem(item = item, isSelected = currentRoute == item.route) {
                        navigateTo(item.route)
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopCenter)
                .border(6.dp, veryLightGray, CircleShape)
                .clip(CircleShape)
                .clickable { 
                    val newRoute = if (currentRoute == Routes.Events.route) {
                        Routes.MyEvents.route
                    } else {
                        Routes.Events.route
                    }
                    navController.navigate(newRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                 },
            shape = CircleShape,
            color = DarkBlue,
            shadowElevation = 8.dp
        ) {
            // İkon kaldırıldığı için içi boş bırakıldı.
        }
    }
}


@Composable
fun RowScope.MyClubsAppNavItem(item: MyClubsBottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) DarkBlue else Color.Transparent
    val contentColor = if (isSelected) Color.White else DarkBlue

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Icon(item.icon, contentDescription = item.label, tint = contentColor, modifier = Modifier.size(28.dp))
        Text(
            text = item.label,
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            minLines = 2
        )
    }
}