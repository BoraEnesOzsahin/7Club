@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.viewmodels.CreateEventViewModel

@Composable
fun CreateEventScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit,
    viewModel: CreateEventViewModel = viewModel()
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.selectedFileUri = uri
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlik Oluştur", fontWeight = FontWeight.Bold, color = DarkBlue) },
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
            // --- GEREKLİ ALANLAR ---

            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Etkinlik Adı (Zorunlu)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.eventTime,
                onValueChange = { viewModel.eventTime = it },
                label = { Text("Etkinlik Saati (Örn: 14:00)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.contactPhone,
                onValueChange = { viewModel.contactPhone = it },
                label = { Text("İletişim Telefonu") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.clubName,
                onValueChange = { viewModel.clubName = it },
                label = { Text("Kulüp Adı") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Açıklama (Opsiyonel)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- PDF YÜKLEME ---
            OutlinedButton(
                onClick = { filePickerLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.UploadFile, null)
                Spacer(modifier = Modifier.width(8.dp))
                if (viewModel.selectedFileUri != null) {
                    Text("PDF Seçildi ✅", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                } else {
                    Text("Etkinlik Formu Yükle (PDF)")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- GÖNDER BUTONU ---
            Button(
                onClick = {
                    viewModel.createEvent(
                        onSuccess = {
                            showSnackbar("Etkinlik ve PDF başarıyla gönderildi!")
                            navController.popBackStack()
                        },
                        onError = { showSnackbar(it) }
                    )
                },
                enabled = !viewModel.isUploading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                if (viewModel.isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Text(" Yükleniyor...", modifier = Modifier.padding(start = 8.dp))
                } else {
                    Text("Etkinliği Oluştur", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}