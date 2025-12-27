package com.example.a7club.ui.components // Veya ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.a7club.ui.theme.DarkBlue // Projenin ana rengi

@Composable
fun SearchAndFilterBar(
    onSearchClick: () -> Unit = {}, // Arama'ya basınca ne olsun?
    onFilterClick: () -> Unit // Filtre'ye basınca ne olsun?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- 1. Arama Çubuğu (Soldaki Geniş Kısım) ---
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp)) // Hafif gölge
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Ara",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Etkinlik veya kulüp ara...",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- 2. Filtreleme Butonu (Sağdaki Kare Kısım) ---
        Surface(
            modifier = Modifier
                .size(50.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clickable(onClick = onFilterClick),
            shape = RoundedCornerShape(16.dp),
            color = DarkBlue // Buton rengi (Logodaki Lacivert)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Tune, // Filtre İkonu
                    contentDescription = "Filtrele",
                    tint = Color.White // İkon rengi
                )
            }
        }
    }
}