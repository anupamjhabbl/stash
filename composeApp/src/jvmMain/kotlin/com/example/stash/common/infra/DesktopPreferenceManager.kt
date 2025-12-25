package com.example.stash.common.infra

import kotlinx.serialization.json.Json
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class DesktopPreferenceManager {
    private val prefsFile = File(System.getProperty("user.home"), ".secure_prefs.json")
    private val key: SecretKey = loadOrGenerateKey()
    private val json = Json { encodeDefaults = true }

    private var cache: MutableMap<String, String> = mutableMapOf()

    init {
        if (prefsFile.exists()) {
            try {
                val encryptedData = prefsFile.readBytes()
                val decryptedJson = decrypt(encryptedData, key)
                cache = json.decodeFromString<Map<String, String>>(decryptedJson).toMutableMap()
            } catch (_: Exception) {}
        }
    }

    fun putString(key: String, value: String) {
        cache[key] = value
        save()
    }

    fun getString(key: String): String? = cache[key]

    fun remove(key: String) {
        cache.remove(key)
        save()
    }

    fun clearAll() {
        cache.clear()
        save()
    }

    private fun save() {
        val jsonString = json.encodeToString(cache)
        val encrypted = encrypt(jsonString, key)
        prefsFile.writeBytes(encrypted)
    }

    private fun encrypt(plainText: String, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12).apply { SecureRandom().nextBytes(this) }
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val encrypted = cipher.doFinal(plainText.toByteArray())
        return iv + encrypted
    }

    private fun decrypt(data: ByteArray, secretKey: SecretKey): String {
        val iv = data.copyOfRange(0, 12)
        val encrypted = data.copyOfRange(12, data.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return String(cipher.doFinal(encrypted))
    }

    private fun loadOrGenerateKey(): SecretKey {
        val keyFile = File(System.getProperty("user.home"), ".secure_prefs_key")
        return if (keyFile.exists()) {
            SecretKeySpec(keyFile.readBytes(), "AES")
        } else {
            val gen = KeyGenerator.getInstance("AES")
            gen.init(256)
            val key = gen.generateKey()
            keyFile.writeBytes(key.encoded)
            key
        }
    }
}
