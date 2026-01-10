package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.data.UserRole
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentLoginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    showSnackbar: (String) -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()

    // Handle login state changes
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                showSnackbar("Giriş başarılı! Hoş geldin ${state.data?.email}")
                // Öğrenciler sadece etkinlikleri ve kulüpleri görebilir, araç talep edemez.
                // Bu yüzden doğrudan Etkinlikler (Events) sayfasına yönlendiriyoruz.

                // CORRECTED: Added .route to provide the String path
                navController.navigate(Routes.Events.route) {
                    // CORRECTED: Added .route here as well
                    popUpTo(Routes.RoleSelection.route) { inclusive = true }
                }
            }
            is Resource.Error -> {
                showSnackbar(state.message ?: "Bilinmeyen bir hata oluştu.")
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color(0xFFD0BCFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Öğrenci Giriş", color = Color(0xFF21005D))
            }
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("E-Mail") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = loginState !is Resource.Loading,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFD0BCFF),
                    focusedContainerColor = Color(0xFFD0BCFF),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Şifre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = loginState !is Resource.Loading,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFD0BCFF),
                    focusedContainerColor = Color(0xFFD0BCFF),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (loginState is Resource.Loading) {
                CircularProgressIndicator()
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF))
                    ) {
                        Text("Geri Dön", color = Color(0xFF21005D))
                    }
                    Button(
                        onClick = { viewModel.signIn(UserRole.STUDENT) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F378B))
                    ) {
                        Text("Giriş Yap", color = Color.White)
                    }
                }
            }
        }
    }
}
