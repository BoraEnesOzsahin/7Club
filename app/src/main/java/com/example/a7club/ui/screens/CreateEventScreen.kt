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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile // Yeni İkon
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit,
    viewModel: CreateEventViewModel = viewModel()
) {
    // Görsel Seçici (Galeri)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.selectedFileUri.value = uri
    }

    // --- YENİ EKLENEN: Belge Seçici (PDF/Word) ---
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.selectedDocumentUri.value = uri
        // Dosya yolundan basitçe ismi almaya çalışalım
        viewModel.selectedDocumentName.value = uri?.lastPathSegment ?: "Belge Seçildi"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Yeni Etkinlik", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Görsel Yükleme Alanı
            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (viewModel.selectedFileUri.value != null) "Görsel Seçildi" else "Etkinlik Afişi Yükle")
            }

            // --- YENİ EKLENEN BUTON: Islak İmzalı Belge ---
            // Mevcut tasarımına uygun olarak eklendi
            OutlinedButton(
                onClick = {
                    // PDF, Word vb. dökümanları filtreler
                    documentPickerLauncher.launch("application/*")
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue)
            ) {
                Icon(Icons.Default.AttachFile, null)
                Spacer(modifier = Modifier.width(8.dp))

                val docName = viewModel.selectedDocumentName.value
                Text(if (docName.isNotEmpty()) docName else "Başvuru Formu Ekle (PDF/Word)")
            }

            // Başlık
            OutlinedTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Etkinlik Başlığı") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Kategori
            Text("Kategori: ${viewModel.category.value}", color = DarkBlue, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Business", "Tech", "Social", "Art").forEach { cat ->
                    FilterChip(
                        selected = viewModel.category.value == cat,
                        onClick = { viewModel.category.value = cat },
                        label = { Text(cat) }
                    )
                }
            }

            // Tarih
            OutlinedTextField(
                value = viewModel.dateString.value,
                onValueChange = { viewModel.dateString.value = it },
                label = { Text("Tarih (Örn: 25 Kasım)") },
                trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Saat
            OutlinedTextField(
                value = viewModel.eventTime.value,
                onValueChange = { viewModel.eventTime.value = it },
                label = { Text("Saat (Örn: 14:00)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Konum
            OutlinedTextField(
                value = viewModel.location.value,
                onValueChange = { viewModel.location.value = it },
                label = { Text("Konum (Örn: İnan Kıraç Salonu)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Telefon
            OutlinedTextField(
                value = viewModel.contactPhone.value,
                onValueChange = { viewModel.contactPhone.value = it },
                label = { Text("İletişim Numarası") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Açıklama
            OutlinedTextField(
                value = viewModel.description.value,
                onValueChange = { viewModel.description.value = it },
                label = { Text("Etkinlik Açıklaması") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Kaydet Butonu
            Button(
                onClick = {
                    viewModel.createEvent(
                        onSuccess = {
                            showSnackbar("Etkinlik ve belgeler onaya gönderildi!")
                            navController.popBackStack()
                        },
                        onError = { error ->
                            showSnackbar(error)
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                enabled = !viewModel.isUploading.value
            ) {
                if (viewModel.isUploading.value) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Oluştur ve Onaya Gönder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}