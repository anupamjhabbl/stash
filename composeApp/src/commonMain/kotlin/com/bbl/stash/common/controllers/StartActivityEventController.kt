package com.bbl.stash.common.controllers

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object StartActivityForResultEventController {
    private val _eventChannel: Channel<StartActivityForResultEvent> = Channel()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun sendEvent(event: StartActivityForResultEvent) {
        _eventChannel.trySend(event)
    }

    private val _resultChannel: Channel<StartActivityForResultEventResult> = Channel()
    val resultChannel = _resultChannel.receiveAsFlow()

    fun sendResult(result: StartActivityForResultEventResult) {
        _resultChannel.trySend(result)
    }
}

data class StartActivityForResultEvent(
    val type: StartActivityIntentType
)

data class StartActivityForResultEventResult(
    val type: StartActivityIntentType,
    val result: Any
)

enum class StartActivityIntentType {
    GOOGLE_AUTH,
    AUTH_CANCELLED
}