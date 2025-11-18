package com.example.a7club.ui.navigation

sealed class Routes(val route: String) {
    object RoleSelection : Routes("role_selection")
    object StudentLogin : Routes("student_login")
    object InterestQuestion : Routes("interest_question/{index}") {
        fun createRoute(index: Int) = "interest_question/$index"
    }
    object Events : Routes("events")
    object Discover : Routes("discover")
    object Clubs : Routes("clubs")
    object Profile : Routes("profile")
}