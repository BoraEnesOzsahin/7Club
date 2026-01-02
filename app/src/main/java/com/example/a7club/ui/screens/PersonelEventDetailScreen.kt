@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.model.VehicleRequest
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun PersonelEventDetailScreen(
    navController: NavController,
    eventName: String,
    clubName: String,
    viewModel: PersonnelViewModel = viewModel()
) {
    // ViewModel'den gelen event listesini dinle
    val pendingEvents by viewModel.pendingEvents.collectAsState()

    // Etkinliği isme göre bul
    val event = pendingEvents.find { it.title == eventName }

    // Araç talebini dinle
    val vehicleRequest by viewModel.currentVehicleRequest.collectAsState()

    // Hangi formun açık olduğunu tutan state
    var activeForm by remember { mutableStateOf<String?>(null) }

    // Onay/Red dialog kontrolleri
    var showResultDialog by remember { mutableStateOf(false) }
    var resultType by remember { mutableStateOf("") }

    // Link açmak için handler
    val uriHandler = LocalUriHandler.current

    // Sayfa açılınca veya event değişince araç talebini çek
    LaunchedEffect(event) {
        if (event != null) {
            viewModel.fetchVehicleRequest(event.id)
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Etkinlik Detayı", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (event == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Etkinlik verisi yükleniyor...", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- ETKİNLİK KARTI ---
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightPurple.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(event.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Kulüp: $clubName", fontSize = 16.sp, color = DarkBlue.copy(alpha = 0.7f))

                        // Saat ve Telefon Bilgileri
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = DarkBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Saat: ${event.eventTime.ifEmpty { "Belirtilmedi" }}",
                                fontWeight = FontWeight.Bold,
                                color = DarkBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, null, tint = DarkBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "İletişim: ${event.contactPhone.ifEmpty { "Belirtilmedi" }}",
                                color = DarkBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(event.description, fontSize = 14.sp, color = DarkBlue)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Talep Formları", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBlue, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(12.dp))

                // --- BUTONLAR ---

                // 1. Etkinlik Talep Formu (PDF)
                FormButton("Etkinlik Talep Formu (PDF)", Icons.Default.Description) {
                    activeForm = "EventRequest"
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Araç Talep Formu
                FormButton("Araç Talep Formu", Icons.Default.DirectionsBus) {
                    activeForm = "Vehicle"
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ONAY / RED BUTONLARI
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = { viewModel.rejectEvent(event.id) { resultType = "Rejected"; showResultDialog = true } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) { Text("Reddet", color = Color.Red, fontWeight = FontWeight.Bold) }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { viewModel.verifyEvent(event.id) { resultType = "Approved"; showResultDialog = true } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) { Text("Onayla", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold) }
                }
            }

            // AÇILIR PENCERELER (DIALOGS)
            if (activeForm != null) {
                Dialog(onDismissRequest = { activeForm = null }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                            // Dialog Başlığı
                            Text(
                                text = if(activeForm == "EventRequest") "Etkinlik Talep Formu" else "Araç Talep Formu",
                                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBlue
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // DİALOG İÇERİĞİ
                            if (activeForm == "EventRequest") {
                                // PDF GÖSTERİMİ
                                if (event.formUrl.isNotEmpty()) {
                                    Text(
                                        "Bu etkinlik için yüklenen formu görüntülemek için butona tıklayın:",
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { uriHandler.openUri(event.formUrl) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Eğer Download ikonu yoksa varsayılan başka bir ikon kullanıyoruz
                                        Icon(Icons.Default.Description, null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("PDF Dosyasını Aç")
                                    }
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.ErrorOutline, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Bu etkinlik için PDF formu yüklenmemiş.", color = Color.Gray, textAlign = TextAlign.Center)
                                    }
                                }
                            } else if (activeForm == "Vehicle") {
                                // ARAÇ KARTI GÖSTERİMİ
                                if (vehicleRequest != null) {
                                    VehicleRequestCard(vehicleRequest!!)
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.DirectionsBus, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Bu etkinlik için araç talebi bulunmamaktadır.", color = Color.Gray)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Kapat Butonu
                            Button(
                                onClick = { activeForm = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Kapat", color = Color.Black)
                            }
                        }
                    }
                }
            }

            // İşlem Sonucu Dialog'u (Onaylandı/Reddedildi)
            if (showResultDialog) {
                Dialog(onDismissRequest = { showResultDialog = false; navController.popBackStack() }) {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            val icon = if(resultType == "Approved") Icons.Default.CheckCircle else Icons.Default.Cancel
                            val color = if(resultType == "Approved") Color(0xFF2E7D32) else Color.Red

                            Icon(icon, null, tint = color, modifier = Modifier.size(50.dp))
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                if (resultType == "Approved") "Etkinlik Onaylandı!" else "Etkinlik Reddedildi!",
                                fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBlue
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { showResultDialog = false; navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Tamam")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun FormButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF3EFFF),
        modifier = Modifier.fillMaxWidth().height(60.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(icon, null, tint = DarkBlue)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DarkBlue)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = DarkBlue.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun VehicleRequestCard(request: VehicleRequest) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DirectionsBus, null, tint = Color(0xFF1565C0))
            Spacer(modifier = Modifier.width(8.dp))
            Text(request.vehicleType, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Kalkış: ${request.pickupLocation}", fontSize = 14.sp)
        Text("Varış: ${request.destination}", fontSize = 14.sp)
        Text("Yolcu: ${request.passengerCount}", fontSize = 14.sp)
        if (request.notes.isNotEmpty()) Text("Not: ${request.notes}", fontSize = 12.sp, color = Color.Gray)
    }
}