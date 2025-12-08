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
import com.example.a7club.ui.viewmodels.CommitteeEventViewModel

@Composable
fun CreateVehicleRequestScreen(committeeEventViewModel: CommitteeEventViewModel = viewModel()) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Submit Vehicle Request")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = committeeEventViewModel.vehicleType,
            onValueChange = { committeeEventViewModel.vehicleType = it },
            label = { Text("Vehicle Type") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = committeeEventViewModel.pickupLocation,
            onValueChange = { committeeEventViewModel.pickupLocation = it },
            label = { Text("Pickup Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = committeeEventViewModel.dropoffLocation,
            onValueChange = { committeeEventViewModel.dropoffLocation = it },
            label = { Text("Dropoff Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = committeeEventViewModel.passengerCount,
            onValueChange = { committeeEventViewModel.passengerCount = it },
            label = { Text("Passenger Count") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = committeeEventViewModel.notes,
            onValueChange = { committeeEventViewModel.notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // TODO: Replace with actual eventId and clubId
                committeeEventViewModel.submitVehicleRequest("dummyEventId", "dummyClubId")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Request")
        }
    }
}
