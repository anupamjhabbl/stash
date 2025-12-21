package com.example.stash

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.stash.di.KoinInitializer
import com.example.stash.sync.JvmSyncScheduler
import org.koin.core.context.stopKoin

fun main() {
    KoinInitializer().init()

    val scheduler: JvmSyncScheduler = org.koin.java.KoinJavaComponent.get(
        JvmSyncScheduler::class.java
    )
    scheduler.start()

    application {
        Window(
            onCloseRequest = {
                scheduler.stop()
                stopKoin()
                exitApplication()
            },
            title = "Stash"
        ) {
            App()
        }
    }
}
