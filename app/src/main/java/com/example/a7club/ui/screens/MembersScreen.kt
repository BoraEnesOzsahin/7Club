package com.example.a7club.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(navController: NavController) {
    // Örnek Veri (ViewModel'e bağlanabilir)
    val members = listOf(
        Pair("202101001", "Fatma Zülal Baltacı"),
        Pair("202101002", "Bora Enes Özşahin"),
        Pair("202101003", "Yağmur Direkçi"),
        Pair("202202015", "Sami Sidar"),
        Pair("202305012", "Azra Sağdıç")
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Üyeler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // ORTAK BOTTOM BAR
            ClubAdminBottomAppBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Tablo Başlığı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.width(32.dp))
                Text("Öğrenci No", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1f))
                Text("İsim Soyisim", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1.5f))
            }
            Divider(color = Color.LightGray)

            // Üye Listesi
            LazyColumn {
                itemsIndexed(members) { index, member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                            fontSize = 16.sp,
                            modifier = Modifier.width(32.dp)
                        )
                        Text(
                            text = member.first,
                            color = DarkBlue,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = member.second,
                            color = DarkBlue,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1.5f),
                            textAlign = TextAlign.Start
                        )
                    }
                    Divider(color = Color(0xFFF0F0F0))
                }
            }
        }
    }
}