package com.bbl.stash.common

import platform.UIKit.UIDevice

class IosDeviceIdProvider: DeviceIdProvider {
    override fun get(): String {
        return UIDevice.currentDevice.identifierForVendor?.UUIDString
            ?: "unknown_device"
    }
}