package com.example.a7club.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.a7club.utils.FirebaseSeeder

private val ButtonColor = Color(0xFF160092) // DarkBlue

@Composable
fun RoleSelectionScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan görseli
        Image(
            painter = painterResource(id = R.drawable.profiles),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
