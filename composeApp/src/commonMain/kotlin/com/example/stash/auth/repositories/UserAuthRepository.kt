package com.example.stash.auth.repositories

import com.example.stash.auth.entity.AuthToken
import com.example.stash.auth.entity.BaseResponse
import com.example.stash.auth.entity.PasswordResetResponse
import com.example.stash.auth.entity.UserForgetPasswordBody
import com.example.stash.auth.entity.UserLoginBody
import com.example.stash.auth.entity.UserOTPVerifyBody
import com.example.stash.auth.entity.UserPasswordResetBody
import com.example.stash.auth.entity.UserRegisteredId
import com.example.stash.auth.entity.UserRegistrationBody

interface UserAuthRepository {
    suspend fun registerUser(userRegistrationBody: UserRegistrationBody): BaseResponse<UserRegisteredId>
    suspend fun loginUser(userLoginBody: UserLoginBody): BaseResponse<AuthToken>
    suspend fun verifyOTP(userOTPVerifyBody: UserOTPVerifyBody, origin: String): BaseResponse<AuthToken>
    suspend fun forgetPasswordRequestOTP(userForgetPasswordBody: UserForgetPasswordBody): BaseResponse<UserRegisteredId>
    suspend fun resetPassword(userResetBody: UserPasswordResetBody, accessToken: String): BaseResponse<PasswordResetResponse>
    suspend fun getNewAccessToken(refreshToken: String): BaseResponse<AuthToken>
}