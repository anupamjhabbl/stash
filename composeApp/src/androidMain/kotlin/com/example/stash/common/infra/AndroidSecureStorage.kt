package com.example.stash.common.infra

class AndroidSecureStorage(
    val encryptedPreferenceManager: EncryptedPreferenceManager
): SecureStorage {
    override fun saveToken(key: String, value: String) {
        encryptedPreferenceManager.saveToken(key, value)
    }

    override fun getToken(key: String): String? {
        return encryptedPreferenceManager.getToken(key)
    }

    override fun remove(key: String) {
        encryptedPreferenceManager.remove(key)
    }

    override fun clear() {
        encryptedPreferenceManager.clear()
    }

}