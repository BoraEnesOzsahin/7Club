package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a7club.ui.screens.*
import com.example.a7club.ui.viewmodels.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    showSnackbar: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }
        composable(Routes.RoleSelection.route) {
            RoleSelectionScreen(navController, showSnackbar)
        }

        // Login Screens
        composable(Routes.StudentLogin.route) { StudentLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.ClubLogin.route) { ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.AdminLogin.route) { PersonnelLoginScreen(navController, authViewModel, showSnackbar) }

        // Main user screens
        composable(Routes.Events.route) { EventsScreen(navController) }
        composable(Routes.ClubHomeScreen.route) { ClubHomeScreen(navController, showSnackbar) }
        composable(
            route = Routes.PersonnelHomeScreen.route,
            arguments = listOf(navArgument("tabIndex") { 
                type = NavType.IntType
                defaultValue = 0 
            })
        ) {
             val initialTabIndex = it.arguments?.getInt("tabIndex") ?: 0
             PersonnelHomeScreen(navController = navController, authViewModel = authViewModel, initialTabIndex = initialTabIndex)
        }

        // Additional Screens
        composable(Routes.SettingsScreen.route) { SettingsScreen(navController, showSnackbar) }
        composable(Routes.EventCalendarScreen.route) { EventCalendarScreen(navController, showSnackbar) }
        composable(Routes.ClubProfileScreen.route) { ClubProfileScreen(navController, showSnackbar) }
        composable(Routes.NotificationsScreen.route) { NotificationsScreen(navController) }
        composable(Routes.MembersScreen.route) { MembersScreen(navController) }
        composable(Routes.ContactInfoScreen.route) { ContactInfoScreen(navController) }
        composable(Routes.Forms.route) { FormsScreen(navController) }
        composable(Routes.Profile.route) { PersonnelProfileScreen(navController, authViewModel) }
        
        // Other Navigation
        composable(Routes.MainScreen.route) { MainScreen(navController) }
        composable(Routes.ClubCommitteeLogin.route) { ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.PersonnelLogin.route) { PersonnelLoginScreen(navController, authViewModel, showSnackbar) }
        
        // Event Detail with argument
        composable(
            route = Routes.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(navController, eventId, showSnackbar)
        }

        // Newly created screen
        composable(Routes.MyEvents.route) {
            MyEventsScreen(navController)
        }
        composable(Routes.MyClubs.route) {
            MyClubsScreen(navController)
        }
        composable(Routes.MyReviews.route) {
            MyReviewsScreen(navController)
        }
        composable(
            route = Routes.EventReview.route,
            arguments = listOf(
                navArgument("eventName") { type = NavType.StringType },
                navArgument("clubName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            val clubName = backStackEntry.arguments?.getString("clubName") ?: ""
            EventReviewScreen(navController, eventName, clubName)
        }
        composable(Routes.PastEvents.route) {
            PastEventsScreen(navController)
        }
    }
}
