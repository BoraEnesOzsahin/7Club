package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a7club.ui.screens.ClubCommitteeLoginScreen
import com.example.a7club.ui.screens.CreateEventScreen
import com.example.a7club.ui.screens.CreateVehicleRequestScreen
import com.example.a7club.ui.screens.EventDetailScreen
import com.example.a7club.ui.screens.EventsScreen
import com.example.a7club.ui.screens.InterestQuestionScreen
import com.example.a7club.ui.screens.MainScreen
import com.example.a7club.ui.screens.PersonnelLoginScreen
import com.example.a7club.ui.screens.RoleSelectionScreen
import com.example.a7club.ui.screens.StudentFlowViewModel
import com.example.a7club.ui.screens.StudentLoginScreen
import com.example.a7club.ui.viewmodels.AuthViewModel

@Composable
fun NavGraph(showSnackbar: (String) -> Unit) {
    val navController = rememberNavController()
    val studentFlowViewModel: StudentFlowViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel() // Ortak AuthViewModel oluşturuldu

    // Uygulama başlangıç rotasını rol seçimine ayarlayalım
    NavHost(navController = navController, startDestination = Routes.RoleSelection.route) {
        composable(Routes.MainScreen.route) {
            MainScreen(navController)
        }
        composable(Routes.RoleSelection.route) {
            // ViewModel'i resetleyerek her giriş denemesinin temiz başlamasını sağla
            authViewModel.resetLoginState()
            RoleSelectionScreen(navController, showSnackbar)
        }
        composable(Routes.StudentLogin.route) {
            StudentLoginScreen(navController, authViewModel, showSnackbar)
        }
        composable(Routes.ClubCommitteeLogin.route) {
            // DÜZELTİLDİ: AuthViewModel eklendi
            ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar)
        }
        composable(Routes.PersonnelLogin.route) {
             // DÜZELTİLDİ: AuthViewModel eklendi
            PersonnelLoginScreen(navController, authViewModel, showSnackbar)
        }
        composable(
            route = Routes.InterestQuestion.route,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 1
            InterestQuestionScreen(
                navController = navController, 
                questionIndex = index,
                onInterestSelected = { interest -> studentFlowViewModel.interests.add(interest) }
            )
        }
        composable(Routes.Events.route) {
            EventsScreen(navController, studentFlowViewModel, showSnackbar)
        }
        composable(Routes.CreateVehicleRequest.route) {
            CreateVehicleRequestScreen()
        }
        composable(Routes.CreateEvent.route) {
            CreateEventScreen(navController, showSnackbar)
        }
        composable(
            route = Routes.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(eventId = eventId, navController = navController, showSnackbar = showSnackbar)
        }
    }
}
