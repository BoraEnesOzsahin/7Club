package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
// Correctly importing BOTH colors from the central theme package
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.theme._7ClubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCalendarScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    Scaffold(
        containerColor = VeryLightPurple, // Uses the imported color
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlik Takvimi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Bildirimler")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LightPurple // Uses the imported color
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder for the calendar view
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightPurple, RoundedCornerShape(16.dp)), // Uses the imported color with correct casing
                contentAlignment = Alignment.Center
            ) {
                Text("Etkinlik Takvimi İçeriği Buraya Gelecek")
            }
        }
    }
}

@Preview(showBackground = true, name = "Event Calendar Screen Preview")
@Composable
fun EventCalendarScreenPreview() {
    _7ClubTheme {
        EventCalendarScreen(
            navController = rememberNavController(),
            showSnackbar = {}
        )
    }
}
