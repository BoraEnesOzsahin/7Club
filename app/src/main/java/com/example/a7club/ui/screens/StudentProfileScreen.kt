package com.example.a7club.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource // ÖNEMLİ
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a7club.R // ÖNEMLİ
import com.example.a7club.ui.navigation.Routes
import java.util.Locale

// ... (Renkler ve changeAppLanguage fonksiyonu aynı kalıyor) ...
val ProfileLightPurple = Color(0xFFCCC2FF)
val ProfileDarkBlue = Color(0xFF160092)
val ProfileBg = Color(0xFFF9F8FD)

@Composable
fun StudentProfileScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val currentLang = context.resources.configuration.locales[0].language

    Scaffold(
        containerColor = ProfileBg,
        topBar = {
            ProfileTopBar(onNotificationClick = {
                navController.navigate(Routes.StudentNotifications)
            })
        },
        bottomBar = {
            // BURASI GÜNCELLENDİ: Etiketler XML'den
            StudentBottomBar(
                selectedIndex = 3,
                onIndexSelected = { index ->
                    when(index) {
                        0 -> { navController.navigate(Routes.Events.route) { popUpTo(Routes.Events.route) { inclusive = true } } }
                        1 -> {}
                        2 -> {}
                        3 -> {}
                    }
                },
                onCenterClick = {}
            )
        }
    ) { innerPadding ->
        // ... (İçerik, butonlar vb. önceki kodun aynısı - stringResource kullanıyorlar zaten) ...
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFFE6E3F6)))
            Spacer(modifier = Modifier.height(24.dp))
            ProfileOptionCard(text = "Ege Örter", isButton = false)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileOptionCard(text = stringResource(id = R.string.logout), isButton = true, onClick = { navController.navigate(Routes.StudentLogin.route) { popUpTo(0) } })
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.language_selection), style = MaterialTheme.typography.bodyMedium, color = ProfileDarkBlue, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LanguageButton(text = stringResource(id = R.string.english), isSelected = currentLang.startsWith("en"), onClick = { if (!currentLang.startsWith("en")) changeAppLanguage(context, "en") }, modifier = Modifier.weight(1f))
                LanguageButton(text = stringResource(id = R.string.turkish), isSelected = currentLang.startsWith("tr"), onClick = { if (!currentLang.startsWith("tr")) changeAppLanguage(context, "tr") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

// --- ÜST BAR (GÜNCELLENDİ) ---
@Composable
fun ProfileTopBar(onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(ProfileLightPurple).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.size(28.dp))
        // BAŞLIK: "Profile" / "Profil"
        Text(text = stringResource(id = R.string.profile), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = ProfileDarkBlue)
        // BİLDİRİM İKONU
        IconButton(onClick = onNotificationClick) {
            Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.notifications), tint = ProfileDarkBlue, modifier = Modifier.size(28.dp))
        }
    }
}

// ... (ProfileOptionCard ve LanguageButton aynı kalıyor) ...
@Composable
fun ProfileOptionCard(text: String, isButton: Boolean, onClick: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxWidth().height(60.dp).then(if (isButton) Modifier.clickable(onClick = onClick) else Modifier), shape = RoundedCornerShape(12.dp), color = Color(0xFFDCD6FA)) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = ProfileDarkBlue, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
fun LanguageButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.height(40.dp).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), color = if (isSelected) ProfileDarkBlue else ProfileLightPurple) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = if (isSelected) Color.White else ProfileDarkBlue, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

// ... (changeAppLanguage fonksiyonu önceki cevaptakiyle aynı) ...
fun changeAppLanguage(context: Context, languageCode: String) {
    if (context is Activity) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        val intent = Intent(context, context::class.java)
        intent.putExtra("start_destination", Routes.StudentProfileScreen.route)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.finish()
        context.startActivity(intent)
    }
}