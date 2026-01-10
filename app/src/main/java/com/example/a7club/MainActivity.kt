package com.example.a7club

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.a7club.ui.theme._7ClubTheme
import com.example.a7club.ui.navigation.NavGraph
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash Screen (Başlangıç ekranı) yüklemesi
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _7ClubTheme {
                // NavController ve ViewModel oluşturuluyor
                val navController = rememberNavController()
                val studentViewModel: StudentFlowViewModel = viewModel()

                // Snackbar durumu
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                // --- 1. ADIM: DİL DEĞİŞİMİ SONRASI HEDEFİ AL ---
                // Profil ekranından gelen "start_destination" bilgisini okuyoruz.
                val startDestination = intent.getStringExtra("start_destination")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        studentViewModel = studentViewModel,
                        modifier = Modifier.padding(innerPadding),
                        showSnackbar = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }

                // --- 2. ADIM: YÖNLENDİRME YAP ---
                // Uygulama açıldığında çalışır. Eğer özel bir hedef varsa oraya gider.
                LaunchedEffect(Unit) {
                    if (startDestination != null) {
                        navController.navigate(startDestination) {
                            // Geri tuşuna basınca Splash veya Login ekranına dönmesin diye geçmişi temizle
                            popUpTo(0)
                        }
                    }
                }
            }
        }
    }
}