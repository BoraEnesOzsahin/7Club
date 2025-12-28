@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun PersonnelClubMembersScreen(
    navController: NavController,
    clubName: String,
    viewModel: PersonnelViewModel = viewModel()
) {
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }
    
    val members by viewModel.currentClubMembers.collectAsState()
    val isLoading by viewModel.isLoadingMembers.collectAsState()

    LaunchedEffect(clubName) {
        viewModel.fetchClubMembers(clubName)
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
                    Text("Kulüpler", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkBlue)
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
                    isMenuExpanded = false
                    navController.navigate(Routes.PersonnelHomeScreen.createRoute(index))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(0.7f).height(55.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF3EFFF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "Üyeler", fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF3EFFF)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DarkBlue)
                    } else if (members.isEmpty()) {
                        Text(
                            text = "Bu kulübe kayıtlı üye bulunamadı.",
                            modifier = Modifier.align(Alignment.Center),
                            color = DarkBlue.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(members) { index, member ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                        text = member.studentNo,
                                        color = DarkBlue,
                                        fontSize = 15.sp,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    Text(
                                        text = member.fullName,
                                        color = DarkBlue,
                                        fontSize = 15.sp,
                                        modifier = Modifier.weight(2f),
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
