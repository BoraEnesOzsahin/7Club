@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.data.Resource
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.EventDetailViewModel
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

// DÜZELTME: Parametre sırası NavGraph ile eşitlendi.
// (navController önce, eventId sonra)
@Composable
fun EventDetailScreen(
    navController: NavController,
    eventId: String,
    showSnackbar: (String) -> Unit,
    studentFlowViewModel: StudentFlowViewModel = viewModel(),
    eventDetailViewModel: EventDetailViewModel = viewModel()
) {
    val eventsState = studentFlowViewModel.eventsState.value
    val event = if (eventsState is Resource.Success) {
        eventsState.data?.find { it.id == eventId }
    } else null

    var showConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
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
        },
        bottomBar = {
            if (event != null) {
                Button(
                    onClick = { showConfirmationDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) {
                    Text("Etkinliğe Katıl", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        if (event == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Etkinlik yükleniyor...", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(LightPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = event.title.take(1).uppercase(),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = event.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.clubName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    EventInfoRow(Icons.Default.CalendarToday, event.dateString.ifEmpty { "Tarih Belirtilmemiş" })
                    Spacer(modifier = Modifier.height(12.dp))
                    EventInfoRow(Icons.Default.LocationOn, event.location.ifEmpty { "Konum Belirtilmemiş" })

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Etkinlik Hakkında",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = event.description,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color.Black.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Katılım Onayı", color = DarkBlue, fontWeight = FontWeight.Bold) },
            text = { Text("'${event?.title}' etkinliğine katılmak istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        eventDetailViewModel.signUpForEvent(eventId, "dummyStudentId")
                        showSnackbar("Kayıt başarılı! İyi eğlenceler.")
                        showConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) {
                    Text("Evet, Katıl")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Vazgeç", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun EventInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DarkBlue,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}