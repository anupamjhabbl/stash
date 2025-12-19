package com.example.stash

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.stash.di.KoinInitializer
import org.koin.core.context.stopKoin

fun main() {
    KoinInitializer().init()
    application {
        Window(
            onCloseRequest = {
                stopKoin()
                exitApplication()
            },
            title = "Stash"
        ) {
            App()
        }
    }
}
