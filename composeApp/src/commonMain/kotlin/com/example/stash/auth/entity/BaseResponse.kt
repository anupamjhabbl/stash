package com.example.stash.auth.entity

import com.example.stash.common.Constants
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val data: T? = null,
    val statusCode: Int,
    val isSuccess: Boolean,
    val message: String? = null
) {
    companion object {
        suspend fun <T> processResponse(apiCall: suspend () -> BaseResponse<T>): T? {
            val response = apiCall()
            if (response.isSuccess) {
                return response.data
            } else {
                throw StashApplicationException(
                    errorCode = response.statusCode,
                    message = response.message ?: Constants.DEFAULT_ERROR
                )
            }
        }
    }
}