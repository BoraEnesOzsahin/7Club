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
    object StudentProfile : Routes("student_profile")
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
    object FormSelection : Routes("form_selection") // YENİ
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
    object EventRequestForm : Routes("event_request_form/{eventName}") {
        fun createRoute(eventName: String) = "event_request_form/$eventName"
    }
    object VehicleRequestForm : Routes("vehicle_request_form/{eventName}") {
        fun createRoute(eventName: String) = "vehicle_request_form/$eventName"
    }
    
    // PERSONEL ROTALARI
    object PersonnelHomeScreen : Routes("personnel_home_screen?tabIndex={tabIndex}") {
        fun createRoute(tabIndex: Int = 0) = "personnel_home_screen?tabIndex=$tabIndex"
    }
    object PersonnelEventRequests : Routes("personnel_event_requests") 
    object PersonnelOverdueEvents : Routes("personnel_overdue_events") // YENİ: 2 günden eski talepler
    object PersonnelPastEvents : Routes("personnel_past_events")
    
    object PersonnelEventDetail : Routes("personnel_event_detail/{eventName}/{clubName}") {
        fun createRoute(eventName: String, clubName: String) = "personnel_event_detail/$eventName/$clubName"
    }
    object PersonnelClubDetail : Routes("personnel_club_detail/{clubName}") {
        fun createRoute(clubName: String) = "personnel_club_detail/$clubName"
    }
    object PersonnelClubMembers : Routes("personnel_club_members/{clubName}") {
        fun createRoute(clubName: String) = "personnel_club_members/$clubName"
    }
    object PersonnelClubEvents : Routes("personnel_club_events/{clubName}/{isPast}") {
        fun createRoute(clubName: String, isPast: Boolean) = "personnel_club_events/$clubName/$isPast"
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

    // YENİ EKRAN
    object MyEvents : Routes("my_events")
    object MyClubs : Routes("my_clubs")
    object MyReviews : Routes("my_reviews")
    object EventReview : Routes("event_review/{eventName}/{clubName}") {
        fun createRoute(eventName: String, clubName: String) = "event_review/$eventName/$clubName"
    }
    object PastEvents : Routes("past_events")
}