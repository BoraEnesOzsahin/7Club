package com.example.a7club.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.data.UserRole
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.*
import com.example.a7club.ui.viewmodels.AuthViewModel

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
                navController.navigate(Routes.ClubHomeScreen.route) {
                    popUpTo(Routes.RoleSelection.route) { inclusive = true }
                }
            }
            is Resource.Error -> {
                showSnackbar(state.message ?: "Bilinmeyen bir hata oluştu.")
                viewModel.resetLoginState()
            }
            else -> Unit
        }
    }

    ClubCommitteeLoginContent(
        email = viewModel.email,
        password = viewModel.password,
        onEmailChange = { viewModel.email = it },
        onPasswordChange = { viewModel.password = it },
        loginState = loginState,
        onLoginClick = { viewModel.signIn(UserRole.CLUB_COMMITTEE) },
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubCommitteeLoginContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    loginState: Resource<out Any>?,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VeryLightPurple) // Main background color
    ) {
        Waves() // Draw the waves first

        // Main content column, centered
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // This Box handles the overlapping of the circle and the card
            Box {
                // The main card, with top padding to make space for the overlapping circle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp), // Half of the circle's size
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = LightPurple),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    // Card content starts below the overlapping circle area
                    Column(
                        modifier = Modifier.padding(top = 60.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = { Text("E-Mail") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = VeryLightPurple,
                                unfocusedContainerColor = VeryLightPurple,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = DarkBlue,
                                unfocusedLabelColor = DarkBlue.copy(alpha = 0.5f)
                            ),
                            enabled = loginState !is Resource.Loading
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            label = { Text("Şifre") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = VeryLightPurple,
                                unfocusedContainerColor = VeryLightPurple,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = DarkBlue,
                                unfocusedLabelColor = DarkBlue.copy(alpha = 0.5f)
                            ),
                            enabled = loginState !is Resource.Loading
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                         Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Button(
                                onClick = onBackClick,
                                shape = RoundedCornerShape(16.dp),
                                enabled = loginState !is Resource.Loading,
                                colors = ButtonDefaults.buttonColors(containerColor = VeryLightPurple, contentColor = DarkBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Geri Dön")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = onLoginClick,
                                shape = RoundedCornerShape(16.dp),
                                enabled = loginState !is Resource.Loading,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Giriş Yap")
                            }
                        }
                    }
                }

                // The overlapping circle, drawn on top of the card
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(100.dp)
                        .background(DarkBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kulüp YK Giriş",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Loading indicator overlay
        if (loginState is Resource.Loading) {
            Box(
                contentAlignment = Alignment.Center, 
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ){
                CircularProgressIndicator(color = DarkBlue)
            }
        }
    }
}

@Composable
fun Waves() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter){
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Lightest Blue Wave (Bottom)
            val pathLightBlue = Path().apply {
                moveTo(0f, height * 0.8f)
                cubicTo(width * 0.3f, height * 0.75f, width * 0.6f, height * 0.9f, width, height * 0.85f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(pathLightBlue, color = LightBlueWave)
            
            // 2. Purple Wave (Middle) - Corrected
            val pathPurple = Path().apply {
                moveTo(0f, height * 0.88f)
                cubicTo(width * 0.35f, height * 0.82f, width * 0.65f, height * 0.97f, width, height * 0.92f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(pathPurple, color = WavePurple)

            // 3. Dark Blue Wave (Top)
            val pathDarkBlue = Path().apply {
                moveTo(0f, height * 0.93f)
                cubicTo(width * 0.4f, height * 0.9f, width * 0.7f, height, width, height * 0.96f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(pathDarkBlue, color = DarkBlue)
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Preview")
@Composable
fun ClubCommitteeLoginScreenPreview() {
    _7ClubTheme {
        ClubCommitteeLoginContent(
            email = "",
            password = "",
            onEmailChange = {},
            onPasswordChange = {},
            loginState = null,
            onLoginClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Login Screen Loading")
@Composable
fun ClubCommitteeLoginScreenLoadingPreview() {
    _7ClubTheme {
        ClubCommitteeLoginContent(
            email = "test@mail.com",
            password = "123456",
            onEmailChange = {},
            onPasswordChange = {},
            loginState = Resource.Loading(),
            onLoginClick = {},
            onBackClick = {}
        )
    }
}
