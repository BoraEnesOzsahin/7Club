@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.theme.VeryLightPurple
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun PersonnelOverdueEventsScreen(
    navController: NavController,
    viewModel: PersonnelViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }
    // DÜZELTME: 2 günden eski (gecikmiş) talepleri çekiyoruz
    val overdueEvents by viewModel.overduePendingEvents.collectAsState()
    
    // Alt barın durumu (Yönetim ekranı olduğu için true - genişlemiş kalsın)
    var isMenuExpanded by remember { mutableStateOf(true) }

    val filteredEvents = overdueEvents.filter {
        it.title.contains(searchText, ignoreCase = true) || 
        it.clubName.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(modifier = Modifier.background(LightPurple).padding(top = 32.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) { Icon(Icons.Default.Menu, null, tint = DarkBlue) }
                    Text("Tüm Talepler (Gecikmiş)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkBlue)
                    IconButton(onClick = { }) { Icon(Icons.Default.Notifications, null, tint = DarkBlue) }
                }
            }
        },
        bottomBar = {
            PersonnelMainBottomBar(
                navController = navController,
                selectedIndex = -1,
                isMenuExpanded = isMenuExpanded,
                onMenuToggle = { isMenuExpanded = !isMenuExpanded },
                onIndexSelected = { index ->
                    navController.navigate(Routes.PersonnelHomeScreen.createRoute(index))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = DarkBlue)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Arama Çubuğu (Görseldeki Tasarım)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Taleplerde ara...", color = DarkBlue.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = DarkBlue) },
                    modifier = Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(25.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VeryLightPurple,
                        unfocusedContainerColor = VeryLightPurple,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = { }, modifier = Modifier.size(45.dp).background(VeryLightPurple, CircleShape)) { 
                    Icon(Icons.Default.Tune, null, tint = DarkBlue) 
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (filteredEvents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "2 günden eski bekleyen talep bulunmuyor.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                    items(filteredEvents) { event ->
                        EventRequestCard(event) {
                            navController.navigate(Routes.PersonnelEventDetail.createRoute(event.title, event.clubName))
                        }
                    }
                }
            }
        }
    }
}
