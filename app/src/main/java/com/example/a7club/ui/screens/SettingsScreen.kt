package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.theme._7ClubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var selectedLanguage by remember { mutableStateOf("Türkçe") }

    Scaffold(
        containerColor = Color(0xFFF3F1FF), // Ekranın arka plan rengi
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ayarlar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Geri dönme işlevi
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Bildirimler */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Bildirimler")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFE8E5FF))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E5FF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Dil Seçimi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // İngilizce Butonu
                        Button(
                            onClick = { selectedLanguage = "İngilizce" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50), // Tam yuvarlak kenarlar
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLanguage == "İngilizce") Color(0xFF000080) else Color.LightGray,
                                contentColor = if (selectedLanguage == "İngilizce") Color.White else Color.Black
                            )
                        ) {
                            Text("İngilizce")
                        }
                        // Türkçe Butonu
                        Button(
                            onClick = { selectedLanguage = "Türkçe" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50), // Tam yuvarlak kenarlar
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLanguage == "Türkçe") Color(0xFF000080) else Color.LightGray,
                                contentColor = if (selectedLanguage == "Türkçe") Color.White else Color.Black
                            )
                        ) {
                            Text("Türkçe")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    _7ClubTheme {
        SettingsScreen(navController = rememberNavController())
    }
}
