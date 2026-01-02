package com.bbl.stash.common.controllers

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarEvent(
    val message: String,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val action: SnackbarAction = SnackbarAction()
)

data class SnackbarAction(
    val actionLabel: String? = null,
    val action: () -> Unit = {}
)

object SnackbarController {
    private val _eventChannel: Channel<SnackbarEvent> = Channel()
    val eventChannel = _eventChannel.receiveAsFlow()


    fun sendEvent(event: SnackbarEvent) {
        _eventChannel.trySend(event)
    }
}