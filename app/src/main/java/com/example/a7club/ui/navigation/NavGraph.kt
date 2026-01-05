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
import com.example.a7club.ui.viewmodels.CommitteeEventViewModel
import com.example.a7club.ui.viewmodels.EventRequestViewModel // Import edildi

@Composable
fun NavGraph(modifier: Modifier = Modifier, showSnackbar: (String) -> Unit) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val studentFlowViewModel: StudentFlowViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        modifier = modifier
    ) {
        // --- BAŞLANGIÇ ---
        composable(Routes.Splash.route) { SplashScreen(navController) }

        composable(Routes.RoleSelection.route) {
            authViewModel.resetLoginState()
            RoleSelectionScreen(navController, showSnackbar)
        }

        composable(Routes.StudentLogin.route) { StudentLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.ClubCommitteeLogin.route) { ClubCommitteeLoginScreen(navController, authViewModel, showSnackbar) }
        composable(Routes.PersonnelLogin.route) { PersonnelLoginScreen(navController, authViewModel, showSnackbar) }

        // --- PERSONEL ---
        composable(
            route = Routes.PersonnelHomeScreen.route,
            arguments = listOf(navArgument("tabIndex") { type = NavType.IntType; defaultValue = 0 })
        ) { backStackEntry ->
            val tabIndex = backStackEntry.arguments?.getInt("tabIndex") ?: 0
            PersonnelHomeScreen(navController, authViewModel, initialTabIndex = tabIndex)
        }

        composable(Routes.PersonnelEventRequests.route) { PersonnelEventRequestsScreen(navController) }
        composable(Routes.PersonnelOverdueEvents.route) { PersonnelOverdueEventsScreen(navController) }
        composable(Routes.PersonnelPastEvents.route) { PersonnelPastEventsScreen(navController) }

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

        composable(
            route = Routes.PersonnelClubMembers.route,
            arguments = listOf(navArgument("clubName") { type = NavType.StringType })
        ) { backStackEntry ->
            val clubName = backStackEntry.arguments?.getString("clubName") ?: ""
            PersonnelClubMembersScreen(navController, clubName)
        }

        composable(
            route = Routes.PersonnelClubEvents.route,
            arguments = listOf(
                navArgument("clubName") { type = NavType.StringType },
                navArgument("isPast") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val clubName = backStackEntry.arguments?.getString("clubName") ?: ""
            val isPast = backStackEntry.arguments?.getBoolean("isPast") ?: false
            PersonnelClubEventsScreen(navController, clubName, isPast)
        }

        composable(Routes.Profile.route) { PersonnelProfileScreen(navController, authViewModel) }

        // --- ÖĞRENCİ ---
        composable(Routes.MainScreen.route) { MainScreen(navController) }
        composable(Routes.Events.route) { EventsScreen(navController) }

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
            // GÜNCEL: Parametre sırası ve içeriği eşitlendi
            EventDetailScreen(navController, eventId, showSnackbar)
        }

        // --- KULÜP YÖNETİMİ ---
        composable(Routes.ClubHomeScreen.route) { ClubHomeScreen(navController, showSnackbar) }
        composable(Routes.SettingsScreen.route) { SettingsScreen(navController, showSnackbar) }
        composable(Routes.CreateEvent.route) { CreateEventScreen(navController, showSnackbar) }
        composable(Routes.EventCalendarScreen.route) { EventCalendarScreen(navController, showSnackbar) }
        composable(Routes.ClubProfileScreen.route) { ClubProfileScreen(navController, showSnackbar) }
        composable(Routes.NotificationsScreen.route) { NotificationsScreen(navController) }
        composable(Routes.MembersScreen.route) { MembersScreen(navController) }
        composable(Routes.ContactInfoScreen.route) { ContactInfoScreen(navController) }
        composable(Routes.Forms.route) { FormsScreen(navController) }
        composable(Routes.ClubPosts.route) { ClubPostsScreen(navController) }

        composable(
            route = Routes.EventRequestForm.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            // GÜNCEL: ViewModel oluşturulup parametre olarak verildi (Hata çözüldü)
            val requestViewModel: EventRequestViewModel = viewModel()
            EventRequestFormScreen(navController, eventName, requestViewModel)
        }

        composable(
            route = Routes.VehicleRequestForm.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            val committeeViewModel: CommitteeEventViewModel = viewModel()
            VehicleRequestFormScreen(navController, eventName, committeeViewModel)
        }

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

        composable(Routes.RejectedEventForms.route) {
            RejectedEventFormsScreen(navController)
        }

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

        composable(
            route = Routes.ClubEventForms.route,
            arguments = listOf(navArgument("eventName") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            ClubEventFormsScreen(navController, eventName)
        }

        composable(Routes.CreateVehicleRequest.route) { CreateVehicleRequestScreen() }
    }
}