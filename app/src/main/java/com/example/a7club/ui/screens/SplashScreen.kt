package com.example.a7club.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a7club.R
import com.example.a7club.ui.navigation.Routes
import kotlinx.coroutines.delay

// --- RENK PALETİ (Dalga Tasarımı) ---
private val BackgroundColor = Color(0xFFE1EEF7)
private val FrontWaveColor = Color(0xFFBAD9FB)
private val MiddleWaveColor = Color(0xFF5449AD)
private val BackWaveColor = Color(0xFFDCD8FC)

@Composable
fun SplashScreen(navController: NavController) {
    // 2 saniye sonra Role Selection ekranına geçiş
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Routes.RoleSelection.route) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // --- Arka Plan Dalgaları (Canvas ile Çizim) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // --- ÜST KISIM DALGALARI ---
            val topPath1 = Path().apply {
                moveTo(0f, 0f); lineTo(0f, height * 0.35f); cubicTo(width * 0.4f, height * 0.25f, width * 0.6f, height * 0.45f, width, height * 0.3f); lineTo(width, 0f); close()
            }
            drawPath(path = topPath1, color = BackWaveColor)
            val topPath2 = Path().apply {
                moveTo(0f, 0f); lineTo(0f, height * 0.28f); cubicTo(width * 0.3f, height * 0.18f, width * 0.7f, height * 0.38f, width, height * 0.23f); lineTo(width, 0f); close()
            }
            drawPath(path = topPath2, color = MiddleWaveColor)
            val topPath3 = Path().apply {
                moveTo(0f, 0f); lineTo(0f, height * 0.20f); cubicTo(width * 0.25f, height * 0.10f, width * 0.6f, height * 0.30f, width, height * 0.15f); lineTo(width, 0f); close()
            }
            drawPath(path = topPath3, color = FrontWaveColor)

            // --- ALT KISIM DALGALARI ---
            val bottomPath1 = Path().apply {
                moveTo(0f, height); lineTo(0f, height * 0.65f); cubicTo(width * 0.3f, height * 0.55f, width * 0.7f, height * 0.75f, width, height * 0.60f); lineTo(width, height); close()
            }
            drawPath(path = bottomPath1, color = BackWaveColor)
            val bottomPath2 = Path().apply {
                moveTo(0f, height); lineTo(0f, height * 0.72f); cubicTo(width * 0.4f, height * 0.62f, width * 0.6f, height * 0.82f, width, height * 0.72f); lineTo(width, height); close()
            }
            drawPath(path = bottomPath2, color = MiddleWaveColor)
            val bottomPath3 = Path().apply {
                moveTo(0f, height); lineTo(0f, height * 0.80f); cubicTo(width * 0.35f, height * 0.70f, width * 0.55f, height * 0.90f, width, height * 0.83f); lineTo(width, height); close()
            }
            drawPath(path = bottomPath3, color = FrontWaveColor)
        }

        // --- MERKEZDEKİ ÖZEL LOGO (logoooo.png) ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(220.dp), // Logonun ekrandaki boyutu
            contentAlignment = Alignment.Center
        ) {
            Image(
                // NOT: logoooo.png dosyasının drawable klasöründe olduğundan emin olun!
                // Eğer ismi farklıysa buradan güncelleyin.
                painter = painterResource(id = R.drawable.logoooo), 
                contentDescription = "YU Club Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}
