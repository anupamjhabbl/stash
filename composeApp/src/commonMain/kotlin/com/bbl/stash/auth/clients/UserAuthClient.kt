package com.bbl.stash.auth.clients

import com.bbl.stash.auth.entity.AuthToken
import com.bbl.stash.auth.entity.BaseResponse
import com.bbl.stash.auth.entity.PasswordResetResponse
import com.bbl.stash.auth.entity.UserForgetPasswordBody
import com.bbl.stash.auth.entity.UserLoginBody
import com.bbl.stash.auth.entity.UserOTPVerifyBody
import com.bbl.stash.auth.entity.UserPasswordResetBody
import com.bbl.stash.auth.entity.UserRegisteredId
import com.bbl.stash.auth.entity.UserRegistrationBody
import com.bbl.stash.common.Constants
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface UserAuthClient {
    @POST("api/auth/register")
    suspend fun registerUser(@Body userRegistrationBody: UserRegistrationBody): BaseResponse<UserRegisteredId>

    @POST("api/auth/login")
    suspend fun loginUser(@Body userLoginBody: UserLoginBody): BaseResponse<AuthToken>

    @POST("api/auth/verifyOtp")
    suspend fun verifyOTP(@Body userOTPVerifyBody: UserOTPVerifyBody, @Query("origin") origin: String): BaseResponse<AuthToken>

    @POST("api/auth/forgotPassword")
    suspend fun forgetPasswordRequestOTP(@Body userForgetPasswordBody: UserForgetPasswordBody): BaseResponse<UserRegisteredId>

    @POST("api/auth/resetPassword")
    suspend fun resetPassword(@Body userResetBody: UserPasswordResetBody, @Header(Constants.HTTPHeaders.AUTHORIZATION) accessToken: String): BaseResponse<PasswordResetResponse>

    @POST("api/auth/refresh")
    suspend fun getNewAccessToken(@Header(Constants.HTTPHeaders.AUTHORIZATION) refreshToken: String): BaseResponse<AuthToken>
}