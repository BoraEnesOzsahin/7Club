package com.example.a7club.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme._7ClubTheme

// --- RENK PALETİ ---
private val BackgroundColor = Color(0xFFE1EEF7)
private val FrontWaveColor = Color(0xFFBAD9FB)
private val MiddleWaveColor = Color(0xFF5449AD)
private val BackWaveColor = Color(0xFFDCD8FC)
private val ButtonColor = Color(0xFF1E138C)

@Composable
fun RoleSelectionScreen(navController: NavController, showSnackbar: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // --- Arka Plan Dalgaları (İNCELTİLMİŞ VERSİYON) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // --- ÜST DALGALAR ---
            val topPath1 = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, height * 0.16f)
                cubicTo(width * 0.4f, height * 0.08f, width * 0.6f, height * 0.22f, width, height * 0.12f)
                lineTo(width, 0f)
                close()
            }
            drawPath(path = topPath1, color = BackWaveColor)

            val topPath2 = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, height * 0.13f)
                cubicTo(width * 0.3f, height * 0.05f, width * 0.7f, height * 0.18f, width, height * 0.10f)
                lineTo(width, 0f)
                close()
            }
            drawPath(path = topPath2, color = MiddleWaveColor)

            val topPath3 = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, height * 0.10f)
                cubicTo(width * 0.25f, height * 0.03f, width * 0.6f, height * 0.16f, width, height * 0.06f)
                lineTo(width, 0f)
                close()
            }
            drawPath(path = topPath3, color = FrontWaveColor)

            // --- ALT DALGALAR ---
            val bottomPath1 = Path().apply {
                moveTo(0f, height)
                lineTo(0f, height * 0.86f)
                cubicTo(width * 0.3f, height * 0.80f, width * 0.7f, height * 0.92f, width, height * 0.84f)
                lineTo(width, height)
                close()
            }
            drawPath(path = bottomPath1, color = BackWaveColor)

            val bottomPath2 = Path().apply {
                moveTo(0f, height)
                lineTo(0f, height * 0.89f)
                cubicTo(width * 0.4f, height * 0.84f, width * 0.6f, height * 0.94f, width, height * 0.88f)
                lineTo(width, height)
                close()
            }
            drawPath(path = bottomPath2, color = MiddleWaveColor)

            val bottomPath3 = Path().apply {
                moveTo(0f, height)
                lineTo(0f, height * 0.92f)
                cubicTo(width * 0.35f, height * 0.88f, width * 0.55f, height * 0.96f, width, height * 0.91f)
                lineTo(width, height)
                close()
            }
            drawPath(path = bottomPath3, color = FrontWaveColor)
        }

        // Dikey hizalanmış butonlar
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            MenuButton(text = "Öğrenci") {
                navController.navigate(Routes.StudentLogin.route)
            }
            MenuButton(text = "Kulüp\nYönetim Kurulu\nGirişi") {
                navController.navigate(Routes.ClubCommitteeLogin.route)
            }
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
            .size(160.dp)
            .clip(CircleShape)
            .background(ButtonColor)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
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
