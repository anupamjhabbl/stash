package com.example.stash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stash.auth.usecases.AuthPreferencesUseCase
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val authPreferencesUseCase: AuthPreferencesUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App(authPreferencesUseCase.isUserLogged())
        }
    }
}