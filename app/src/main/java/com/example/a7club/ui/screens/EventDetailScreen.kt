package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a7club.data.models.Event
import com.example.a7club.ui.viewmodels.EventDetailViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@Composable
fun EventDetailScreen(eventId: String, eventDetailViewModel: EventDetailViewModel = viewModel()) {
    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(eventId) {
        isLoading = true
        val docRef = Firebase.firestore.collection("events").document(eventId)
        try {
            val document = docRef.get().await()
            event = document.toObject(Event::class.java)
        } catch (e: Exception) {
            // Handle error
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (event != null) {
            Text("Event Details")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Title: ${event!!.title}")
            Text(text = "Description: ${event!!.description}")
            Text(text = "Club: ${event!!.clubName}")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // TODO: Replace with actual studentId from auth
                eventDetailViewModel.signUpForEvent(eventId, "dummyStudentId")
            }) {
                Text("Sign Up For Event")
            }
        } else {
            Text("Event not found.")
        }
    }
}
