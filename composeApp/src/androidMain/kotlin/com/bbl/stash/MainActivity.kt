package com.bbl.stash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.bbl.stash.common.ObserveAsEvents
import com.bbl.stash.common.StartActivityForResultEventController
import com.bbl.stash.common.StartActivityIntentType
import com.bbl.stash.common.auth.GoogleAuthentication
import com.bbl.stash.sync.DataSyncWorker.Companion.initWorker
import com.bbl.stash.sync.DataSyncWorker.Companion.stopWorker
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    lateinit var googleAuthResultLauncher: ActivityResultLauncher<Intent>
    val googleAuthentication: GoogleAuthentication by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        googleAuthResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            googleAuthentication.handleGoogleAuthResult(result)
        }
        setContent {
            App(
                startSync = {
                    initWorker(applicationContext)
                },
                stopSync = {
                    stopWorker(applicationContext)
                }
            )

            ObserveAsEvents(
                flow = StartActivityForResultEventController.eventChannel
            ) {
                when(it.type) {
                    StartActivityIntentType.GOOGLE_AUTH -> {
                        googleAuthResultLauncher.launch(googleAuthentication.provideGoogleSignInClient(this).signInIntent)
                    }

                    StartActivityIntentType.AUTH_CANCELLED -> {}
                }
            }
        }
    }
}