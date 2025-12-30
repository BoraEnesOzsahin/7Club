package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource // ÖNEMLİ IMPORT
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a7club.R

// --- RENK PALETİ ---
val NotifDarkBlue = Color(0xFF160092)
val NotifLightPurple = Color(0xFFCCC2FF)
val NotifBackground = Color(0xFFF9F8FD)
val NotifCardLight = Color(0xFFE6E3F6)

// --- MODEL ---
data class StudentNotificationItem(
    val id: Int,
    val title: String? = null,
    val message: String,
    val type: StudentNotificationType
)

enum class StudentNotificationType {
    IMPORTANT,
    STANDARD
}

@Composable
fun StudentNotificationsScreen(
    navController: NavController
) {
    // VERİLERİ BURADA OLUŞTURUYORUZ (Çünkü stringResource Composable içinde çalışır)
    val notifications = listOf(
        StudentNotificationItem(
            id = 1,
            title = stringResource(R.string.notif_reminder), // XML'den çekiyor
            message = stringResource(R.string.notif_upcoming), // XML'den çekiyor
            type = StudentNotificationType.IMPORTANT
        ),
        StudentNotificationItem(
            id = 2,
            message = stringResource(R.string.notif_karaoke), // XML'den çekiyor
            type = StudentNotificationType.IMPORTANT
        ),
        StudentNotificationItem(
            id = 3,
            message = stringResource(R.string.notif_announcement), // XML'den çekiyor
            type = StudentNotificationType.STANDARD
        )
    )

    Scaffold(
        containerColor = NotifBackground,
        topBar = {
            StudentNotificationTopBar(onBackClick = { navController.popBackStack() })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(notifications) { notification ->
                StudentNotificationCard(item = notification)
            }
        }
    }
}

@Composable
fun StudentNotificationTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.menu), // "Geri" için Menu tanımını kullandım veya özel bir string eklenebilir
                tint = NotifDarkBlue,
                modifier = Modifier.size(28.dp)
            )
        }

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = stringResource(R.string.notifications),
            tint = NotifDarkBlue,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun StudentNotificationCard(item: StudentNotificationItem) {
    val containerColor = if (item.type == StudentNotificationType.IMPORTANT) NotifDarkBlue else NotifCardLight
    val contentColor = if (item.type == StudentNotificationType.IMPORTANT) Color.White else NotifDarkBlue

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (item.title != null) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = item.message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}