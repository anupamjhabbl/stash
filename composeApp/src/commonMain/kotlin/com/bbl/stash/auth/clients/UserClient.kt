package com.bbl.stash.auth.clients

import com.bbl.stash.auth.entity.BaseResponse
import com.bbl.stash.auth.entity.User
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface UserClient {
    @GET("api/user/{userId}")
    suspend fun getUser(@Path("userId") userId: String): BaseResponse<User>

    @GET("api/user/me")
    suspend fun getLocalUser(): BaseResponse<User>

    @POST("api/user/logout")
    suspend fun logoutUser(): BaseResponse<String>
}