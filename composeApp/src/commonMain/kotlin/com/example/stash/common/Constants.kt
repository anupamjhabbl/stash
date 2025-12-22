package com.example.stash.common

object Constants {
    const val TASK_TIMER_MINUTES = 15L
    const val TASK_IDENTIFIER = "periodic_db_sync_worker"
}

expect object PlatformConstants {
    fun getBaseUrl(): String
}