package com.example.stash.presentation.scene

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.example.stash.presentation.scene.CategoryDockerScene.Companion.HOME_STASH_SCENE
import com.example.stash.presentation.scene.CategoryDockerScene.Companion.STASH_DOCKER_SCENE

class CategoryDockerSceneStrategy<T: Any>(
    val windowSizeClass: WindowSizeClass
): SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val stashDockerEntry = entries
            .lastOrNull()
            ?.takeIf {
                it.metadata.containsKey(STASH_DOCKER_SCENE)
            } ?: return null

        val homeStashEntry = entries
            .findLast {
                it.metadata.containsKey(HOME_STASH_SCENE)
            } ?: return null

        return CategoryDockerScene(
            homeStashScreen = homeStashEntry,
            stashDockerScreen = stashDockerEntry,
            key = homeStashEntry.contentKey,
            previousEntries = entries.dropLast(1)
        )
    }
}

@Composable
fun <T : Any> rememberListDetailSceneStrategy(): CategoryDockerSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    return remember(windowSizeClass) {
        CategoryDockerSceneStrategy(windowSizeClass)
    }
}