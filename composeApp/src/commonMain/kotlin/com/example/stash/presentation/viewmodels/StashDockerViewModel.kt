package com.example.stash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stash.domain.model.dto.StashCategoryWithItem
import com.example.stash.domain.usecase.StashDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StashDockerViewModel(
    private val stashDataUseCase: StashDataUseCase
): ViewModel() {
    private var stashCategoryId: Long? = null
    private val _stashScreenState: MutableStateFlow<StashDockerScreenState> = MutableStateFlow(StashDockerScreenState())
    val stashScreenState: StateFlow<StashDockerScreenState> = _stashScreenState.asStateFlow()

    fun init(stashCategoryId: Long) {
        this.stashCategoryId = stashCategoryId
        viewModelScope.launch {
            stashDataUseCase.getCategoryDataWithItemsForId(stashCategoryId).onStart {
                _stashScreenState.update {
                    it.copy(isLoading = true)
                }
            }.catch {
                _stashScreenState.update {
                    it.copy(isLoading = false)
                }
            }.collect { item ->
                _stashScreenState.update {
                    it.copy(isLoading = false, stashItemList = item)
                }
            }
        }
    }

    fun addStashItem(stashItemId: Long?, stashItemName: String, stashItemUrl: String, stashItemRating: Float, stashItemCompletedStatus: String) {
        stashCategoryId?.let { stashCategoryId ->
            viewModelScope.launch {
                _stashScreenState.update {
                    it.copy(isLoading = true)
                }
                try {
                    stashDataUseCase.addStashItem(
                        stashItemId,
                        stashCategoryId,
                        stashItemName,
                        stashItemUrl,
                        stashItemRating,
                        stashItemCompletedStatus
                    )
                } finally {
                    _stashScreenState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }
}

data class StashDockerScreenState(
    val isLoading: Boolean = false,
    val stashItemList: StashCategoryWithItem? = null
)

