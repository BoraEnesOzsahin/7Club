package com.example.a7club.ui.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object MainScreen : Routes("main_screen")
    object RoleSelection : Routes("role_selection")
    object StudentLogin : Routes("student_login")
    object ClubLogin : Routes("club_login")
    object AdminLogin : Routes("admin_login")
    object InterestQuestion : Routes("interest_question/{index}") {
        fun createRoute(index: Int) = "interest_question/$index"
    }
    object Events : Routes("events")
    object CreateEvent : Routes("create_event")
    object Discover : Routes("discover")
    object Clubs : Routes("clubs")
    object Profile : Routes("profile")
    object CreateVehicleRequest : Routes("create_vehicle_request")
    object ClubCommitteeLogin : Routes("club_committee_login")
    object PersonnelLogin : Routes("personnel_login")
    object ClubHomeScreen : Routes("club_home_screen")
    object SettingsScreen : Routes("settings_screen")
    object EventCalendarScreen : Routes("event_calendar_screen")
    object ClubProfileScreen : Routes("club_profile_screen")
    object NotificationsScreen : Routes("notifications_screen")
    object MembersScreen : Routes("members_screen")
    object ContactInfoScreen : Routes("contact_info_screen")
    object Forms : Routes("forms")
    object PastEventForms : Routes("past_event_forms")
    object PastEventDetail : Routes("past_event_detail/{eventName}") {
        fun createRoute(eventName: String) = "past_event_detail/$eventName"
    }
    object PendingEventForms : Routes("pending_event_forms")
    object PendingEventDetail : Routes("pending_event_detail/{eventName}") {
        fun createRoute(eventName: String) = "pending_event_detail/$eventName"
    }
    object RejectedEventForms : Routes("rejected_event_forms")
    object RejectedEventDetail : Routes("rejected_event_detail/{eventName}") {
        fun createRoute(eventName: String) = "rejected_event_detail/$eventName"
    }
    object EventRequestForm : Routes("event_request_form")
    object VehicleRequestForm : Routes("vehicle_request_form")
    
    // PERSONEL ROTALARI - Parametreli Home Screen
    object PersonnelHomeScreen : Routes("personnel_home_screen?tabIndex={tabIndex}") {
        fun createRoute(tabIndex: Int = 0) = "personnel_home_screen?tabIndex=$tabIndex"
    }
    object PersonnelEventRequests : Routes("personnel_event_requests") 
    object PersonnelPastEvents : Routes("personnel_past_events")
    
    object PersonnelEventDetail : Routes("personnel_event_detail/{eventName}/{clubName}") {
        fun createRoute(eventName: String, clubName: String) = "personnel_event_detail/$eventName/$clubName"
    }
    object PersonnelClubDetail : Routes("personnel_club_detail/{clubName}") {
        fun createRoute(clubName: String) = "personnel_club_detail/$clubName"
    }

    object ParticipantInfoForm : Routes("participant_info_form/{fromNewForm}") {
        fun createRoute(fromNewForm: Boolean) = "participant_info_form/$fromNewForm"
    }
    
    object EventDetail : Routes("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object ClubPosts : Routes("club_posts")
    object ClubEventForms : Routes("club_event_forms/{eventName}") {
        fun createRoute(eventName: String) = "club_event_forms/$eventName"
    }
}
