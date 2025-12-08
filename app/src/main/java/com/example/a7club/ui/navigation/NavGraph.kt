package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a7club.ui.screens.EventsScreen
import com.example.a7club.ui.screens.InterestQuestionScreen
import com.example.a7club.ui.screens.RoleSelectionScreen
import com.example.a7club.ui.screens.StudentFlowViewModel
import com.example.a7club.ui.screens.StudentLoginScreen

@Composable
fun NavGraph(showSnackbar: (String) -> Unit) {
    val navController = rememberNavController()
    val viewModel: StudentFlowViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.RoleSelection.route) {
        composable(Routes.RoleSelection.route) {
            RoleSelectionScreen(navController, showSnackbar)
        }
        composable(Routes.StudentLogin.route) {
            StudentLoginScreen(navController, viewModel, showSnackbar)
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
    }
}
