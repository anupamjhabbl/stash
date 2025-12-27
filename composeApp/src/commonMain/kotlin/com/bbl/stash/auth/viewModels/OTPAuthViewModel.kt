package com.bbl.stash.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bbl.stash.auth.entity.AuthToken
import com.bbl.stash.auth.entity.OTPAction
import com.bbl.stash.auth.entity.OTPState
import com.bbl.stash.auth.entity.StashApplicationException
import com.bbl.stash.auth.entity.UserForgetPasswordBody
import com.bbl.stash.auth.entity.UserOTPVerifyBody
import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.auth.usecases.ProfileUseCase
import com.bbl.stash.auth.usecases.UserAuthUseCase
import com.bbl.stash.common.Constants
import com.bbl.stash.common.RequestStatus
import com.bbl.stash.common.SafeIOUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OTPAuthViewModel(
    private val userAuthUseCase: UserAuthUseCase,
    private val authPreferencesUseCase: AuthPreferencesUseCase,
    private val profileUseCase: ProfileUseCase
): ViewModel() {
    private val _otpState: MutableStateFlow<OTPState> = MutableStateFlow(OTPState(OTP_SIZE))
    val otpState: StateFlow<OTPState> = _otpState.asStateFlow()

    private val _userOTPVerifyRequestStatus: MutableSharedFlow<RequestStatus<String>> = MutableSharedFlow()
    val userOTPVerifyRequestStatus: SharedFlow<RequestStatus<String>> = _userOTPVerifyRequestStatus.asSharedFlow()

    private val _userOTPResendRequestStatus: MutableSharedFlow<RequestStatus<String>> = MutableSharedFlow()
    val userOTPResendRequestStatus: SharedFlow<RequestStatus<String>> = _userOTPResendRequestStatus.asSharedFlow()

    private lateinit var userEmail: String
    private lateinit var userId: String
    private lateinit var origin: String

    fun processEvent(viewEvent: UserAuthIntent.OTPAuth.ViewEvent) {
        when (viewEvent) {
            is UserAuthIntent.OTPAuth.ViewEvent.OnAction -> onAction(viewEvent.action)
            is UserAuthIntent.OTPAuth.ViewEvent.SetData -> setEmail(viewEvent.email, viewEvent.userId, viewEvent.origin)
            UserAuthIntent.OTPAuth.ViewEvent.ResendOTP -> resendOTP()
            UserAuthIntent.OTPAuth.ViewEvent.VerifyOTP -> verifyOTP()
        }
    }

    private fun setEmail(email: String, id: String, origin: String) {
        userEmail = email
        userId = id
        this.origin = origin
    }

    fun getOTPLength() = OTP_SIZE

    private fun verifyOTP() {
        viewModelScope.launch {
            _userOTPVerifyRequestStatus.emit(RequestStatus.Loading)
            val registerUserResult = SafeIOUtil.safeCall {
                userAuthUseCase.verifyOTP(getOTPVerifyBody(), origin)
            }
            registerUserResult.onSuccess { result ->
                result?.let {
                    saveTheToken(result)
                    fetchLocalUserData()
                } ?: _userOTPVerifyRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
            }
            registerUserResult.onFailure { exception ->
                when (exception) {
                    is StashApplicationException -> {
                        _userOTPVerifyRequestStatus.emit(RequestStatus.Error(exception.message))
                    }
                    is Exception -> {
                        _userOTPVerifyRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
                    }
                }
            }
        }
    }

    private suspend fun fetchLocalUserData() {
        val getUserDataRequest = SafeIOUtil.safeCall {
            profileUseCase.getLocalUser()
        }
        getUserDataRequest.onSuccess { user ->
            if (user == null) {
                removeToken()
                _userOTPVerifyRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
            } else {
                authPreferencesUseCase.saveLoggedUser(user)
                _userOTPVerifyRequestStatus.emit(RequestStatus.Success(""))
            }
        }
        getUserDataRequest.onFailure { exception ->
            removeToken()
            when (exception) {
                is StashApplicationException -> {
                    _userOTPVerifyRequestStatus.emit(RequestStatus.Error(exception.message))
                }
                is Exception -> {
                    _userOTPVerifyRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
                }
            }
        }
    }

    private fun removeToken() {
        authPreferencesUseCase.removeAccessToken()
        authPreferencesUseCase.removeRefreshToken()
    }

    private fun saveTheToken(authToken: AuthToken) {
        authPreferencesUseCase.saveAccessToken(authToken.accessToken)
        authPreferencesUseCase.saveRefreshToken(authToken.refreshToken)
    }

    private fun getOTPVerifyBody(): UserOTPVerifyBody {
        val otp = stringifyOtp(otpState.value.code)
        return UserOTPVerifyBody(
            userId = userId,
            otp = otp
        )
    }

    private fun resendOTP() {
        viewModelScope.launch {
            _userOTPResendRequestStatus.emit(RequestStatus.Loading)
            val registerUserResult = SafeIOUtil.safeCall {
                userAuthUseCase.forgetPasswordRequestOTP(getForgetPasswordRequestBody())
            }
            registerUserResult.onSuccess { response ->
                response?.let {
                    _userOTPResendRequestStatus.emit(RequestStatus.Success(response.userId))
                } ?: _userOTPResendRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
            }
            registerUserResult.onFailure { exception ->
                when (exception) {
                    is StashApplicationException -> {
                        _userOTPResendRequestStatus.emit(RequestStatus.Error(exception.message))
                    }
                    is Exception -> {
                        _userOTPResendRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
                    }
                }
            }
        }
    }

    private fun getForgetPasswordRequestBody(): UserForgetPasswordBody {
        return UserForgetPasswordBody(email = userEmail)
    }

    private fun onAction(action: OTPAction) {
        when(action) {
            is OTPAction.OnChangeFieldFocused -> {
                _otpState.update { it.copy(
                    focusedIndex = action.index
                ) }
            }
            is OTPAction.OnEnterNumber -> {
                enterNumber(action.number, action.index)
            }
            OTPAction.OnKeyboardBack -> {
                val previousIndex = getPreviousFocusedIndex(otpState.value.focusedIndex)
                _otpState.update { it.copy(
                    code = it.code.mapIndexed { index, number ->
                        if(index == previousIndex) {
                            null
                        } else {
                            number
                        }
                    },
                    focusedIndex = previousIndex
                ) }
            }
        }
    }

    private fun enterNumber(number: Int?, index: Int) {
        val newCode = otpState.value.code.mapIndexed { currentIndex, currentNumber ->
            if(currentIndex == index) {
                number
            } else {
                currentNumber
            }
        }
        val wasNumberRemoved = number == null
        _otpState.update { it.copy(
            code = newCode,
            focusedIndex = if(wasNumberRemoved || it.code.getOrNull(index) != null) {
                it.focusedIndex
            } else {
                getNextFocusedTextFieldIndex(
                    currentCode = it.code,
                    currentFocusedIndex = it.focusedIndex
                )
            },
            isValid = if (newCode.none { it == null }) {
                newCode.joinToString("").length == OTP_SIZE
            } else false
        ) }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun getNextFocusedTextFieldIndex(
        currentCode: List<Int?>,
        currentFocusedIndex: Int?
    ): Int? {
        if(currentFocusedIndex == null) {
            return null
        }

        if(currentFocusedIndex == OTP_SIZE - 1) {
            return currentFocusedIndex
        }

        return getFirstEmptyFieldIndexAfterFocusedIndex(
            code = currentCode,
            currentFocusedIndex = currentFocusedIndex
        )
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        code: List<Int?>,
        currentFocusedIndex: Int
    ): Int {
        code.forEachIndexed { index, number ->
            if(index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if(number == null) {
                return index
            }
        }
        return currentFocusedIndex
    }


    private fun stringifyOtp(code: List<Int?>): String {
        return code.filterNotNull().joinToString("") { it.toString() }
    }

    companion object {
        private const val OTP_SIZE = 6
    }
}