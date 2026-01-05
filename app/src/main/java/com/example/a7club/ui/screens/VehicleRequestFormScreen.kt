@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.a7club.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.viewmodels.CommitteeEventViewModel

@Composable
fun VehicleRequestFormScreen(
    navController: NavController,
    eventName: String, // Route'dan gelen parametre
    viewModel: CommitteeEventViewModel
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Araç Talep Formu", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- TASARIM KODLARI AYNEN KALIYOR ---

            Text("İlgili Etkinlik: $eventName", fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.vehicleType,
                onValueChange = { viewModel.vehicleType = it },
                label = { Text("Araç Tipi (Örn: Otobüs)") },
                modifier = Modifier.fillMaxWidth()
            )
            // ... Diğer TextField'lar aynen kalıyor (pickupLocation, dropoffLocation vb.) ...

            Spacer(modifier = Modifier.height(32.dp))

            // BUTON LOGIC GÜNCELLEMESİ
            Button(
                onClick = {
                    if (viewModel.pickupLocation.isBlank()) {
                        Toast.makeText(context, "Lütfen konum bilgisini girin", Toast.LENGTH_SHORT).show()
                    } else {
                        // VERİTABANI İŞLEMİ TETİKLENİYOR
                        viewModel.submitVehicleRequest(eventName = eventName, clubId = "club_yukek")

                        Toast.makeText(context, "Araç talebi gönderildi!", Toast.LENGTH_LONG).show()
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