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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.a7club.ui.theme._7ClubTheme
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

// --- KRİTİK IMPORT ---
// Eğer NavGraph dosyan 'ui/navigation' paketindeyse bu satır şarttır.
import com.example.a7club.ui.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash Screen (Başlangıç ekranı) yüklemesi
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _7ClubTheme {
                // NavController ve ViewModel burada oluşturulur
                val navController = rememberNavController()
                val studentViewModel: StudentFlowViewModel = viewModel()

                // Snackbar (alt bilgi çubuğu) durumu
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    // NavGraph çağrısı
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
            }
        }
    }
}