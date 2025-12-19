package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// DÜZELTME: Tüm ekranları tek bir import ile alıyoruz. 'screensimport' silindi.
import com.example.a7club.ui.screens.*
import com.example.a7club.ui.viewmodels.AuthViewModel

@Composable
fun NavGraph(showSnackbar: (String) -> Unit) {
    val navController = rememberNavController()
    val studentFlowViewModel: StudentFlowViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.RoleSelection) {
        composable(Routes.MainScreen) { MainScreen(navController) }
        composable(Routes.RoleSelection) {
            authViewModel.resetLoginState()
            RoleSelectionScreen(navController, showSnackbar)
        }
        composable(Routes.StudentLogin) { StudentLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.ClubCommitteeLogin) { ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.PersonnelLogin) { PersonnelLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.Events) { EventsScreen(navController, studentFlowViewModel, showSnackbar) }
        composable(Routes.Settings) { SettingsScreen(navController) }
        composable(Routes.CreateVehicleRequest) { CreateVehicleRequestScreen() }
        composable(Routes.CreateEvent) { CreateEventScreen(navController, showSnackbar) }
        composable(
            route = "${Routes.InterestQuestion}/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 1
            InterestQuestionScreen(
                navController = navController,
                questionIndex = index,
                onInterestSelected = { studentFlowViewModel.interests.add(it) }
            )
        }
        composable(
            route = "${Routes.EventDetail}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(eventId = eventId, navController = navController, showSnackbar = showSnackbar)
        }
    }
}
