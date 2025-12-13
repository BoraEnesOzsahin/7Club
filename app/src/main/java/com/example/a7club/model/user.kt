package com.example.a7club.model

/**
 * Represents a user in the system with role-specific data.
 * This sealed interface ensures type safety, making it clear which fields
 * are available for which role.
 */
sealed interface User {
    val uid: String
    val fullName: String
    val email: String
}

/**
 * Represents a Student user.
 * `studentId` and `department` are non-nullable and therefore mandatory.
 */
data class Student(
    override val uid: String = "",
    override val fullName: String = "",
    override val email: String = "",
    val studentId: String = "",
    val department: String = "",
    val followedClubs: List<String> = emptyList()
) : User

/**
 * Represents a Club Committee user.
 * For now, they have the same properties as a Student, so we use a typealias.
 */
typealias ClubCommittee = Student

/**
 * Represents a Personnel (Staff) user.
 * Does not have a `studentId`. `department` is mandatory.
 */
data class Personnel(
    override val uid: String = "",
    override val fullName: String = "",
    override val email: String = "",
    val department: String = ""
) : User
