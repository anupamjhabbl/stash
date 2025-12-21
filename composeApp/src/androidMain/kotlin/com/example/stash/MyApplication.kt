package com.example.stash

import android.app.Application
import com.example.stash.di.KoinInitializer
import com.example.stash.sync.DataSyncWorker.Companion.initWorker

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
        initWorker(applicationContext)
    }
}