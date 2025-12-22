package com.example.stash.common

import java.util.UUID

actual object UUIDUtils {
    actual fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}