package com.example.stash.common.infra

import com.example.stash.auth.entity.User
import kotlinx.serialization.json.Json
import com.russhwolf.settings.Settings

class PreferenceManager {
    companion object {
        const val LOCAL_USER_DATA = "local_user_data"
    }

    private val settings: Settings = Settings()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun saveLocalUser(user: User) {
        settings.putString(
            LOCAL_USER_DATA,
            json.encodeToString(user)
        )
    }

    fun removeLocalUser() {
        settings.remove(LOCAL_USER_DATA)
    }

    fun clear() {
        settings.clear()
    }

    fun getLocalUser(): User? {
        val userJson = settings.getStringOrNull(LOCAL_USER_DATA)
        return userJson?.let {
            runCatching {
                json.decodeFromString<User>(it)
            }.getOrNull()
        }
    }
}