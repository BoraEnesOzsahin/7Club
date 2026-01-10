@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue

@Composable
fun RejectedEventDetailScreen(navController: NavController, eventName: String) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Red Detayı", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Başlık Kartı
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Etkinlik Adı", fontSize = 14.sp, color = Color.Gray)
                    Text(eventName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Red Nedeni
            Text("Reddedilme Nedeni:", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth().height(150.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Maalesef belirtilen tarihte salon doluluğu ve etkinlik bütçesindeki tutarsızlıklar nedeniyle başvurunuz onaylanmamıştır. Lütfen tarih değişikliği yaparak tekrar başvurunuz.",
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Daha fazla bilgi için SKS ile görüşebilirsiniz.", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}