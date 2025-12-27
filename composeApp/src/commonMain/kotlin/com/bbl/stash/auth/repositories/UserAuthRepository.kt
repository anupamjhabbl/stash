package com.bbl.stash.auth.repositories

import com.bbl.stash.auth.entity.AuthToken
import com.bbl.stash.auth.entity.BaseResponse
import com.bbl.stash.auth.entity.PasswordResetResponse
import com.bbl.stash.auth.entity.UserForgetPasswordBody
import com.bbl.stash.auth.entity.UserLoginBody
import com.bbl.stash.auth.entity.UserOTPVerifyBody
import com.bbl.stash.auth.entity.UserPasswordResetBody
import com.bbl.stash.auth.entity.UserRegisteredId
import com.bbl.stash.auth.entity.UserRegistrationBody

interface UserAuthRepository {
    suspend fun registerUser(userRegistrationBody: UserRegistrationBody): BaseResponse<UserRegisteredId>
    suspend fun loginUser(userLoginBody: UserLoginBody): BaseResponse<AuthToken>
    suspend fun verifyOTP(userOTPVerifyBody: UserOTPVerifyBody, origin: String): BaseResponse<AuthToken>
    suspend fun forgetPasswordRequestOTP(userForgetPasswordBody: UserForgetPasswordBody): BaseResponse<UserRegisteredId>
    suspend fun resetPassword(userResetBody: UserPasswordResetBody, accessToken: String): BaseResponse<PasswordResetResponse>
    suspend fun getNewAccessToken(refreshToken: String): BaseResponse<AuthToken>
}