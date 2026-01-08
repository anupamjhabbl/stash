package com.bbl.stash.common

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

class AndroidDeviceIdProvider(
    val context: Context
): DeviceIdProvider {
    @SuppressLint("HardwareIds")
    override fun get(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown_device"
    }
}