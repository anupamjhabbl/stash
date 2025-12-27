package com.bbl.stash.common

actual object PlatformConstants {
    actual fun getBaseUrl(): String {
        return "http://127.0.0.1:3000/"
    }
}