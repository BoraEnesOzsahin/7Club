package com.example.a7club.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable // Tıklama için
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime // Saat İkonu
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.viewmodels.CreateEventViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit,
    viewModel: CreateEventViewModel = viewModel()
) {
    // --- TAKVİM VE SAAT İÇİN STATE ---
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(is24Hour = true)
    var showTimePicker by remember { mutableStateOf(false) }

    // Görsel Seçici (Galeri)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.selectedFileUri.value = uri
    }

    // Belge Seçici (PDF/Word)
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.selectedDocumentUri.value = uri
        viewModel.selectedDocumentName.value = uri?.lastPathSegment ?: "Belge Seçildi"
    }

    // --- TAKVİM DİYALOĞU ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        // Formatı veritabanıyla uyumlu hale getiriyoruz: dd/MM/yyyy
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale("tr"))
                        viewModel.dateString.value = format.format(date)
                    }
                    showDatePicker = false
                }) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("İptal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- SAAT DİYALOĞU ---
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour.toString().padStart(2, '0')
                    val minute = timePickerState.minute.toString().padStart(2, '0')
                    viewModel.eventTime.value = "$hour:$minute"
                    showTimePicker = false
                }) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("İptal") }
            },
            text = { TimePicker(state = timePickerState) }
        )
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
                Text(if (viewModel.selectedFileUri.value != null) "Görsel Seçildi" else "Etkinlik Afişi Yükle", textAlign = TextAlign.Center)
            }

            // Islak İmzalı Belge Butonu
            OutlinedButton(
                onClick = { documentPickerLauncher.launch("application/*") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue)
            ) {
                Icon(Icons.Default.AttachFile, null)
                Spacer(modifier = Modifier.width(8.dp))
                val docName = viewModel.selectedDocumentName.value
                Text(if (docName.isNotEmpty()) docName.take(20) + "..." else "Başvuru Formu Ekle (PDF/Word)", textAlign = TextAlign.Center)
            }

            // Başlık
            OutlinedTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Etkinlik Başlığı") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Kategori Seçimi (FilterChips)
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

            // --- TARİH ALANI (GÜNCELLENDİ) ---
            OutlinedTextField(
                value = viewModel.dateString.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tarih Seçiniz") },
                placeholder = { Text("GG/AA/YYYY") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // --- SAAT ALANI (GÜNCELLENDİ) ---
            OutlinedTextField(
                value = viewModel.eventTime.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Saat Seçiniz") },
                placeholder = { Text("SS:DD") },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(Icons.Default.AccessTime, null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                shape = RoundedCornerShape(12.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
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