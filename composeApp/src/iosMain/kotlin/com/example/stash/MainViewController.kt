package com.example.stash

import androidx.compose.ui.window.ComposeUIViewController
import com.example.stash.di.KoinInitializer

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
    }
) {
    App(
        startSync = {},
        stopSync = {}
    )
}