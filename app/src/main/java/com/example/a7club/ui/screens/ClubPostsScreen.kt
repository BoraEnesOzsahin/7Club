package com.example.a7club.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.a7club.ui.navigation.Routes
import com.example.a7club.ui.theme.*

// 1. Gönderi Modeli Tanımlandı
data class Post(
    val id: String = java.util.UUID.randomUUID().toString(),
    val clubName: String,
    val text: String,
    val imageUri: Uri? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubPostsScreen(navController: NavController) {
    var showCreatePanel by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Gönderiler") }
    
    // 2. Dinamik Gönderi Listesi (Yeni eklenenler burada görünecek)
    val posts = remember { mutableStateListOf<Post>(
        Post(clubName = "Women in Business Kulübü", text = "Etkinliğimize davetlisiniz!")
    ) }

    Scaffold(
        containerColor = VeryLightPurple,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gönderiler", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = { /* Menü */ }) {
                        Icon(Icons.Default.Menu, "Menu", tint = DarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Bildirimler */ }) {
                        Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = {
            PostsBottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TabItem(text = "Duyurular", isSelected = selectedTab == "Duyurular") { 
                    selectedTab = "Duyurular"
                    showCreatePanel = false 
                }
                Spacer(modifier = Modifier.width(8.dp))
                TabItem(text = "Gönderiler", isSelected = selectedTab == "Gönderiler") { 
                    selectedTab = "Gönderiler" 
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ARTI SEMBOLÜ
            Icon(
                Icons.Default.Add,
                contentDescription = "Yeni Ekle",
                tint = DarkBlue,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { showCreatePanel = !showCreatePanel }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (showCreatePanel) {
                // 3. Yeni Gönderi Paneli ve Gönder Fonksiyonu
                CreatePostPanel(
                    onSendPost = { text, uri ->
                        posts.add(0, Post(clubName = "YUKEK Kulübü", text = text, imageUri = uri))
                        showCreatePanel = false // Panel kapatılır ve listeye dönülür
                    }
                )
            } else {
                // 4. Dinamik Liste Gösterimi
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(posts) { post ->
                        PostCard(post)
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePostPanel(onSendPost: (String, Uri?) -> Unit) {
    var postText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // 5. Galeri Erişimi İçin Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fotoğraf Yükle Butonu (Launcher'ı tetikler)
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(65.dp)
                    .clickable { galleryLauncher.launch("image/*") },
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (selectedImageUri == null) {
                        Text("Fotoğraf Yükle", color = DarkBlue, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Fotoğraf Seçildi ✔", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metin Ekle Alanı
            Surface(
                modifier = Modifier.fillMaxWidth().height(220.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                TextField(
                    value = postText,
                    onValueChange = { postText = it },
                    placeholder = { 
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Metin Ekle", color = Color.Gray)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gönder Butonu (Listeye ekler)
            Button(
                onClick = { onSendPost(postText, selectedImageUri) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.width(140.dp).height(45.dp)
            ) {
                Text("Gönder", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9).copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Görsel Alanı (Seçilen resmi gösterir)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (post.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(post.imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Image, "No Image", tint = DarkBlue, modifier = Modifier.size(48.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFB04A33)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = post.clubName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkBlue)
            }
            
            if (post.text.isNotEmpty()) {
                Text(text = post.text, fontSize = 13.sp, color = DarkBlue, modifier = Modifier.padding(start = 32.dp, top = 4.dp))
            }
        }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) DarkBlue else Color(0xFFD1C4E9),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else DarkBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PostsBottomAppBar(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(75.dp).clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = LightPurple
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostNavItem(Icons.Default.Groups, "Kulübüm") { navController.navigate(Routes.ClubProfileScreen.route) }
                PostNavItem(Icons.Default.Assignment, "Formlar") { navController.navigate(Routes.Forms.route) }
                Spacer(modifier = Modifier.width(90.dp))
                PostNavItem(Icons.Default.Collections, "Gönderiler", isSelected = true) { /* Buradayız */ }
                PostNavItem(Icons.Default.EventAvailable, "Etkinlikler") { navController.navigate(Routes.EventCalendarScreen.route) }
            }
        }
        Surface(
            modifier = Modifier.size(90.dp).align(Alignment.TopCenter).border(6.dp, Color.White, CircleShape).clickable { navController.navigate(Routes.ClubHomeScreen.route) },
            shape = CircleShape, color = DarkBlue, shadowElevation = 8.dp
        ) {}
    }
}

@Composable
fun PostNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = label, tint = DarkBlue, modifier = Modifier.size(28.dp))
        Text(text = label, color = DarkBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
