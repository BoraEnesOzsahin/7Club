package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubProfileScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    Scaffold(
        containerColor = veryLightPurple,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profilim", fontWeight = FontWeight.Bold) },
                // We might not need a hamburger here, but keeping for consistency
                navigationIcon = {
                    IconButton(onClick = { /* Maybe open drawer or pop back */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Bildirimler")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = lightPurple)
            )
        },
        // We reuse the bottom app bar for consistent navigation
        bottomBar = {
             ClubBottomAppBar(navController = navController)
        },
        floatingActionButton = {
             FloatingActionButton(
                onClick = { /* No action */ },
                shape = CircleShape,
                containerColor = darkBlue,
            ) {}
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile Picture Placeholder
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(lightPurple)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Club Name Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = lightPurple)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text("Kulüp Adı", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Log Out Button
            Button(
                onClick = {
                    // Navigate back to the very first screen, clearing everything
                    navController.navigate(Routes.RoleSelection.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightPurple,
                    contentColor = darkBlue
                )
            ) {
                Text("Oturumu Kapat", fontSize = 18.sp)
            }
        }
    }
}
