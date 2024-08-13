package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.await

class FavoriteViewModel : ViewModel() {

    private val _isFavoriteMap = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val isFavoriteMap: StateFlow<Map<Long, Boolean>> = _isFavoriteMap

    private val _loadingState = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val loadingState: StateFlow<Map<Long, Boolean>> = _loadingState

    private val _checkedFavoriteStatus = MutableStateFlow<Set<Long>>(emptySet())

    private var isActionInProgress = false


    fun checkFavorite(userId: Long, recipeId: Long) {
        if (_checkedFavoriteStatus.value.contains(recipeId)) return

        isActionInProgress = true
        viewModelScope.launch {
            _loadingState.value =
                _loadingState.value.toMutableMap().apply { put(recipeId, true) }
            try {
                // Directly call the suspend function
                val response = RetrofitHelper.apiService.checkFavorite(userId, recipeId)
                _isFavoriteMap.value =
                    _isFavoriteMap.value.toMutableMap().apply { put(recipeId, response) }
                _checkedFavoriteStatus.value = _checkedFavoriteStatus.value + recipeId

            } catch (e: Exception) {
                e.printStackTrace()
                _isFavoriteMap.value =
                    _isFavoriteMap.value.toMutableMap().apply { put(recipeId, false) }
            } finally {
                _loadingState.value =
                    _loadingState.value.toMutableMap().apply { put(recipeId, false) }
                isActionInProgress = false
            }
        }
    }

    fun toggleFavorite(userId: Long, recipeId: Long) {
        if (!isActionInProgress) {
            isActionInProgress = true

            viewModelScope.launch {
                try {
                    val currentFavoriteState = _isFavoriteMap.value[recipeId] ?: false
                    val newFavoriteState = !currentFavoriteState
                    _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap()
                        .apply { put(recipeId, newFavoriteState) }

                    if (newFavoriteState) {
                        // Add to favorites
                        val response = RetrofitHelper.apiService.addFavorite(userId, recipeId)
                        if (!response) {
                            _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap()
                                .apply { put(recipeId, false) }
                        }
                    } else {
                        // Remove from favorites
                        val response = RetrofitHelper.apiService.deleteFavorite(userId, recipeId)
                        if (!response) {
                            _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap()
                                .apply { put(recipeId, true) }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap()
                        .apply { put(recipeId, _isFavoriteMap.value[recipeId] ?: false) }
                } finally {
                    isActionInProgress = false
                }
            }
        }
    }


    val isActionInProgressFlow = MutableStateFlow(isActionInProgress)

}
