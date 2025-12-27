@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.viewmodels.AuthViewModel
import java.util.Locale

@Composable
fun PersonnelProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val userEmail = authViewModel.email.ifEmpty { "yagmurdirekci@gmail.com" }
    val displayName = formatEmailToName(userEmail)

    // ÇÖZÜM: Column'u bir Box içine alıyoruz ve Box'ın Scaffold'un
    // alt boşluğunu (padding) görmezden gelmesini sağlıyoruz.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // offset ile column'u biraz aşağı kaydırarak
                // alttaki morluğu tamamen kapatıyoruz.
                .offset(y = 50.dp)
                .background(Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // offset yukarı kaydırdığı için içeriği
            // eski yerine çekmek için negatif spacer
            Spacer(modifier = Modifier.height(0.dp))

            // Profil Fotoğrafı Alanı
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(VeryLightPurple)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // İsim-Soyisim Kartı
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = VeryLightPurple
            ) {
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Oturumu Kapat Butonu
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        authViewModel.resetLoginState()
                        navController.navigate(Routes.RoleSelection.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                color = VeryLightPurple
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Oturumu Kapat",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                }
            }
        }
    }
}

fun formatEmailToName(email: String): String {
    val prefix = email.split("@").firstOrNull() ?: return "Kullanıcı"
    return when (prefix.lowercase()) {
        "yagmurdirekci" -> "Yağmur Direkçi"
        "samisidar" -> "Sami Sidar"
        else -> prefix.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}