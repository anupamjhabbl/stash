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
import com.example.stash.auth.composables.AuthenticationFormScreen
import com.example.stash.auth.composables.ForgotPasswordScreen
import com.example.stash.auth.composables.OTPVerificationScreen
import com.example.stash.auth.composables.PasswordResetScreen
import com.example.stash.presentation.composables.HomeStashScreen
import com.example.stash.presentation.composables.StashDockerScreen
import com.example.stash.presentation.navigation.routes.StashRoutes
import com.example.stash.presentation.scene.CategoryDockerScene.Companion.homePane
import com.example.stash.presentation.scene.CategoryDockerScene.Companion.stashDockerPane
import com.example.stash.presentation.scene.rememberListDetailSceneStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot(
    isUserLogged: Boolean
) {
    val initialScreen = if (isUserLogged) {
        StashRoutes.HomeScreen
    } else {
        StashRoutes.AuthenticationFormScreen
    }

    val navBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(
                    NavKey::class
                ) {
                    subclass(StashRoutes.HomeScreen::class, StashRoutes.HomeScreen.serializer())
                    subclass(StashRoutes.DockerScreen::class, StashRoutes.DockerScreen.serializer())
                    subclass(StashRoutes.AuthenticationFormScreen::class, StashRoutes.AuthenticationFormScreen.serializer())
                    subclass(StashRoutes.OTPVerificationScreen::class, StashRoutes.OTPVerificationScreen.serializer())
                    subclass(StashRoutes.ForgotPasswordScreen::class, StashRoutes.ForgotPasswordScreen.serializer())
                    subclass(StashRoutes.ResetPasswordScreen::class, StashRoutes.ResetPasswordScreen.serializer())
                }
            }
        },
         initialScreen
    )
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = navBackStack,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        sceneStrategy = listDetailStrategy,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<StashRoutes.HomeScreen>(
                metadata = homePane()
            ) {
                HomeStashScreen { item ->
                    navBackStack.add(StashRoutes.DockerScreen(item))
                }
            }

            entry<StashRoutes.DockerScreen>(
                metadata = stashDockerPane()
            ) {
                StashDockerScreen(it.stashCategoryId) {
                    navBackStack.removeLastOrNull()
                }
            }

            entry<StashRoutes.AuthenticationFormScreen> {
                AuthenticationFormScreen(
                    onForgotPasswordClick = {
                        navBackStack.add(StashRoutes.ForgotPasswordScreen)
                    },
                    goToHomeScreen = {
                        navBackStack.clear()
                        navBackStack.add(StashRoutes.HomeScreen)
                    },
                    onGoToOtpVerificationScreen = { email, origin, userId ->
                        navBackStack.add(StashRoutes.OTPVerificationScreen(email, origin, userId))
                    }
                )
            }

            entry<StashRoutes.ForgotPasswordScreen> {
                ForgotPasswordScreen(
                    onGoToOtpVerificationScreen = { email, origin, userId ->
                        navBackStack.add(StashRoutes.OTPVerificationScreen(email, origin, userId))
                    },
                    onGoBack = {
                        navBackStack.removeLastOrNull()
                    }
                )
            }

            entry<StashRoutes.ResetPasswordScreen> {
                PasswordResetScreen(
                    goToHome = {
                        navBackStack.clear()
                        navBackStack.add(StashRoutes.HomeScreen)
                    }
                )
            }

            entry<StashRoutes.OTPVerificationScreen> { entry ->
                OTPVerificationScreen(
                    userEmail = entry.userEmail,
                    origin = entry.origin,
                    userId = entry.userId,
                    goToResetPasswordScreen = {
                        navBackStack.clear()
                        navBackStack.add(StashRoutes.ResetPasswordScreen)
                    },
                    goToHomeScreen = {
                        navBackStack.clear()
                        navBackStack.add(StashRoutes.HomeScreen)
                    },
                    onGoBack = {
                        navBackStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}