@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.model.User
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun PersonnelClubMembersScreen(
    navController: NavController,
    clubName: String,
    viewModel: PersonnelViewModel = viewModel()
) {
    LaunchedEffect(clubName) {
        viewModel.fetchClubMembers(clubName)
    }

    val members by viewModel.currentClubMembers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("$clubName Üyeleri", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DarkBlue)
            } else if (members.isEmpty()) {
                Text(
                    text = "Henüz üye yok.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(members) { user ->
                        MemberCard(user)
                    }
                }
            }
        }
    }
}

@Composable
fun MemberCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth().height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightPurple)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = DarkBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = user.fullName, fontWeight = FontWeight.Bold, color = DarkBlue)

                // ARTIK HATA VERMEYECEK (Model güncellendiği için)
                // Eğer null gelirse (örn: personel ise) "ID Yok" yerine Email gösterilebilir veya boş bırakılabilir.
                val subText = user.studentId ?: user.email
                Text(text = subText, fontSize = 12.sp, color = DarkBlue.copy(alpha = 0.7f))
            }
        }
    }
}