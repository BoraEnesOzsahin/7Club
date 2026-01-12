package com.example.a7club.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.VehicleRequestViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRequestFormScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit,
    viewModel: VehicleRequestViewModel = viewModel()
) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.selectedDocumentUri = uri
        viewModel.selectedDocumentName = uri?.lastPathSegment ?: "Belge Seçildi"
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale("tr"))
                        viewModel.dateString = format.format(date)
                    }
                    showDatePicker = false
                }) { Text("Tamam") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("İptal") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Araç Talep Formu", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
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
            OutlinedTextField(value = viewModel.eventName, onValueChange = { viewModel.eventName = it }, label = { Text("Etkinlik Adı") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = viewModel.clubName, onValueChange = { viewModel.clubName = it }, label = { Text("Kulüp Adı") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = viewModel.vehicleType, onValueChange = { viewModel.vehicleType = it }, label = { Text("Araç Tipi (örn: Otobüs, Minibüs)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = viewModel.pickupLocation, onValueChange = { viewModel.pickupLocation = it }, label = { Text("Kalkış Noktası") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = viewModel.destination, onValueChange = { viewModel.destination = it }, label = { Text("Varış Noktası") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = viewModel.passengerCount, onValueChange = { viewModel.passengerCount = it }, label = { Text("Yolcu Sayısı") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(
                value = viewModel.dateString,
                onValueChange = {},
                readOnly = true,
                label = { Text("Talep Tarihi") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, null)
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(value = viewModel.reason, onValueChange = { viewModel.reason = it }, label = { Text("Kullanım Amacı / Gerekçe") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5, shape = RoundedCornerShape(12.dp))
            
            OutlinedButton(
                onClick = { documentPickerLauncher.launch("application/*") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue)
            ) {
                Icon(Icons.Default.AttachFile, null)
                Spacer(modifier = Modifier.width(8.dp))
                val docName = viewModel.selectedDocumentName
                Text(if (docName.isNotEmpty()) docName.take(20) + "..." else "Resmi Belge Ekle (PDF/Word)", textAlign = TextAlign.Center)
            }

            Button(
                onClick = {
                    viewModel.createVehicleRequest(
                        onSuccess = {
                            showSnackbar("Araç talebi başarıyla gönderildi.")
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
                enabled = !viewModel.isUploading
            ) {
                if (viewModel.isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Talebi Gönder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}