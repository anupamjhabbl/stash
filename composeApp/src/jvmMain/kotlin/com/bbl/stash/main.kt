package com.bbl.stash

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bbl.stash.di.KoinInitializer
import com.bbl.stash.sync.JvmSyncScheduler
import org.koin.core.context.stopKoin

fun main() {
    KoinInitializer().init()

    val scheduler: JvmSyncScheduler = org.koin.java.KoinJavaComponent.get(
        JvmSyncScheduler::class.java
    )

    application {
        Window(
            onCloseRequest = {
                scheduler.stop()
                stopKoin()
                exitApplication()
            },
            title = "Stash"
        ) {
            App(
                startSync = {
                    scheduler.start()
                }
            ) {
                scheduler.stop()
            }
        }
    }
}
