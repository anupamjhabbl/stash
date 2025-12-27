package com.bbl.stash.common

actual object UUIDUtils {
    actual fun generateUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }
}