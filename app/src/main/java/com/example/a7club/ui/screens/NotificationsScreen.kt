package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.theme._7ClubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composableun NotificationsScreen(navController: NavController) {
    Scaffold(
        containerColor = VeryLightPurple,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bildirimler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LightPurple
                )
            )
        }
    ) { paddingValues ->
        val notifications = listOf(
            "Kültür ve Etkinlik Kulübü etkinliği başladı.",
            "Yeni bir duyuru yayınlandı.",
            "Müzik Kulübü etkinliğine kayıt oldunuz.",
            "X Kulübü etkinliği yarın!"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notificationText ->
                NotificationCard(text = notificationText)
            }
        }
    }
}

@Composable
fun NotificationCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    _7ClubTheme {
        NotificationsScreen(navController = rememberNavController())
    }
}
