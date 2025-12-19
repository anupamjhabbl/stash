package com.example.stash.presentation.navigation.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.stash.presentation.composables.HomeStashScreen
import com.example.stash.presentation.composables.StashDockerScreen
import com.example.stash.presentation.navigation.routes.StashRoutes
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot() {
    val navBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(
                    NavKey::class
                ) {
                    subclass(StashRoutes.HomeScreen::class, StashRoutes.HomeScreen.serializer())
                    subclass(StashRoutes.DockerScreen::class, StashRoutes.DockerScreen.serializer())
                }
            }
        },
         StashRoutes.HomeScreen
    )

    NavDisplay(
        backStack = navBackStack,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<StashRoutes.HomeScreen> {
                HomeStashScreen { item ->
                    navBackStack.add(StashRoutes.DockerScreen(item))
                }
            }

            entry<StashRoutes.DockerScreen> {
                StashDockerScreen(it.stashCategoryId)
            }
        }
    )
}