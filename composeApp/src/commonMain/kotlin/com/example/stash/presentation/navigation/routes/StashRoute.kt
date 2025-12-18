package com.example.stash.presentation.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface StashRoute: NavKey {
    @Serializable
    data object Main : StashRoute

    @Serializable
    data class Docker(val stashCategoryId: Long) : StashRoute
}