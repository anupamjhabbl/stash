package com.example.stash.common

actual object PlatformConstants {
    actual fun getBaseUrl(): String {
        return "http://10.0.2.2:3000/"
    }
}