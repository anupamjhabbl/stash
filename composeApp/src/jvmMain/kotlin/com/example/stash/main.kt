package com.example.stash

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.stash.di.commonModule
import com.example.stash.di.databaseModule
import com.example.stash.di.viewModelModule

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main() {

    startKoin {
        modules(
            databaseModule,
            commonModule,
            viewModelModule
        )
    }
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
