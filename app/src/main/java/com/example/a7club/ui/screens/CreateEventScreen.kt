package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.ui.viewmodels.CreateEventViewModel

@Composable
fun CreateEventScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit,
    createEventViewModel: CreateEventViewModel = viewModel()
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Create an event")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = createEventViewModel.title,
            onValueChange = { createEventViewModel.title = it },
            label = { Text("Event Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = createEventViewModel.description,
            onValueChange = { createEventViewModel.description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = createEventViewModel.clubName,
            onValueChange = { createEventViewModel.clubName = it },
            label = { Text("Club Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                createEventViewModel.createEvent(
                    onSuccess = {
                        showSnackbar("Event successfully created!")
                        navController.popBackStack()
                    },
                    onError = {
                        showSnackbar(it)
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create an event")
        }
    }
}
