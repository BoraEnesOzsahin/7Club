package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme._7ClubTheme

@Composable
fun InterestQuestionScreen(
    navController: NavController, 
    questionIndex: Int, 
    onInterestSelected: (Int?) -> Unit
) {
    val questionText = "İlgi alanı sorusu $questionIndex"
    val options = listOf("Seçenek 1", "Seçenek 2", "Seçenek 3", "Seçenek 4")

    // This function is defined inside the Composable to have access to its parameters.
    fun navigateNext() {
        if (questionIndex < 5) {
            navController.navigate(Routes.InterestQuestion.createRoute(questionIndex + 1))
        } else {
            // Use the .route property to pass the String path
            navController.navigate(Routes.Events.route) {
                popUpTo(Routes.RoleSelection.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = questionText)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            options.forEachIndexed { index, text ->
                OptionCard(text = text) {
                    onInterestSelected(index)
                    navigateNext()
                }
            }
        }
        
        Button(
            onClick = {
                onInterestSelected(null) // Pass null for "geç"
                navigateNext()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
        ) {
            Text("geç", color = Color.Black)
        }
    }
}

@Composable
private fun OptionCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InterestQuestionScreenPreview() {
    _7ClubTheme {
        InterestQuestionScreen(
            navController = rememberNavController(),
            onInterestSelected = {},
            questionIndex = 1
        )
    }
}
