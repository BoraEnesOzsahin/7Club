package com.example.a7club.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.a7club.R
import com.example.a7club.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // 2 saniye sonra Role Selection ekranına geçiş
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Routes.RoleSelection.route) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.launchscreen2),
            contentDescription = "Splash Screen Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Ekranı dolduracak şekilde ayarlandı
        )
    }
}
