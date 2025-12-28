@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun PersonnelEventDetailScreen(
    navController: NavController,
    eventName: String,
    clubName: String,
    viewModel: PersonnelViewModel = viewModel()
) {
    val event = viewModel.pendingEvents.collectAsState().value.find { it.title == eventName }
    
    // UI State'leri
    var activeForm by remember { mutableStateOf<String?>(null) } // "EVENT" or "VEHICLE"
    var showResultDialog by remember { mutableStateOf(false) }
    var resultType by remember { mutableStateOf("") } // "Approved" or "Rejected"
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(modifier = Modifier.background(LightPurple).padding(top = 32.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) { Icon(Icons.Default.Menu, null, tint = DarkBlue) }
                    Text("Etkinlik Talepleri", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkBlue)
                    IconButton(onClick = { }) { Icon(Icons.Default.Notifications, null, tint = DarkBlue) }
                }
            }
        },
        bottomBar = {
            PersonnelMainBottomBar(
                navController = navController,
                selectedIndex = -1,
                initialIsExpanded = true,
                onIndexSelected = { index ->
                    if (index == 0) navController.navigate(Routes.PersonnelHomeScreen.route)
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { 
                        if (activeForm != null) activeForm = null else navController.popBackStack() 
                    }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = DarkBlue)
                    }
                }

                if (activeForm == null) {
                    // --- ANA DETAY GÖRÜNÜMÜ ---
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(modifier = Modifier.fillMaxWidth(0.85f).height(65.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFFEEEAFF)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = eventName, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Surface(modifier = Modifier.fillMaxWidth().height(220.dp), shape = RoundedCornerShape(24.dp), color = Color(0xFFF1EDFF)) {
                        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.Center) {
                            Text(text = "Etkinlik Yeri: ${event?.location ?: "Belirtilmemiş"}", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Etkinlik Saati: ${event?.time ?: "Belirtilmemiş"}", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "İletişim Tel No: ${event?.contactPhone ?: "Belirtilmemiş"}", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { activeForm = "EVENT" }, modifier = Modifier.fillMaxWidth(0.85f).height(55.dp).shadow(4.dp, RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1C4E9))) {
                            Text("Etkinlik Talep Formu", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                        Button(onClick = { activeForm = "VEHICLE" }, modifier = Modifier.fillMaxWidth(0.85f).height(55.dp).shadow(4.dp, RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1C4E9))) {
                            Text("Araç Talep Formu", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                } else {
                    // --- FORM GÖRÜNÜMÜ (ETKİNLİK VEYA ARAÇ) ---
                    val formTitle = if (activeForm == "EVENT") "Etkinlik Talep Formu" else "Araç Talep Formu"
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(modifier = Modifier.fillMaxWidth(0.85f).height(65.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFFEEEAFF)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = formTitle, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Form Görseli Alanı
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(Color.White, RoundedCornerShape(12.dp)).shadow(2.dp, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(120.dp), tint = DarkBlue.copy(alpha = 0.2f))
                        Text(if (activeForm == "EVENT") "ETKİNLİK FORMU" else "ARAÇ FORMU", color = DarkBlue.copy(alpha = 0.4f), modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ONAY - RET BUTONLARI
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp), 
                        horizontalArrangement = Arrangement.Center, 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reddet", 
                            color = Color(0xFF5E5CE6), 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            modifier = Modifier.clickable {
                                event?.let { e ->
                                    viewModel.rejectEvent(e.id) {
                                        resultType = "Rejected"
                                        showResultDialog = true
                                    }
                                }
                            }.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(40.dp))
                        
                        Text(
                            text = "Onayla", 
                            color = Color(0xFF5E5CE6), 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            modifier = Modifier.clickable {
                                event?.let { e ->
                                    viewModel.verifyEvent(e.id) {
                                        resultType = "Approved"
                                        showResultDialog = true
                                    }
                                }
                            }.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            // --- SONUÇ DİYALOĞU ---
            if (showResultDialog) {
                val formTitle = if (activeForm == "EVENT") "Etkinlik Talep Formu" else "Araç Talep Formu"
                Dialog(onDismissRequest = { }) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.9f).height(220.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xFFB3E5FC)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (resultType == "Approved") "$formTitle\nONAYLANDI" else "$formTitle\nREDDEDİLDİ",
                                textAlign = TextAlign.Center,
                                color = DarkBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                lineHeight = 28.sp
                            )
                            
                            Button(
                                onClick = { 
                                    showResultDialog = false
                                    activeForm = null // Formdan çık, ana detaya dön
                                    // Eğer her iki form da onaylanmışsa popBackStack yapılabilir
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Text("Tamam", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
