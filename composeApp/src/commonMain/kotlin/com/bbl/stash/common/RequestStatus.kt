package com.bbl.stash.common

sealed class RequestStatus<out T> {
    data object Idle : RequestStatus<Nothing>()
    data object Loading : RequestStatus<Nothing>()
    data class Success<T>(val data: T) : RequestStatus<T>()
    data class Error(val message: String?) : RequestStatus<Nothing>()
}