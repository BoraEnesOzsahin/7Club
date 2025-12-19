package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// Provided color palette
val darkBlue = Color(0xFF160092)
val lightPurple = Color(0xFFCCC2FF)
val veryLightPurple = Color(0xFFEEEBFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubHomeScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.7f),
                drawerContainerColor = veryLightPurple
            ) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { /* TODO: Navigate to Settings */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Ayarlar")
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Navigate to Event Calendar */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Etkinlik Takvimi")
                }
            }
        }
    ) {
        Scaffold(
            containerColor = veryLightPurple,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Etkinlikler", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = lightPurple
                    )
                )
            },
            bottomBar = {
                ClubBottomAppBar(navController = navController)
            },
            floatingActionButton = {
                 FloatingActionButton(
                    onClick = { /* Do nothing for now */ },
                    shape = CircleShape,
                    containerColor = darkBlue,
                ) {
                    // You can add an icon here if you want
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            // The main content of the screen will go here
            // For now, it can be a placeholder
            Column(modifier = Modifier.padding(paddingValues)) {
                // We will add the events list here later
            }
        }
    }
}

@Composable
fun ClubBottomAppBar(navController: NavController) {
    BottomAppBar(
        containerColor = lightPurple,
        contentColor = darkBlue,
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO: Navigate to Home */ },
            icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = darkBlue,
                unselectedIconColor = darkBlue.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to Announcements */ },
            icon = { Icon(Icons.Default.List, contentDescription = "Duyurular") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = darkBlue,
                unselectedIconColor = darkBlue.copy(alpha = 0.6f)
            )
        )
        // Spacer for the FAB
        Spacer(modifier = Modifier.weight(1f))
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to Clubs List */ },
            icon = { Icon(Icons.Default.List, contentDescription = "Kulüpler Listesi") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = darkBlue,
                unselectedIconColor = darkBlue.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to Profile */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profilim") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = darkBlue,
                unselectedIconColor = darkBlue.copy(alpha = 0.6f)
            )
        )
    }
}
