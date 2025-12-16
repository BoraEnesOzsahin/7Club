package com.example.a7club.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.R
import com.example.a7club.data.models.Event
import com.example.a7club.ui.viewmodels.EventDetailViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    navController: NavController, 
    showSnackbar: (String) -> Unit,
    eventDetailViewModel: EventDetailViewModel = viewModel()
) {
    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        isLoading = true
        val docRef = Firebase.firestore.collection("events").document(eventId)
        try {
            val document = docRef.get().await()
            event = document.toObject(Event::class.java)
        } catch (e: Exception) {
            showSnackbar("Etkinlik yüklenemedi: ${e.message}")
        }
        isLoading = false
    }

    Scaffold(
        containerColor = Color(0xFFF3F1FF),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(event?.clubName ?: "", fontWeight = FontWeight.Bold) },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    } 
                },
                actions = {
                    IconButton(onClick = { showSnackbar("Bildirimler tıklandı") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFE8E5FF))
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (event != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Club Logo and Name
                    Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Kulüp Logosu", modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Event Title Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = event!!.title.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp).fillMaxWidth()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Event Details Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Etkinlik Yeri: ${event!!.location}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Etkinlik Saati: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(event!!.startTime))}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Etkinlik Tarihi: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(event!!.startTime))}")
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Sign Up Button
                    Button(
                        onClick = { showConfirmationDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Etkinliğe Katıl", fontSize = 16.sp)
                    }
                }
            } else {
                Text("Etkinlik bulunamadı.", modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = {
                Text("Katılım Onayı")
            },
            text = {
                Text("'${event?.title ?: ""}' etkinliğine katılmak istediğine emin misin?")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
                    onClick = {
                        eventDetailViewModel.signUpForEvent(eventId, "dummyStudentId")
                        showSnackbar("Etkinliğe başarıyla kayıt oldun!")
                        showConfirmationDialog = false
                    }
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text("Hayır")
                }
            }
        )
    }
}
