package com.example.stash.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stash.auth.entity.StashApplicationException
import com.example.stash.auth.entity.UserRegisterFormState
import com.example.stash.auth.entity.UserRegisteredId
import com.example.stash.auth.entity.UserRegistrationBody
import com.example.stash.auth.usecases.UserAuthUseCase
import com.example.stash.common.Constants
import com.example.stash.common.RequestStatus
import com.example.stash.common.SafeIOUtil
import com.example.stash.common.StringUtils.isStrongPassword
import com.example.stash.common.StringUtils.isValidEmail
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UseRegistrationViewModel(
    private val userAuthUseCase: UserAuthUseCase
): ViewModel() {
    private val _state: MutableStateFlow<UserRegisterFormState> = MutableStateFlow(UserRegisterFormState())
    val state: StateFlow<UserRegisterFormState> = _state.asStateFlow()

    private val _userRegisterRequestStatus: MutableSharedFlow<RequestStatus<UserRegisteredId>> = MutableSharedFlow()
    val userRegisterRequestStatus: SharedFlow<RequestStatus<UserRegisteredId>> = _userRegisterRequestStatus.asSharedFlow()

    fun processEvent(viewEvent: UserAuthIntent.RegisterAuth.ViewEvent) {
        when (viewEvent) {
            is UserAuthIntent.RegisterAuth.ViewEvent.UpdateEmail -> updateEmail(viewEvent.email)
            is UserAuthIntent.RegisterAuth.ViewEvent.UpdatePassword -> updatePassword(viewEvent.password)
            is UserAuthIntent.RegisterAuth.ViewEvent.UpdateUsername -> updateUserName(viewEvent.username)
            UserAuthIntent.RegisterAuth.ViewEvent.RegisterUser -> registerUser()
        }
    }

    private fun registerUser() {
       viewModelScope.launch {
           _userRegisterRequestStatus.emit(RequestStatus.Loading)
           val registerUserResult = SafeIOUtil.safeCall {
               userAuthUseCase.registerUser(getRegisterUserBody())
           }
           registerUserResult.onSuccess { result ->
                result?.let {
                    _userRegisterRequestStatus.emit(RequestStatus.Success(result))
                } ?: _userRegisterRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
           }
           registerUserResult.onFailure { exception ->
               when (exception) {
                   is StashApplicationException -> {
                       _userRegisterRequestStatus.emit(RequestStatus.Error(exception.message))
                   }
                   is Exception -> {
                       _userRegisterRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
                   }
               }
           }
       }
    }

    private fun getRegisterUserBody(): UserRegistrationBody {
        return UserRegistrationBody(
            email = state.value.email,
            password = state.value.password,
            name = state.value.userName
        )
    }

    private fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                emailValid = email.isValidEmail()
            )
        }
    }

    private fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordValid = password.isStrongPassword()
            )
        }
    }

    private fun updateUserName(userName: String) {
        _state.update {
            it.copy(
                userName = userName,
                userNameValid = userName.isNotEmpty()
            )
        }
    }
}