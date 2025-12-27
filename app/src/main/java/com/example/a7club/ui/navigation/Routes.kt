package com.example.a7club.ui.navigation

sealed class Route(val route: String) {
    // --- Giriş & Karşılama ---
    object Splash : Route("splash")
    object RoleSelection : Route("role_selection")
    object StudentLogin : Route("student_login")
    object ClubCommitteeLogin : Route("club_committee_login")
    object PersonnelLogin : Route("personnel_login")

    // --- Ana Ekranlar ---
    object Events : Route("events")
    object ClubHomeScreen : Route("club_home_screen")
    object MainScreen : Route("main_screen")
    object PersonnelHomeScreen : Route("personnel_home_screen")

    object SettingsScreen : Route("settings")
    object NotificationsScreen : Route("notifications")
    object FilterScreen : Route("filter_screen")

    // --- Kulüp Yönetim & Profil ---
    object ClubProfileScreen : Route("club_profile")
    object MembersScreen : Route("members_screen")
    object ContactInfoScreen : Route("contact_info_screen")
    object Forms : Route("forms_screen")
    object EventCalendarScreen : Route("event_calendar_screen")

    // --- Listeler ---
    object PendingEventForms : Route("pending_event_forms")
    object RejectedEventForms : Route("rejected_event_forms")
    object PastEventForms : Route("past_event_forms")

    // --- Form Oluşturma & Talep ---
    object CreateVehicleRequest : Route("create_vehicle_request")
    object CreateEvent : Route("create_event")
    object EventRequestForm : Route("event_request_form")
    object VehicleRequestForm : Route("vehicle_request_form")

    // --- Parametreli Rotalar ---
    object InterestQuestion : Route("interest_question/{index}") {
        fun createRoute(index: Int) = "interest_question/$index"
    }
    object EventDetail : Route("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object ParticipantInfoForm : Route("participant_info_form/{fromNewForm}") {
        fun createRoute(fromNewForm: Boolean) = "participant_info_form/$fromNewForm"
    }
    object PastEventDetail : Route("past_event_detail/{eventName}") {
        fun createRoute(eventName: String) = "past_event_detail/$eventName"
    }
    object PendingEventDetail : Route("pending_event_detail/{eventName}") {
        fun createRoute(eventName: String) = "pending_event_detail/$eventName"
    }
    object RejectedEventDetail : Route("rejected_event_detail/{eventName}") {
        fun createRoute(eventName: String) = "rejected_event_detail/$eventName"
    }


    object PersonnelEventDetail : Route("personnel_event_detail/{title}/{club}") {
        fun createRoute(title: String, club: String) = "personnel_event_detail/$title/$club"
    }
    object PersonnelClubDetail : Route("personnel_club_detail/{clubName}") {
        fun createRoute(clubName: String) = "personnel_club_detail/$clubName"
    }
}

object Routes {
    val Splash = Route.Splash
    val RoleSelection = Route.RoleSelection
    val StudentLogin = Route.StudentLogin
    val ClubCommitteeLogin = Route.ClubCommitteeLogin
    val PersonnelLogin = Route.PersonnelLogin
    val Events = Route.Events
    val ClubHomeScreen = Route.ClubHomeScreen
    val MainScreen = Route.MainScreen
    val PersonnelHomeScreen = Route.PersonnelHomeScreen

    val SettingsScreen = Route.SettingsScreen
    val NotificationsScreen = Route.NotificationsScreen
    val FilterScreen = Route.FilterScreen
    val ClubProfileScreen = Route.ClubProfileScreen
    val MembersScreen = Route.MembersScreen
    val ContactInfoScreen = Route.ContactInfoScreen
    val Forms = Route.Forms
    val EventCalendarScreen = Route.EventCalendarScreen

    val PendingEventForms = Route.PendingEventForms
    val RejectedEventForms = Route.RejectedEventForms
    val PastEventForms = Route.PastEventForms

    val CreateVehicleRequest = Route.CreateVehicleRequest
    val CreateEvent = Route.CreateEvent
    val EventRequestForm = Route.EventRequestForm
    val VehicleRequestForm = Route.VehicleRequestForm

    val InterestQuestion = Route.InterestQuestion
    val EventDetail = Route.EventDetail
    val ParticipantInfoForm = Route.ParticipantInfoForm
    val PastEventDetail = Route.PastEventDetail
    val PendingEventDetail = Route.PendingEventDetail
    val RejectedEventDetail = Route.RejectedEventDetail


    val PersonnelEventDetail = Route.PersonnelEventDetail
    val PersonnelClubDetail = Route.PersonnelClubDetail
}