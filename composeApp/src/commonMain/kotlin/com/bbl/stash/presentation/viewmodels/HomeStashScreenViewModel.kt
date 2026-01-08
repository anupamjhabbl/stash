package com.bbl.stash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bbl.stash.auth.entity.User
import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.auth.usecases.ProfileUseCase
import com.bbl.stash.common.SafeIOUtil
import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.domain.usecase.StashDataUseCase
import com.bbl.stash.sync.StashSyncManager
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeStashScreenViewModel(
    private val stashDataUseCase: StashDataUseCase,
    private val profileUseCase: ProfileUseCase,
    private val authPreferencesUseCase: AuthPreferencesUseCase,
    private val stashSyncManager: StashSyncManager
): ViewModel() {
    private val _stashScreenState: MutableStateFlow<HomeStashScreenState> = MutableStateFlow(HomeStashScreenState())
    val stashScreenState: StateFlow<HomeStashScreenState> = _stashScreenState.asStateFlow()

    private val _homeStashScreenEffect: MutableSharedFlow<HomeStashScreenEffect> = MutableSharedFlow()
    val homeStashScreenEffect: SharedFlow<HomeStashScreenEffect> = _homeStashScreenEffect.asSharedFlow()

    init {
        getStashData()
    }

    private fun getStashData() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        viewModelScope.launch {
            _stashScreenState.update {
                it.copy(isLoading = true)
            }
            val stashCategoryWithItemResult = SafeIOUtil.safeCall {
                stashDataUseCase.getCategoryDataWithItems(loggedUserId)
            }
            stashCategoryWithItemResult.onSuccess { stashCategoryWithResult ->
                stashCategoryWithResult.collect { list ->
                    _stashScreenState.update {
                        it.copy(
                            isLoading = false,
                            stashCategoryList = list
                        )
                    }
                }
            }
            stashCategoryWithItemResult.onFailure {
                _stashScreenState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun addCategoryItem(categoryId: String?, categoryName: String) {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        viewModelScope.launch {
            _stashScreenState.update { it.copy(isLoading = true) }
            SafeIOUtil.safeCall {
                stashDataUseCase.addStashCategory(categoryId, categoryName, loggedUserId)
            }
            _stashScreenState.update { it.copy(isLoading = false) }
        }
    }

    fun getLoggedUser(): User? {
        return authPreferencesUseCase.getLoggedUser()
    }

    fun logOutUser() {
        viewModelScope.launch {
            _stashScreenState.update { it.copy(isLoading = true) }
            val dataSyncResult = async {
                SafeIOUtil.safeCall {
                    stashSyncManager.syncData()
                }
            }
            val resultDataSync = dataSyncResult.await()
            val logOutResult = async {
                SafeIOUtil.safeCall {
                    profileUseCase.logoutUser()
                }
            }
            val resultLogOut = logOutResult.await()
            if (resultLogOut.isSuccess && resultDataSync.isSuccess) {
                authPreferencesUseCase.removeUserData()
                _homeStashScreenEffect.emit(HomeStashScreenEffect.LogOutUser)
            } else {
                _homeStashScreenEffect.emit(HomeStashScreenEffect.LogOutFailure)
            }
            _stashScreenState.update { it.copy(isLoading = false) }
        }
    }

    fun deleteCategory(categoryId: String) {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        viewModelScope.launch {
            val result = SafeIOUtil.safeCall {
                stashDataUseCase.deleteStashCategory(categoryId, loggedUserId)
            }
            result.onFailure {
                _homeStashScreenEffect.emit(HomeStashScreenEffect.DeleteFailure)
            }
        }
    }
}

data class HomeStashScreenState(
    val isLoading: Boolean = true,
    val stashCategoryList: List<StashCategoryWithItem> = emptyList()
)

sealed interface HomeStashScreenEffect {
    data object LogOutUser: HomeStashScreenEffect
    data object LogOutFailure: HomeStashScreenEffect
    data object DeleteFailure: HomeStashScreenEffect
}