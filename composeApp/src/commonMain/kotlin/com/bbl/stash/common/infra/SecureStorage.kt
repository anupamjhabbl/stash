package com.bbl.stash.common.infra

interface SecureStorage {
    fun saveToken(key: String, value: String)
    fun getToken(key: String): String?
    fun remove(key: String)
    fun clear()
}