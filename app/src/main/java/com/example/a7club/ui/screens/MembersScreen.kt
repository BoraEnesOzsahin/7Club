package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(navController: NavController) {
    val members = listOf(
        Pair("202101001", "Fatma Zülal Baltacı"),
        Pair("202101002", "Bora Enes Özşahin"),
        Pair("202101003", "Yağmur Direkçi"),
        Pair("202202015", "Sami Sidar"),
        Pair("202305012", "Azra Sağdıç"),
        Pair("202104088", "Neslihan ERDEM"),
        Pair("202101007", "Sude ADISANOĞLU"),
        Pair("202101008", "Ege ÖRTER"),
        Pair("202101009", "Sevim Nazlı YEŞİLOVA")
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Üyeler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.Menu, "Menu", tint = DarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Bildirim */ }) {
                        Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
            ClubBottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFFF))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Text("No", modifier = Modifier.width(30.dp), fontWeight = FontWeight.ExtraBold, color = DarkBlue, fontSize = 12.sp)
                            Text("Okul No", modifier = Modifier.width(100.dp), fontWeight = FontWeight.ExtraBold, color = DarkBlue, fontSize = 12.sp)
                            Text("İsim Soyisim", fontWeight = FontWeight.ExtraBold, color = DarkBlue, fontSize = 12.sp)
                        }
                    }

                    itemsIndexed(members) { index, member ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}",
                                modifier = Modifier.width(30.dp),
                                fontWeight = FontWeight.Bold,
                                color = DarkBlue,
                                fontSize = 14.sp
                            )
                            Text(
                                text = member.first,
                                modifier = Modifier.width(100.dp),
                                color = DarkBlue.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Text(
                                text = member.second,
                                color = DarkBlue,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
