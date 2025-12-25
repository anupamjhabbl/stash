package com.example.stash.auth.entity

import kotlinx.serialization.Serializable

@Serializable
data class AuthToken(
    val accessToken: String = "",
    val refreshToken: String = ""
)

@Serializable
data class UserRegisteredId(
    val userId: String
)

@Serializable
data class PasswordResetResponse(
    val message: String
)