package com.bbl.stash

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.common.ObserveAsEvents
import com.bbl.stash.common.controllers.SnackbarController
import com.bbl.stash.presentation.navigation.screen.NavigationRoot
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(
    startSync: () -> Unit,
    stopSync: () -> Unit
) {
    val authPreferencesUseCase: AuthPreferencesUseCase = koinInject()
    val isUserLogged = authPreferencesUseCase.isUserLogged()
    MaterialTheme {
        KoinContext {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            ObserveAsEvents(
                flow = SnackbarController.eventChannel,
                snackbarHostState
            ) { event ->
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()

                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action.actionLabel,
                        duration = event.duration
                    )

                    if(result == SnackbarResult.ActionPerformed) {
                        event.action.action.invoke()
                    }
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) {
                NavigationRoot(isUserLogged, startSync, stopSync)
            }
        }
    }
}
