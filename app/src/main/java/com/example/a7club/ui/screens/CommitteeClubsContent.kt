package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.a7club.data.Resource
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

@Composable
fun CommitteeClubsContent(
    navController: androidx.navigation.NavController,
    viewModel: StudentFlowViewModel
) {
    val clubsState by viewModel.clubsState
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Arama Çubuğu (Öğrenci ekranıyla tasarım uyumu için)
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Kulüp ara...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = DarkBlue) },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF8F8F8),
                focusedContainerColor = Color(0xFFF8F8F8),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        when (val state = clubsState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message ?: "Hata oluştu", color = Color.Red)
                }
            }
            is Resource.Success -> {
                val filteredClubs = (state.data ?: emptyList()).filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mevcut StudentClubCard'ı kullanıyoruz, böylece tasarım bozulmaz
                    items(filteredClubs) { club ->
                        StudentClubCard(club) {
                            // Kulüp YK için özel bir aksiyon istersen buraya ekleyebilirsin
                        }
                    }
                }
            }
        }
    }
}