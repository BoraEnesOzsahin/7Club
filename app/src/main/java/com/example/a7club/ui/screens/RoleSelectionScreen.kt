package com.example.a7club.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.R
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme._7ClubTheme

// --- RENK PALETİ ---
private val ButtonColor = Color(0xFF1E138C)

@Composable
fun RoleSelectionScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. ARKA PLAN GÖRSELİ
        Image(
            painter = painterResource(id = R.drawable.background_profiles),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. TAM MERKEZLENMİŞ DAİRESEL BUTONLAR
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Hem dikey hem yatay tam merkez
        ) {
            MenuButton(text = "Öğrenci") {
                navController.navigate(Routes.StudentLogin.route)
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            MenuButton(text = "Kulüp\nYönetim Kurulu\nGirişi") {
                navController.navigate(Routes.ClubCommitteeLogin.route)
            }

            Spacer(modifier = Modifier.height(24.dp))

            MenuButton(text = "Personel\nGirişi") {
                navController.navigate(Routes.PersonnelLogin.route)
            }
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(154.dp)
            .clip(CircleShape)
            .background(ButtonColor)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    _7ClubTheme {
        RoleSelectionScreen(navController = rememberNavController(), showSnackbar = {})
    }
}
