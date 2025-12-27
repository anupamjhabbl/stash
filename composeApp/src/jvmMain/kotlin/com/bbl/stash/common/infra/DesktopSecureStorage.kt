package com.bbl.stash.common.infra

class DesktopSecureStorage(
    val desktopPreferenceManager: DesktopPreferenceManager
): SecureStorage {
    override fun saveToken(key: String, value: String) {
        desktopPreferenceManager.putString(key, value)
    }

    override fun getToken(key: String): String? {
        return desktopPreferenceManager.getString(key)
    }

    override fun remove(key: String) {
        desktopPreferenceManager.remove(key)
    }

    override fun clear() {
        desktopPreferenceManager.clearAll()
    }
}