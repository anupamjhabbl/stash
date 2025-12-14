package com.example.stash

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform