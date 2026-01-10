package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(navController: NavController) {
    val categories = listOf("Söyleşi", "Workshop", "Sempozyum", "Müzik", "Sinema", "Spor")
    var selectedCategory by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Filtrele", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = VeryLightPurple)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Kategoriler", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    Surface(
                        modifier = Modifier.height(44.dp).clickable { selectedCategory = if (selectedCategory == category) "" else category },
                        color = if (selectedCategory == category) DarkBlue else LightPurple.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = category, color = if (selectedCategory == category) Color.White else DarkBlue)
                        }
                    }
                }
            }
        }
    }
}