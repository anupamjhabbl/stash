package com.example.stash.presentation.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface StashRoutes: NavKey {
    @Serializable
    data object HomeScreen : StashRoutes

    @Serializable
    data class DockerScreen(val stashCategoryId: Long) : StashRoutes
}