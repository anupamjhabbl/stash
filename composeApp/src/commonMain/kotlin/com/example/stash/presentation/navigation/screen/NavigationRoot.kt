package com.example.stash.presentation.navigation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.stash.presentation.composables.MainStashScreen
import com.example.stash.presentation.composables.StashDockerScreen
import com.example.stash.presentation.navigation.routes.StashRoute
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
                    subclass(StashRoute.Main::class, StashRoute.Main.serializer())
                    subclass(StashRoute.Docker::class, StashRoute.Docker.serializer())
                }
            }
        },
         StashRoute.Main
    )

    NavDisplay(
        backStack = navBackStack,
        modifier = Modifier.fillMaxSize(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<StashRoute.Main> {
                MainStashScreen { item ->
                    navBackStack.add(StashRoute.Docker(item))
                }
            }

            entry<StashRoute.Docker> {
                StashDockerScreen(it.stashCategoryId)
            }
        }
    )
}