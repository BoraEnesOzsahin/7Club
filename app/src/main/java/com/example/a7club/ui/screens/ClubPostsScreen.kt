package com.example.a7club.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple
import com.example.a7club.ui.viewmodels.ClubPostsViewModel

data class Post(
    val id: String = "",
    val clubName: String = "",
    val text: String = "",
    val imageUri: Uri? = null,
    val timestamp: Long = 0
)

data class Announcement(
    val id: String = "",
    val clubName: String = "",
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val timestamp: Long = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubPostsScreen(navController: NavController, viewModel: ClubPostsViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.selectedImageUri = uri
    }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Yeni ekleme */ },
                containerColor = DarkBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
            }
        },
        bottomBar = {
            ClubAdminBottomAppBar(navController, currentRoute)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Paylaşımlar", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightPurple, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Modifier.weight(1f) RowScope içinde kullanılmalı
                TabButton(text = "Gönderiler", isSelected = selectedTab == 0, modifier = Modifier.weight(1f)) { selectedTab = 0 }
                TabButton(text = "Duyurular", isSelected = selectedTab == 1, modifier = Modifier.weight(1f)) { selectedTab = 1 }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (selectedTab == 0) {
                    items(viewModel.posts) { post ->
                        PostCard(post)
                    }
                } else {
                    items(viewModel.announcements) { announcement ->
                        AnnouncementCard(announcement)
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TabButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) DarkBlue else Color.Transparent,
            contentColor = if (isSelected) Color.White else DarkBlue
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier // Düzeltilen modifier buraya geliyor
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.Gray) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(post.clubName, fontWeight = FontWeight.Bold, color = DarkBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (post.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(post.imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(post.text, color = Color.Black)
        }
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // DÜZELTME: MainAxisAlignment (Flutter) -> horizontalArrangement (Compose)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(announcement.clubName, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 12.sp)
                Text(announcement.date, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(announcement.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(announcement.content, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}