package com.example.a7club.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a7club.ui.theme.DarkBlue
import com.example.a7club.ui.theme.LightPurple

data class ContactInfo(
    val id: Int,
    val title: String,
    val value: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactInfoScreen(navController: NavController) {
    // Dinamik Liste (YK üyeleri ekleme/çıkarma yapabilir)
    val contactList = remember { 
        mutableStateListOf(
            ContactInfo(1, "Kulüp Whatsapp Grubu Linki", "https://chat.whatsapp.com/HF4rJ84rlu42x6ybbacv7W"),
            ContactInfo(2, "İnstagram Hesabı", "https://www.instagram.com/yeditepeyukek/"),
            ContactInfo(3, "Kulüp Maili", "yukek2025@gmail.com")
        )
    }

    // Diyalog State'leri
    var showAddEditDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var inputLabel by remember { mutableStateOf("") }
    var inputValue by remember { mutableStateOf("") }
    var currentEditingId by remember { mutableIntStateOf(-1) }
    var isNewItem by remember { mutableStateOf(false) }

    if (showAddEditDialog) {
        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = { Text(if(isNewItem) "Yeni Bilgi Ekle" else "Düzenle", color = DarkBlue, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    if(isNewItem) {
                        OutlinedTextField(
                            value = dialogTitle,
                            onValueChange = { dialogTitle = it },
                            label = { Text("Başlık (Örn: LinkedIn)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        label = { Text("Bilgi/Link") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isNewItem) {
                            contactList.add(ContactInfo(contactList.size + 1, dialogTitle, inputValue))
                        } else {
                            val index = contactList.indexOfFirst { it.id == currentEditingId }
                            if (index != -1) {
                                contactList[index] = contactList[index].copy(value = inputValue)
                            }
                        }
                        showAddEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) { Text("İptal", color = Color.Gray) }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("İletişim Bilgileri", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = { IconButton(onClick = { }) { Icon(Icons.Default.Menu, "Menu", tint = DarkBlue) } },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.Notifications, "Notifications", tint = DarkBlue) } },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(16.dp)),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightPurple)
            )
        },
        bottomBar = { ClubBottomAppBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.Start).padding(top = 16.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = DarkBlue)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dinamik Kart Listesi
            contactList.forEach { info ->
                ContactCard(
                    title = info.title,
                    value = info.value,
                    onEdit = {
                        isNewItem = false
                        currentEditingId = info.id
                        inputValue = info.value
                        showAddEditDialog = true
                    },
                    onDelete = {
                        contactList.remove(info)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // YENİ EKLE BUTONU
            Button(
                onClick = {
                    isNewItem = true
                    dialogTitle = ""
                    inputValue = ""
                    showAddEditDialog = true
                },
                modifier = Modifier.fillMaxWidth(0.85f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Yeni İletişim Kanalı Ekle", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ContactCard(title: String, value: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9))
        ) {
            Row(
                Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 15.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Delete, "Sil", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFFF))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (value.isEmpty()) "Henüz bilgi eklenmemiş..." else value,
                    modifier = Modifier.weight(1f),
                    color = if (value.isEmpty()) Color.Gray else DarkBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, "Düzenle", tint = DarkBlue.copy(alpha = 0.6f))
                }
            }
        }
    }
}
