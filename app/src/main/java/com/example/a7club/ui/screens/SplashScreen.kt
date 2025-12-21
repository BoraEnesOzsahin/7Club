package com.example.a7club.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a7club.R
import com.example.a7club.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // 2 saniye sonra ana ekrana otomatik geçiş
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Routes.RoleSelection.route) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. ARKA PLAN GÖRSELİ (background_launch.png)
        Image(
            painter = painterResource(id = R.drawable.background_launch),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. MERKEZDEKİ LOGO (Daha fazla küçültüldü)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoooo),
                contentDescription = "YU Club Logo",
                modifier = Modifier
                    // Boyut 380x675'ten 320x570 seviyesine indirildi
                    .width(320.dp) 
                    .height(570.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
