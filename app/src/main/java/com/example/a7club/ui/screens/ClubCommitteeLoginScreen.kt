package com.example.a7club.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.data.UserRole
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubCommitteeLoginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    showSnackbar: (String) -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                showSnackbar("Kulüp Yönetim Kurulu olarak giriş yapıldı!")
                navController.navigate(Routes.ClubHomeScreen.route) {
                    popUpTo(Routes.RoleSelection.route) { inclusive = true }
                }
            }
            is Resource.Error -> {
                showSnackbar(state.message ?: "Giriş başarısız.")
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3EFFF)) // Açık lila arka plan
    ) {
        // Alt Dalgalar (Wave Effect)
        WaveBackground(modifier = Modifier.align(Alignment.BottomCenter))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.TopCenter) {
                // Ana Giriş Kutusu
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD1C4E9), RoundedCornerShape(32.dp))
                        .padding(top = 60.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // E-Mail
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        placeholder = { Text("E-Mail", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFEDE7F6),
                            focusedContainerColor = Color(0xFFEDE7F6),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Şifre
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        placeholder = { Text("Şifre", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFEDE7F6),
                            focusedContainerColor = Color(0xFFEDE7F6),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (loginState is Resource.Loading) {
                        CircularProgressIndicator(color = Color(0xFF1A0273))
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDE7F6))
                            ) {
                                Text("Geri Dön", color = Color(0xFF1A0273), fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.signIn(UserRole.CLUB_COMMITTEE) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A0273))
                            ) {
                                Text("Giriş Yap", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Üstteki Yuvarlak Başlık
                Box(
                    modifier = Modifier
                        .offset(y = (-50).dp)
                        .size(100.dp)
                        .background(Color(0xFF1A0273), CircleShape)
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kulüp YK\nGiriş",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WaveBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(200.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val path1 = Path().apply {
                moveTo(0f, height * 0.7f)
                quadraticBezierTo(width * 0.25f, height * 0.6f, width * 0.5f, height * 0.8f)
                quadraticBezierTo(width * 0.75f, height * 1.0f, width, height * 0.8f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path1, color = Color(0xFF1A0273))

            val path2 = Path().apply {
                moveTo(0f, height * 0.5f)
                quadraticBezierTo(width * 0.3f, height * 0.8f, width * 0.6f, height * 0.5f)
                quadraticBezierTo(width * 0.85f, height * 0.3f, width, height * 0.6f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path2, color = Color(0xFF3F51B5).copy(alpha = 0.5f))

            val path3 = Path().apply {
                moveTo(0f, height * 0.3f)
                quadraticBezierTo(width * 0.4f, height * 0.1f, width * 0.7f, height * 0.4f)
                quadraticBezierTo(width * 0.9f, height * 0.6f, width, height * 0.2f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path3, color = Color(0xFF90CAF9).copy(alpha = 0.4f))
        }
    }
}
