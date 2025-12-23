package com.example.stash.presentation.scene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene

class CategoryDockerScene<T: Any>(
    val homeStashScreen: NavEntry<T>,
    val stashDockerScreen: NavEntry<T>,
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>
): Scene<T> {
    override val entries: List<NavEntry<T>>
        get() = listOf(homeStashScreen, stashDockerScreen)

    override val content: @Composable (() -> Unit)
        get() = {
            Row(
                Modifier.fillMaxSize()
            ) {
                Box(Modifier.weight(4f)) {
                    homeStashScreen.Content()
                }

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                )

                Box(Modifier.weight(6f)) {
                    stashDockerScreen.Content()
                }
            }
        }

    companion object {
        const val HOME_STASH_SCENE = "homeStashScreenScene"
        const val STASH_DOCKER_SCENE = "stashDockerScene"

        fun homePane() = mapOf(HOME_STASH_SCENE to true)

        fun stashDockerPane() = mapOf(STASH_DOCKER_SCENE to true)
    }
}