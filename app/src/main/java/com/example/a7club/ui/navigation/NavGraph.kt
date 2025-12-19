package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// Explicit imports for all screens to avoid resolution issues
import com.example.a7club.ui.screens.ClubCommitteeLoginScreen
import com.example.a7club.ui.screens.ClubHomeScreen
import com.example.a7club.ui.screens.ClubProfileScreen
import com.example.a7club.ui.screens.CreateEventScreen
import com.example.a7club.ui.screens.CreateVehicleRequestScreen
import com.example.a7club.ui.screens.EventCalendarScreen
import com.example.a7club.ui.screens.EventDetailScreen
import com.example.a7club.ui.screens.EventsScreen
import com.example.a7club.ui.screens.InterestQuestionScreen
import com.example.a7club.ui.screens.MainScreen
import com.example.a7club.ui.screens.NotificationsScreen
import com.example.a7club.ui.screens.PersonnelLoginScreen
import com.example.a7club.ui.screens.RoleSelectionScreen
import com.example.a7club.ui.screens.SettingsScreen
import com.example.a7club.ui.screens.StudentLoginScreen
import com.example.a7club.ui.viewmodels.AuthViewModel
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

@Composable
fun NavGraph(modifier: Modifier = Modifier, showSnackbar: (String) -> Unit) {
    val navController = rememberNavController()
    val studentFlowViewModel: StudentFlowViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.RoleSelection.route,
        modifier = modifier
    ) {
        composable(Routes.MainScreen.route) {
            MainScreen(navController)
        }
        composable(Routes.RoleSelection.route) {
            authViewModel.resetLoginState()
            RoleSelectionScreen(navController, showSnackbar)
        }
        composable(Routes.StudentLogin.route) {
            StudentLoginScreen(navController, authViewModel, showSnackbar)
        }
        composable(Routes.ClubCommitteeLogin.route) {
            ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar)
        }
        composable(Routes.PersonnelLogin.route) {
            PersonnelLoginScreen(navController, authViewModel, showSnackbar)
        }
        composable(Routes.ClubHomeScreen.route) {
            ClubHomeScreen(navController = navController, showSnackbar = showSnackbar, viewModel = studentFlowViewModel)
        }
        composable(Routes.SettingsScreen.route) {
            SettingsScreen(navController = navController, showSnackbar = showSnackbar)
        }
        composable(Routes.EventCalendarScreen.route) {
            EventCalendarScreen(navController = navController, showSnackbar = showSnackbar)
        }
        composable(Routes.ClubProfileScreen.route) { 
            ClubProfileScreen(navController = navController, showSnackbar = showSnackbar)
        }
        composable(Routes.NotificationsScreen.route) { 
            NotificationsScreen(navController = navController)
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
