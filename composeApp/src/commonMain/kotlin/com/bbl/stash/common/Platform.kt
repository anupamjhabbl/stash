package com.bbl.stash.common

expect fun getPlatform(): Platform

enum class Platform {
    ANDROID, APPLE, JVM
}