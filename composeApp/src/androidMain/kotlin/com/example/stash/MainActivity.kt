package com.example.stash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stash.sync.DataSyncWorker.Companion.initWorker
import com.example.stash.sync.DataSyncWorker.Companion.stopWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App(
                startSync = {
                    initWorker(applicationContext)
                },
                stopSync = {
                    stopWorker(applicationContext)
                }
            )
        }
    }
}