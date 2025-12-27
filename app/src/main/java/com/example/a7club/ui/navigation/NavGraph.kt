package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a7club.ui.screens.*
import com.example.a7club.ui.viewmodels.AuthViewModel
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

@Composable
fun NavGraph(modifier: Modifier = Modifier, showSnackbar: (String) -> Unit) {
    val navController = rememberNavController()
    val studentFlowViewModel: StudentFlowViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        modifier = modifier
    ) {
        // --- BAŞLANGIÇ & GİRİŞ EKRANLARI ---
        composable(Routes.Splash.route) { SplashScreen(navController) }
        composable(Routes.RoleSelection.route) {
            authViewModel.resetLoginState()
            RoleSelectionScreen(navController, showSnackbar)
        }
        composable(Routes.StudentLogin.route) { StudentLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.ClubCommitteeLogin.route) { ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.PersonnelLogin.route) { PersonnelLoginScreen(navController, authViewModel, showSnackbar) }

        // --- PERSONEL ROLÜ ---
        composable(Routes.PersonnelHomeScreen.route) {
            PersonnelHomeScreen(navController, authViewModel)
        }

        // YENİ: Etkinlik Talepleri Sayfası
        composable(Routes.PersonnelEventRequests.route) {
            PersonnelEventRequestsScreen(navController)
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
            PersonnelEventDetailScreen(navController, eventName, clubName)
        }

        composable(
            route = Routes.PersonnelClubDetail.route,
            arguments = listOf(navArgument("clubName") { type = NavType.StringType })
        ) { backStackEntry ->
            val clubName = backStackEntry.arguments?.getString("clubName") ?: ""
            PersonnelClubDetailScreen(navController, clubName)
        }

        composable(Routes.Profile.route) {
            PersonnelProfileScreen(navController, authViewModel)
        }

        // --- ÖĞRENCİ AKIŞI ---
        composable(Routes.MainScreen.route) { MainScreen(navController) }
        composable(Routes.Events.route) { EventsScreen(navController, studentFlowViewModel, showSnackbar) }
        composable(
            route = Routes.InterestQuestion.route,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 1
            InterestQuestionScreen(navController, index, { studentFlowViewModel.interests.add(it) })
        }
        composable(
            route = Routes.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(eventId, navController, showSnackbar)
        }

        // --- KULÜP YÖNETİMİ & FORMLAR ---
        composable(Routes.ClubHomeScreen.route) {
            ClubHomeScreen(navController, showSnackbar, studentFlowViewModel)
        }
        composable(Routes.SettingsScreen.route) { SettingsScreen(navController = navController, showSnackbar = showSnackbar) }
        composable(Routes.CreateVehicleRequest.route) { CreateVehicleRequestScreen() }
        composable(Routes.CreateEvent.route) { CreateEventScreen(navController, showSnackbar) }
        composable(Routes.EventCalendarScreen.route) { EventCalendarScreen(navController, showSnackbar) }
        composable(Routes.ClubProfileScreen.route) { ClubProfileScreen(navController, showSnackbar) }
        composable(Routes.NotificationsScreen.route) { NotificationsScreen(navController) }
        composable(Routes.MembersScreen.route) { MembersScreen(navController) }
        composable(Routes.ContactInfoScreen.route) { ContactInfoScreen(navController) }
        composable(Routes.Forms.route) { FormsScreen(navController) }
        composable(Routes.EventRequestForm.route) { EventRequestFormScreen(navController) }
        composable(Routes.VehicleRequestForm.route) { VehicleRequestFormScreen(navController) }

        // Form Geçmişi ve Detaylar
        composable(Routes.PastEventForms.route) { PastEventFormsScreen(navController) }
        composable(
            route = Routes.PastEventDetail.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            PastEventDetailScreen(navController, eventName)
        }

        composable(Routes.PendingEventForms.route) { PendingEventFormsScreen(navController) }
        composable(
            route = Routes.PendingEventDetail.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            PendingEventDetailScreen(navController, eventName)
        }

        composable(Routes.RejectedEventForms.route) { RejectedEventFormsScreen(navController) }
        composable(
            route = Routes.RejectedEventDetail.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            RejectedEventDetailScreen(navController, eventName)
        }

        composable(
            route = Routes.ParticipantInfoForm.route,
            arguments = listOf(navArgument("fromNewForm") { type = NavType.BoolType })
        ) { backStackEntry ->
            val fromNewForm = backStackEntry.arguments?.getBoolean("fromNewForm") ?: false
            ParticipantInfoFormScreen(navController, fromNewForm)
        }

        composable(Routes.ClubPosts.route) {
            ClubPostsScreen(navController)
        }

        composable(
            route = Routes.ClubEventForms.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            ClubEventFormsScreen(navController, eventName)
        }
    }
}
