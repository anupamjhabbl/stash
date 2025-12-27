package com.bbl.stash.common

actual object PlatformConstants {
    actual fun getBaseUrl(): String {
//        return "http://192.168.31.154:3000/"
        return "http://10.0.2.2:3000/"
    }
}