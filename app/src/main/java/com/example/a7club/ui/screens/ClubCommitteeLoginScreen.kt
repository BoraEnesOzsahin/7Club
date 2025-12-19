package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.data.UserRole
import com.example.a7club.ui.navigation.Routes
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
                showSnackbar("Kulüp Yönetim Kurulu olarak giriş yapıldı! Hoş geldin ${state.data?.email}")
                // CORRECTED: Added .route to pass the String path
                navController.navigate(Routes.MainScreen.route) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Kulüp Yönetim Kurulu Girişi")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is Resource.Loading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is Resource.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (loginState is Resource.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.signIn(UserRole.CLUB_COMMITTEE) },
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState !is Resource.Loading
            ) {
                Text("Giriş Yap")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is Resource.Loading
        ) {
            Text("Geri Dön")
        }
    }
}
