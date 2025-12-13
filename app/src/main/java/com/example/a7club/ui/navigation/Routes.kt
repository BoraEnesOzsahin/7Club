package com.example.a7club.ui.navigation

sealed class Routes(val route: String) {
    object MainScreen : Routes("main_screen")
    object RoleSelection : Routes("role_selection")
    object StudentLogin : Routes("student_login")
    object ClubLogin : Routes("club_login") // Yeni Rota
    object AdminLogin : Routes("admin_login") // Yeni Rota
    object InterestQuestion : Routes("interest_question/{index}") {
        fun createRoute(index: Int) = "interest_question/$index"
    }
    object Events : Routes("events")
    object Discover : Routes("discover")
    object Clubs : Routes("clubs")
    object Profile : Routes("profile")
    object CreateVehicleRequest : Routes("create_vehicle_request")
}
