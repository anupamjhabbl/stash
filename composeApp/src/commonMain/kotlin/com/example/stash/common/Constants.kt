package com.example.stash.common

object Constants {
    const val TASK_TIMER_MINUTES = 15L
    const val TASK_IDENTIFIER = "periodic_db_sync_worker"

    const val DEFAULT_ERROR = "Default Error"

    object HTTPHeaders {
        const val AUTHORIZATION_BEARER = "Bearer"
        const val AUTHORIZATION = "Authorization"
    }

    object Origin {
        const val REGISTRATION = "registration"
        const val FORGOT_PASSWORD = "forgot_password"
    }
}

expect object PlatformConstants {
    fun getBaseUrl(): String
}