package com.example.a7club.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme._7ClubTheme
import com.example.a7club.utils.FirebaseSeeder

// --- RENK PALETİ ---
private val BackgroundColor = Color(0xFFE1EEF7)
private val FrontWaveColor = Color(0xFFBAD9FB)
private val MiddleWaveColor = Color(0xFF5449AD)
private val BackWaveColor = Color(0xFFDCD8FC)
private val ButtonColor = Color(0xFF160092) // DarkBlue

@Composable
fun RoleSelectionScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit
) {
    // Toast mesajı gösterebilmek için Context'i alıyoruz
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // --- ARKA PLAN DALGALARI ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val path1 = Path().apply {
                moveTo(0f, height * 0.4f)
                cubicTo(width * 0.5f, height * 0.3f, width * 0.8f, height * 0.5f, width, height * 0.45f)
                lineTo(width, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(path = path1, color = BackWaveColor)

            val path2 = Path().apply {
                moveTo(0f, height * 0.35f)
                cubicTo(width * 0.2f, height * 0.45f, width * 0.6f, height * 0.25f, width, height * 0.4f)
                lineTo(width, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(path = path2, color = FrontWaveColor.copy(alpha = 0.6f))
        }

        // --- İÇERİK ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Butonlar
            MenuButton(text = "Öğrenci Girişi") {
                navController.navigate(Routes.StudentLogin.route)
            }
            Spacer(modifier = Modifier.height(24.dp))

            MenuButton(text = "Kulüp Yönetimi\nGirişi") {
                navController.navigate(Routes.ClubCommitteeLogin.route)
            }
            Spacer(modifier = Modifier.height(24.dp))

            MenuButton(text = "SKS Personel\nGirişi") {
                navController.navigate(Routes.PersonnelLogin.route)
            }

            // --- VERİTABANI GÜNCELLEME BUTONU ---
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    // Kullanıcıya işlemin başladığını bildirelim
                    Toast.makeText(context, "İşlem yapılıyor, lütfen bekleyin...", Toast.LENGTH_SHORT).show()

                    FirebaseSeeder.seedDatabase { result ->
                        // İşlem bitince gelen sonucu ekrana bas (Snackbar yerine Toast)
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
            ) {
                Text("Veritabanını Güncelle (Test)", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
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
        RoleSelectionScreen(rememberNavController()) {}
    }
}