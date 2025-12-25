package com.example.stash.presentation.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface StashRoutes: NavKey {
    @Serializable
    data object HomeScreen : StashRoutes

    @Serializable
    data object AuthenticationFormScreen: StashRoutes

    @Serializable
    data object ForgotPasswordScreen: StashRoutes

    @Serializable
    data object ResetPasswordScreen: StashRoutes

    @Serializable
    data class OTPVerificationScreen(
        val userEmail: String,
        val origin: String,
        val userId: String
    ) : StashRoutes

    @Serializable
    data class DockerScreen(val stashCategoryId: String) : StashRoutes
}