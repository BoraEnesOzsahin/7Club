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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.theme._7ClubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubProfileScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    Scaffold(
        containerColor = VeryLightPurple,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profilim", fontWeight = FontWeight.Bold) },
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
             ClubBottomAppBar(navController = navController)
        },
        floatingActionButton = {
             FloatingActionButton(
                onClick = { /* No action */ },
                shape = CircleShape,
                containerColor = DarkBlue,
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
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(LightPurple)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightPurple)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text("Kulüp Adı", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Routes.RoleSelection.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightPurple,
                    contentColor = DarkBlue
                )
            ) {
                Text("Oturumu Kapat", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true, name = "Club Profile Screen Preview")
@Composable
fun ClubProfileScreenPreview() {
    _7ClubTheme {
        ClubProfileScreen(
            navController = rememberNavController(), 
            showSnackbar = {}
        )
    }
}
