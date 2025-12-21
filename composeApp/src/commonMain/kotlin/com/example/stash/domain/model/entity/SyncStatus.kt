package com.example.stash.domain.model.entity

enum class SyncStatus(val status: String) {
    COMPLETED("completed"),
    PENDING("pending");

    companion object {
        fun getSyncStatus(status: String): SyncStatus {
            return entries.firstOrNull { it.status == status } ?: PENDING
        }
    }
}