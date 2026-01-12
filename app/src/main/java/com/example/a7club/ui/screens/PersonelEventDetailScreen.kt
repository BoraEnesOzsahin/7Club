@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.model.VehicleRequest
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun PersonnelEventDetailScreen(
    navController: NavController,
    eventTitle: String,
    clubName: String,
    viewModel: PersonnelViewModel = viewModel()
) {
    val context = LocalContext.current

    // ViewModel'daki listelerden ilgili etkinliği buluyoruz
    val pendingEvents by viewModel.recentPendingEvents.collectAsState()
    val overdueEvents by viewModel.overduePendingEvents.collectAsState()
    val pastEvents by viewModel.pastEvents.collectAsState()

    // Etkinliği bul (Tüm listeleri tara)
    val event = pendingEvents.find { it.title == eventTitle && it.clubName == clubName }
        ?: overdueEvents.find { it.title == eventTitle && it.clubName == clubName }
        ?: pastEvents.find { it.title == eventTitle && it.clubName == clubName }

    // Etkinlik bulunduğunda Araç Talebini Çek
    LaunchedEffect(event) {
        event?.let { viewModel.loadVehicleRequest(it.id) }
    }
    val vehicleRequest by viewModel.selectedVehicleRequest.collectAsState()

    // Reddetme Diyaloğu Kontrolü
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = DarkBlue)
        }
        return
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. BAŞLIK KARTI
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = event.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = event.clubName, fontSize = 16.sp, color = DarkBlue.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        StatusChip(event.status)
                        Spacer(modifier = Modifier.width(8.dp))
                        CategoryChip(event.category)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. BİLGİ KUTUCUKLARI
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoBox(icon = Icons.Default.Event, title = "Tarih", value = event.dateString, modifier = Modifier.weight(1f))
                InfoBox(icon = Icons.Default.LocationOn, title = "Konum", value = event.location, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- İŞTE EKSİK OLAN KISIM BURASIYDI ---
            // Bu kısım sayesinde "Katıl" diyen öğrencilerin sayısını görebileceksin.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Katılımcı Sayısı
                val participantCount = event.participants.size
                InfoBox(
                    icon = Icons.Default.Person, // İkon
                    title = "Katılımcı",
                    value = "$participantCount Kişi",
                    modifier = Modifier.weight(1f)
                )

                // Görsel denge için boşluk (İstersen buraya Kontenjan vs. de ekleyebilirsin)
                Spacer(modifier = Modifier.weight(1f))
            }
            // ---------------------------------------

            Spacer(modifier = Modifier.height(20.dp))

            // 3. AÇIKLAMA
            Text("Etkinlik Açıklaması", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. ARAÇ TALEBİ KARTI (EĞER VARSA)
            if (vehicleRequest != null) {
                VehicleRequestInfoCard(vehicleRequest!!)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 5. ETKİNLİK FORMU LİNKİ
            if (event.formUrl.isNotEmpty()) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.formUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPurple)
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = DarkBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Etkinlik Formunu Görüntüle", color = DarkBlue, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 6. ONAY / RED BUTONLARI (Sadece BEKLEYEN etkinlikler için)
            if (event.status == "PENDING") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Reddet Butonu
                    Button(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.weight(1f).height(55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text("Reddet", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    // Onayla Butonu
                    Button(
                        onClick = {
                            viewModel.approveEvent(event) {
                                Toast.makeText(context, "Etkinlik Onaylandı!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.weight(1f).height(55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                    ) {
                        Text("Onayla", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // REDDETME DİYALOĞU
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Etkinliği Reddet", fontWeight = FontWeight.Bold, color = DarkBlue) },
            text = {
                Column {
                    Text("Lütfen ret sebebini giriniz:", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Sebep...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectEvent(event, rejectionReason) {
                            Toast.makeText(context, "Etkinlik Reddedildi.", Toast.LENGTH_SHORT).show()
                            showRejectDialog = false
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Reddet")
                }
            },
            dismissButton = {
                Button(onClick = { showRejectDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text("İptal", color = Color.Black)
                }
            },
            containerColor = Color.White
        )
    }
}

// --- YARDIMCI UI BİLEŞENLERİ ---

// --- YARDIMCI UI BİLEŞENLERİ ---

// GÜNCELLENMİŞ InfoBox Kodu
@Composable
fun InfoBox(icon: ImageVector, title: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            // .height(60.dp) // <-- BU SATIR SİLİNDİ (Sorun buydu, sığmıyordu)
            .heightIn(min = 70.dp) // <-- YENİ: En az 70dp olsun ama yazı uzunsa otomatik uzasın
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp), // İç boşluklar düzenlendi
        verticalAlignment = Alignment.CenterVertically // İkon ve metinleri dikeyde ortalar
    ) {
        Icon(icon, null, tint = DarkBlue, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f) // Metin alanı kalan boşluğu doldursun
        ) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp)) // Başlık ile değer arasına nefes payı
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
                maxLines = 2, // <-- GÜNCELLENDİ: Uzun konumlar kesilmesin, 2 satıra inebilsin
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

// ... diğer bileşenler (VehicleRequestInfoCard, StatusChip vb.) aynı kalacak ...

@Composable
fun VehicleRequestInfoCard(request: VehicleRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Mavi tonu
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsBus, null, tint = DarkBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Araç Talebi Mevcut", fontWeight = FontWeight.Bold, color = DarkBlue)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Tip: ${request.vehicleType}", color = DarkBlue)
            Text("Yolcu: ${request.passengerCount} Kişi", color = DarkBlue)
            Text("Güzergah: ${request.pickupLocation} -> ${request.destination}", color = DarkBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, text) = when (status) {
        "APPROVED" -> Color(0xFF4CAF50) to "Onaylandı"
        "REJECTED" -> Color(0xFFF44336) to "Reddedildi"
        else -> Color(0xFFFF9800) to "Onay Bekliyor"
    }
    Surface(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryChip(category: String) {
    Surface(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.DarkGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}