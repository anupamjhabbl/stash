package com.example.stash

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.stash.presentation.navigation.screen.NavigationRoot
import org.koin.compose.KoinContext

@Composable
fun App(
    isUserLogged: Boolean
) {
    MaterialTheme {
        KoinContext {
            NavigationRoot(isUserLogged)
        }
    }
}

