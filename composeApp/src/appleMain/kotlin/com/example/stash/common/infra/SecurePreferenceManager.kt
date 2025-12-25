package com.example.stash.common.infra

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.Security.*

class SecurePreferenceManager : SecurePreferences {
    private fun baseQuery(key: String): NSDictionary {
        return mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to key,
            kSecAttrService to "com.yourapp.secureprefs"
        ) as NSDictionary
    }

    override fun putString(key: String, value: String) {
        val data = value.encodeToByteArray().toNSData()

        val query = baseQuery(key).mutableCopy() as NSMutableDictionary
        query[kSecValueData] = data

        SecItemDelete(query)

        val status = SecItemAdd(query, null)
        check(status == errSecSuccess) {
            "Keychain save failed with code $status"
        }
    }

    override fun getString(key: String): String? {
        val query = baseQuery(key).mutableCopy() as NSMutableDictionary
        query[kSecReturnData] = kCFBooleanTrue
        query[kSecMatchLimit] = kSecMatchLimitOne

        memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query, result.ptr)

            if (status != errSecSuccess) return null

            val data = result.value as NSData
            return data.toByteArray().decodeToString()
        }
    }

    override fun remove(key: String) {
        SecItemDelete(baseQuery(key))
    }

    private fun baseQueryAll(): NSDictionary {
        return mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to service
        ) as NSDictionary
    }

    override fun clearAll() {
        SecItemDelete(baseQueryAll())
    }
}

fun ByteArray.toNSData(): NSData =
    NSData.create(bytes = this, length = size.toULong())

fun NSData.toByteArray(): ByteArray {
    val byteArray = ByteArray(length.toInt())
    memScoped {
        memcpy(byteArray.refTo(0), bytes, length)
    }
    return byteArray
}
