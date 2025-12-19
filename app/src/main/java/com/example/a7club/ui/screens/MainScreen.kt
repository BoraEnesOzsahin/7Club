package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select an Action")
        Spacer(modifier = Modifier.height(16.dp))

        // Fixed: Removed .route
        Button(onClick = { navController.navigate(Routes.CreateVehicleRequest) }) {
            Text("Submit Vehicle Request")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fixed: Removed .route
        Button(onClick = { navController.navigate(Routes.Events) }) {
            Text("See All Events")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fixed: Removed .route
        Button(onClick = { navController.navigate(Routes.CreateEvent) }) {
            Text("Create an event")
        }
    }
}
