package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a7club.ui.screens.*
import com.example.a7club.ui.viewmodels.AuthViewModel
import com.example.a7club.ui.viewmodels.StudentFlowViewModel
import com.example.a7club.ui.viewmodels.PersonnelViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    showSnackbar: (String) -> Unit
) {
    // ViewModel'leri burada oluşturuyoruz
    val studentFlowViewModel: StudentFlowViewModel = viewModel()
    val personnelViewModel: PersonnelViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }

        // --- STUDENT ---
        composable(Routes.StudentProfile.route) {
            StudentProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(route = "my_events_screen") {
            StudentJoinedEventsScreen(navController = navController)
        }

        composable(Routes.RoleSelection.route) {
            RoleSelectionScreen(navController, showSnackbar)
        }
        composable(Routes.Clubs.route) {
            ClubsScreen(navController = navController)
        }

        // --- LOGIN SCREENS ---
        composable(Routes.StudentLogin.route) { StudentLoginScreen(navController, authViewModel, showSnackbar) }

        // DÜZELTME BURADA YAPILDI: Hem ClubLogin hem ClubCommitteeLogin eklendi.
        // Buton hangisini çağırırsa çağırsın artık çökmez.
        composable(Routes.ClubLogin.route) { ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.ClubCommitteeLogin.route) { ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar) }

        composable(Routes.PersonnelLogin.route) { PersonnelLoginScreen(navController, authViewModel, showSnackbar) }

        // --- MAIN USER SCREENS ---

        composable(Routes.Events.route) {
            EventsScreen(navController, viewModel = studentFlowViewModel)
        }

        composable(Routes.Discover.route) {
            DiscoverScreen(navController = navController)
        }

        composable(Routes.ClubHomeScreen.route) { ClubHomeScreen(navController, showSnackbar) }

        // --- PERSONEL EKRANLARI ---
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

        composable(
            route = Routes.PersonnelEventDetail.route,
            arguments = listOf(
                navArgument("eventName") { type = NavType.StringType },
                navArgument("clubName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            val clubName = backStackEntry.arguments?.getString("clubName") ?: ""

            PersonnelEventDetailScreen(
                navController = navController,
                eventTitle = eventName,
                clubName = clubName,
                viewModel = personnelViewModel
            )
        }

        composable(
            route = Routes.PersonnelClubDetail.route,
            arguments = listOf(navArgument("clubName") { type = NavType.StringType })
        ) { backStackEntry ->
            val clubName = backStackEntry.arguments?.getString("clubName") ?: ""
            PersonnelClubDetailScreen(navController, clubName)
        }

        composable(
            route = Routes.PersonnelClubMembers.route,
            arguments = listOf(navArgument("clubName") { type = NavType.StringType })
        ) { backStackEntry ->
            val clubName = backStackEntry.arguments?.getString("clubName") ?: ""
            PersonnelClubMembersScreen(navController, clubName)
        }

        // --- ADDITIONAL SCREENS ---
        composable(Routes.SettingsScreen.route) { SettingsScreen(navController, showSnackbar) }
        composable(Routes.EventCalendarScreen.route) { EventCalendarScreen(navController, showSnackbar) }
        composable(Routes.ClubProfileScreen.route) { ClubProfileScreen(navController, showSnackbar) }
        composable(Routes.NotificationsScreen.route) { NotificationsScreen(navController = navController) }
        composable(Routes.MembersScreen.route) { MembersScreen(navController) }
        composable(Routes.ContactInfoScreen.route) { ContactInfoScreen(navController) }
        composable(Routes.Forms.route) { FormsScreen(navController) }
        composable(Routes.Profile.route) { PersonnelProfileScreen(navController, authViewModel) }
        composable(Routes.MainScreen.route) { MainScreen(navController) }

        // --- EVENT DETAIL (ÖĞRENCİ İÇİN) ---
        composable(
            route = Routes.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(navController, eventId, showSnackbar, studentFlowViewModel)
        }

        // --- OTHER SCREENS ---
        composable(Routes.MyEvents.route) { MyEventsScreen(navController) }
        composable(Routes.MyClubs.route) { MyClubsScreen(navController) }
        composable(Routes.MyReviews.route) { MyReviewsScreen(navController) }

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