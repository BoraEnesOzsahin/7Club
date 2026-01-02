@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.CommitteeEventViewModel

@Composable
fun VehicleRequestFormScreen(
    navController: NavController,
    eventName: String, // Bu bilgi önceki sayfadan geliyor
    viewModel: CommitteeEventViewModel
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Araç Talep Formu", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. ÇÖZÜM: ETKİNLİK ADINI GÖSTEREN KİLİTLİ KUTU ---
            OutlinedTextField(
                value = eventName, // Otomatik gelen isim
                onValueChange = {}, // Değiştirilemez
                label = { Text("Seçilen Etkinlik") },
                enabled = false, // Kullanıcı buraya yazamaz, sadece görür
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledContainerColor = LightPurple.copy(alpha = 0.3f),
                    disabledLabelColor = DarkBlue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            // -----------------------------------------------------

            OutlinedTextField(
                value = viewModel.vehicleType,
                onValueChange = { viewModel.vehicleType = it },
                label = { Text("Araç Tipi (Örn: Otobüs, Minibüs)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.pickupLocation,
                onValueChange = { viewModel.pickupLocation = it },
                label = { Text("Kalkış Yeri (Nereden)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.dropoffLocation,
                onValueChange = { viewModel.dropoffLocation = it },
                label = { Text("Varış Yeri (Nereye)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.passengerCount,
                onValueChange = { viewModel.passengerCount = it },
                label = { Text("Yolcu Sayısı") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = { viewModel.notes = it },
                label = { Text("Notlar (Opsiyonel)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (viewModel.pickupLocation.isBlank() || viewModel.passengerCount.isBlank()) {
                        Toast.makeText(context, "Lütfen gerekli alanları doldurun", Toast.LENGTH_SHORT).show()
                    } else {
                        // eventName değişkenini kullanarak kaydediyoruz
                        viewModel.submitVehicleRequest(eventName = eventName, clubId = "club_yukek")

                        Toast.makeText(context, "Talep Gönderildi!", Toast.LENGTH_LONG).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text("Talebi Oluştur", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}