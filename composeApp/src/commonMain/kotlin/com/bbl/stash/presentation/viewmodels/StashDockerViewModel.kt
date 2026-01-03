package com.bbl.stash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.common.SafeIOUtil
import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.domain.usecase.StashDataUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StashDockerViewModel(
    private val stashDataUseCase: StashDataUseCase,
    private val authPreferencesUseCase: AuthPreferencesUseCase
): ViewModel() {
    private var stashCategoryId: String? = null
    private val _stashScreenState: MutableStateFlow<StashDockerScreenState> = MutableStateFlow(StashDockerScreenState())
    val stashScreenState: StateFlow<StashDockerScreenState> = _stashScreenState.asStateFlow()

    private val _stashDockerScreenEffect: MutableSharedFlow<StashDockerScreenEffect> = MutableSharedFlow()
    val stashDockerScreenEffect: SharedFlow<StashDockerScreenEffect> = _stashDockerScreenEffect.asSharedFlow()

    fun init(stashCategoryId: String) {
        this.stashCategoryId = stashCategoryId
        getStashData(stashCategoryId)
    }

    private fun getStashData(stashCategoryId: String) {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        viewModelScope.launch {
            _stashScreenState.update {
                it.copy(isLoading = true)
            }
            val stashItemList = SafeIOUtil.safeCall {
                stashDataUseCase.getCategoryDataWithItemsForId(stashCategoryId, loggedUserId)
            }
            stashItemList.onSuccess { itemList ->
                itemList.collect { item ->
                    _stashScreenState.update {
                        it.copy(isLoading = false, stashItemList = item)
                    }
                }
            }
            stashItemList.onFailure {
                _stashScreenState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun addStashItem(stashItemId: String?, stashItemName: String, stashItemUrl: String, stashItemRating: Float, stashItemCompletedStatus: String) {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        stashCategoryId?.let { stashCategoryId ->
            viewModelScope.launch {
                _stashScreenState.update {
                    it.copy(isLoading = true)
                }
                SafeIOUtil.safeCall {
                    stashDataUseCase.addStashItem(
                        loggedUserId,
                        stashItemId,
                        stashCategoryId,
                        stashItemName,
                        stashItemUrl,
                        stashItemRating,
                        stashItemCompletedStatus
                    )
                }
                _stashScreenState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun deleteStashItem(itemId: String) {
        viewModelScope.launch {
            val result = SafeIOUtil.safeCall {
                stashDataUseCase.deleteStashItem(itemId)
            }
            result.onFailure {
                _stashDockerScreenEffect.emit(StashDockerScreenEffect.DeleteFailure)
            }
        }
    }
}

data class StashDockerScreenState(
    val isLoading: Boolean = true,
    val stashItemList: StashCategoryWithItem? = null
)

sealed interface StashDockerScreenEffect {
    data object DeleteFailure: StashDockerScreenEffect
}

