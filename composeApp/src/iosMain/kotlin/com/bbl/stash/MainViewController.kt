package com.bbl.stash

import androidx.compose.ui.window.ComposeUIViewController
import com.bbl.stash.di.KoinInitializer

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