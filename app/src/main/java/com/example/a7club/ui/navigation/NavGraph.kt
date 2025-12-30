package com.example.a7club.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Tüm ekranları ve ViewModel'ları içeri al
import com.example.a7club.ui.screens.*
import com.example.a7club.ui.viewmodels.AuthViewModel
import com.example.a7club.ui.viewmodels.StudentFlowViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    studentViewModel: StudentFlowViewModel,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        modifier = modifier
    ) {
        // --- GİRİŞ VE KARŞILAMA ---
        composable(Routes.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Routes.RoleSelection.route) {
            RoleSelectionScreen(navController = navController, showSnackbar = showSnackbar)
        }
        composable(Routes.StudentLogin.route) {
            StudentLoginScreen(
                navController = navController,
                viewModel = authViewModel,
                showSnackbar = showSnackbar
            )
        }
        composable(Routes.ClubCommitteeLogin.route) {
            ClubCommitteeLoginScreen(
                navController = navController,
                viewModel = authViewModel,
                showSnackbar = showSnackbar
            )
        }
        composable(Routes.PersonnelLogin.route) {
            PersonnelLoginScreen(
                navController = navController,
                viewModel = authViewModel,
                showSnackbar = showSnackbar
            )
        }

        // --- ANA AKIŞLAR ---
        composable(Routes.Events.route) {
            EventsScreen(
                navController = navController,
                viewModel = studentViewModel,
                showSnackbar = showSnackbar
            )
        }

        composable(Routes.PersonnelHomeScreen.route) { MainScreen(navController = navController) }
        composable(Routes.MainScreen.route) { MainScreen(navController = navController) }

        composable(Routes.ClubHomeScreen.route) {
            ClubHomeScreen(
                navController = navController,
                showSnackbar = showSnackbar,
                viewModel = studentViewModel
            )
        }

        // --- PROFİL VE BİLDİRİMLER (GÜNCELLENDİ) ---
        composable(Routes.NotificationsScreen.route) {
            NotificationsScreen(navController = navController)
        }

        // Yeni Profil Ekranı
        composable(Routes.StudentProfileScreen.route) {
            StudentProfileScreen(navController = navController)
        }

        // Öğrenci Bildirimleri
        composable(Routes.StudentNotifications) {
            StudentNotificationsScreen(navController = navController)
        }

        composable(Routes.FilterScreen.route) {
            FilterScreen(navController = navController)
        }

        // --- KULÜP YÖNETİM ---
        composable(Routes.ClubProfileScreen.route) {
            ClubProfileScreen(navController = navController, showSnackbar = showSnackbar)
        }
        composable(Routes.MembersScreen.route) { MembersScreen(navController = navController) }
        composable(Routes.ContactInfoScreen.route) { ContactInfoScreen(navController = navController) }

        composable(Routes.EventCalendarScreen.route) {
            EventCalendarScreen(
                navController = navController,
                showSnackbar = showSnackbar
            )
        }

        // --- FORMLAR VE LİSTELER ---
        composable(Routes.Forms.route) { FormsScreen(navController = navController) }
        composable(Routes.PendingEventForms.route) { PendingEventFormsScreen(navController = navController) }
        composable(Routes.RejectedEventForms.route) { RejectedEventFormsScreen(navController = navController) }
        composable(Routes.PastEventForms.route) { PastEventFormsScreen(navController = navController) }

        // --- OLUŞTURMA VE DOSYA ---
        composable(Routes.CreateEvent.route) {
            CreateEventScreen(navController = navController, showSnackbar = showSnackbar)
        }
        composable(Routes.CreateVehicleRequest.route) { CreateVehicleRequestScreen() }
        composable(Routes.EventRequestForm.route) { EventRequestFormScreen(navController = navController) }
        composable(Routes.VehicleRequestForm.route) { VehicleRequestFormScreen(navController = navController) }

        // --- DETAY SAYFALARI ---
        composable(
            route = Routes.InterestQuestion.route,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 1
            InterestQuestionScreen(
                navController = navController,
                questionIndex = index,
                onInterestSelected = { studentViewModel.addInterest(it) }
            )
        }

        composable(
            route = Routes.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                navController = navController,
                showSnackbar = showSnackbar
            )
        }

        composable(
            route = Routes.ParticipantInfoForm.route,
            arguments = listOf(navArgument("fromNewForm") { type = NavType.BoolType })
        ) { backStackEntry ->
            val fromNewForm = backStackEntry.arguments?.getBoolean("fromNewForm") ?: false
            ParticipantInfoFormScreen(navController = navController, fromNewForm = fromNewForm)
        }

        composable(
            route = Routes.PastEventDetail.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            PastEventDetailScreen(navController = navController, eventName = eventName)
        }

        composable(
            route = Routes.PendingEventDetail.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            PendingEventDetailScreen(navController = navController, eventName = eventName)
        }

        composable(
            route = Routes.RejectedEventDetail.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            RejectedEventDetailScreen(navController = navController, eventName = eventName)
        }
    }
}