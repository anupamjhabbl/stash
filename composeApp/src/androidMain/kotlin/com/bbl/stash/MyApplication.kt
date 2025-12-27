package com.bbl.stash

import android.app.Application
import com.bbl.stash.di.KoinInitializer

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}