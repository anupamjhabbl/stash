package com.example.stash.common

import platform.Foundation.NSUUID

actual object UUIDUtils {
    actual fun generateUUID(): String {
        return NSUUID().UUIDString()
    }
}