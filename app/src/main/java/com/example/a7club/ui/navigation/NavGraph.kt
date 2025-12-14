package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a7club.ui.screens.CreateVehicleRequestScreen
import com.example.a7club.ui.screens.EventsScreen
import com.example.a7club.ui.screens.InterestQuestionScreen
import com.example.a7club.ui.screens.MainScreen
import com.example.a7club.ui.screens.RoleSelectionScreen
import com.example.a7club.ui.screens.StudentFlowViewModel
import com.example.a7club.ui.screens.StudentLoginScreen
import com.example.a7club.ui.viewmodels.AuthViewModel

@Composable
fun NavGraph(showSnackbar: (String) -> Unit) {
    val navController = rememberNavController()
    val viewModel: StudentFlowViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // Start Destination değiştirildi: Routes.MainScreen.route -> Routes.RoleSelection.route
    NavHost(navController = navController, startDestination = Routes.RoleSelection.route) {
        composable(Routes.MainScreen.route) {
            MainScreen(navController)
        }
        composable(Routes.RoleSelection.route) {
            RoleSelectionScreen(navController, showSnackbar)
        }
        composable(Routes.StudentLogin.route) {
            StudentLoginScreen(navController, authViewModel, showSnackbar)
        }
        composable(
            route = Routes.InterestQuestion.route,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 1
            InterestQuestionScreen(navController, viewModel, index)
        }
        composable(Routes.Events.route) {
            EventsScreen(navController, viewModel, showSnackbar)
        }
        // Öğrenci akışında araç talebi ekranına gerek yok, bu sadece kulüp yönetiminde olmalı.
        // Ancak şu an için bu rotayı kaldırmak yerine sadece kulüp girişiyle erişilebilir yapacağız.
        composable(Routes.CreateVehicleRequest.route) {
            CreateVehicleRequestScreen()
        }
    }
}
