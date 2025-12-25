package com.example.stash.common.infra

class AppleSecureStorage(
    val securePreferences: SecurePreferenceManager
): SecureStorage {
    override fun saveToken(key: String, value: String) {
        securePreferences.putString(key, value)
    }

    override fun getToken(key: String): String? {
        return securePreferences.getString(key)
    }

    override fun remove(key: String) {
        securePreferences.remove(key)
    }

    override fun clear() {
        securePreferences.clearAll()
    }
}