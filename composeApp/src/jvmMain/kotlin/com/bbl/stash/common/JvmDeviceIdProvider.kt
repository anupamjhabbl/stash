package com.bbl.stash.common

class JvmDeviceIdProvider: DeviceIdProvider {
    override fun get(): String {
        return listOf(
            System.getProperty("os.name"),
            System.getProperty("os.arch"),
            System.getProperty("user.name")
        ).joinToString("-").hashCode().toString()
    }
}