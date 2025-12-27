package com.bbl.stash.presentation.navigation.screen

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import com.bbl.stash.auth.composables.AuthenticationFormScreen
import com.bbl.stash.auth.composables.ForgotPasswordScreen
import com.bbl.stash.auth.composables.OTPVerificationScreen
import com.bbl.stash.auth.composables.PasswordResetScreen
import com.bbl.stash.presentation.composables.HomeStashScreen
import com.bbl.stash.presentation.composables.StashDockerScreen
import com.bbl.stash.presentation.navigation.routes.StashRoutes
import com.bbl.stash.presentation.scene.CategoryDockerScene.Companion.homePane
import com.bbl.stash.presentation.scene.CategoryDockerScene.Companion.stashDockerPane
import com.bbl.stash.presentation.scene.rememberListDetailSceneStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot(
    isUserLogged: Boolean,
    startSync: () -> Unit,
    stopSync: () -> Unit
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
        transitionSpec = {
            slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
        },
        popTransitionSpec = {
            slideInHorizontally { -it } + fadeIn() togetherWith
                    slideOutHorizontally { it } + fadeOut()
        },
        predictivePopTransitionSpec = {
            slideInHorizontally { -it } + fadeIn() togetherWith
                    slideOutHorizontally { it } + fadeOut()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<StashRoutes.HomeScreen>(
                metadata = homePane()
            ) {
                HomeStashScreen(
                    onItemClick = { item ->
                        navBackStack.add(StashRoutes.DockerScreen(item))
                    },
                    stopSync = stopSync,
                ) {
                    navBackStack.clear()
                    navBackStack.add(StashRoutes.AuthenticationFormScreen)
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
                    startSync = startSync,
                    goToHomeScreen = {
                        navBackStack.clear()
                        navBackStack.add(StashRoutes.HomeScreen)
                    },
                    onGoToOtpVerificationScreen = { email, origin, userId ->
                        navBackStack.add(
                            StashRoutes.OTPVerificationScreen(
                                email,
                                origin,
                                userId
                            )
                        )
                    }
                )
            }

            entry<StashRoutes.ForgotPasswordScreen> {
                ForgotPasswordScreen(
                    onGoToOtpVerificationScreen = { email, origin, userId ->
                        navBackStack.add(
                            StashRoutes.OTPVerificationScreen(
                                email,
                                origin,
                                userId
                            )
                        )
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
                    },
                    startSync = startSync
                )
            }
        }
    )
}