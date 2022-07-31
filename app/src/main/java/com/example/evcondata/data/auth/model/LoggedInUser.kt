package com.example.evcondata.data.auth.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val username: String,
    val userId: String,
    val sessionToken: String
)