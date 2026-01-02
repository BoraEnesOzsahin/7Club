package com.example.a7club.model

sealed interface User {
    val uid: String
    val fullName: String
    val email: String
    val role: String // STUDENT, COMMITTEE, STAFF
}

data class Student(
    override val uid: String = "",
    override val fullName: String = "",
    override val email: String = "",
    override val role: String = "STUDENT",
    val studentId: String = "",
    val department: String = "",
    val enrolledClubs: List<String> = emptyList()
) : User

data class Personnel(
    override val uid: String = "",
    override val fullName: String = "",
    override val email: String = "",
    override val role: String = "STAFF",
    val department: String = ""
) : User