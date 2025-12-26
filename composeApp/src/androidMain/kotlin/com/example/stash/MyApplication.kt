package com.example.stash

import android.app.Application
import com.example.stash.di.KoinInitializer

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}