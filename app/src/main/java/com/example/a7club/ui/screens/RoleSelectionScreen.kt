package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme._7ClubTheme

@Composable
fun RoleSelectionScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RoleButton(text = "Öğrenci") {
                navController.navigate(Routes.StudentLogin.route)
            }
            Spacer(modifier = Modifier.height(32.dp))
            RoleButton(text = "Kulüp Yönetim Kurulu Girişi") {
                navController.navigate(Routes.ClubCommitteeLogin.route)
            }
            Spacer(modifier = Modifier.height(32.dp))
            RoleButton(text = "Personel Girişi") {
                navController.navigate(Routes.PersonnelLogin.route)
            }
        }
    }
}

@Composable
fun RoleButton(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(160.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    _7ClubTheme {
        RoleSelectionScreen(navController = rememberNavController(), showSnackbar = {})
    }
}
