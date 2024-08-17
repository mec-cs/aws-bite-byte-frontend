package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.Like
import com.chattingapp.foodrecipeuidemo.entity.LikeCountResponse
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LikeViewModel : ViewModel() {


    private val _loadingState = MutableStateFlow<Map<Long, Boolean>>(emptyMap()) // New loading state
    val loadingState: StateFlow<Map<Long, Boolean>> = _loadingState

    private val _isLikedMap = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val isLikedMap: StateFlow<Map<Long, Boolean>> = _isLikedMap




    private val _checkedLikeStatus = MutableStateFlow<Set<Long>>(emptySet()) // New state to track checked recipes

    fun checkLike(userId: Long, recipeId: Long) {
        // Prevent redundant checks
        if (_checkedLikeStatus.value.contains(recipeId)) return

        viewModelScope.launch {
            _loadingState.value = _loadingState.value.toMutableMap().apply {
                put(recipeId, true)
            }
            try {
                // Directly call the suspend function
                val response = RetrofitHelper.apiService.getLike(recipeId, userId)
                _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                    put(recipeId, response)
                }
                _checkedLikeStatus.value += recipeId // Mark as checked
            } catch (e: Exception) {
                e.printStackTrace()
                _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                    put(recipeId, false)
                }
            } finally {
                _loadingState.value = _loadingState.value.toMutableMap().apply {
                    put(recipeId, false)
                }
            }
        }
    }

    private var isActionInProgress = false

    fun toggleLike(like: Like) {
        if (!isActionInProgress) {
            isActionInProgress = true
            viewModelScope.launch {
                try {
                    val currentLikeState = _isLikedMap.value[like.recipeId] ?: false
                    val newLikeState = !currentLikeState
                    _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                        put(like.recipeId, newLikeState)
                    }

                    if (newLikeState) {
                        try {
                            RetrofitHelper.apiService.addLike(like)
                            updateLikeCount(like.recipeId, 1)
                            Log.d("LikeToggle", "Like added successfully")
                        } catch (e: Exception) {
                            Log.e("LikeToggle", "Error adding like: ${e.message}")
                            _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                                put(like.recipeId, false)
                            }
                        }
                    } else {
                        try {
                            RetrofitHelper.apiService.deleteLike(like.recipeId, like.userId)
                            updateLikeCount(like.recipeId, -1)
                            Log.d("LikeToggle", "Like removed successfully")
                        } catch (e: Exception) {
                            Log.e("LikeToggle", "Error removing like: ${e.message}")
                            _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                                put(like.recipeId, true)
                            }
                        }
                    }
                    delay(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                        put(like.recipeId, _isLikedMap.value[like.recipeId] ?: false)
                    }
                } finally {
                    isActionInProgress = false
                }
            }
        }
    }



    private fun updateLikeCount(recipeId: Long, delta: Int) {
        viewModelScope.launch {
            val currentCount = _likeCounts.value[recipeId]?.likes ?: 0
            _likeCounts.value = _likeCounts.value.toMutableMap().apply {
                put(recipeId, LikeCountResponse(likes = currentCount + delta))
            }
        }
    }


    private val _likeCounts = MutableStateFlow<Map<Long, LikeCountResponse?>>(emptyMap())
    val likeCounts: StateFlow<Map<Long, LikeCountResponse?>> = _likeCounts

    fun fetchLikeCounts(recipeId: Long) {
        viewModelScope.launch {
            if (_likeCounts.value.containsKey(recipeId)) {
                // Like counts for this recipe already fetched, no need to fetch again
                return@launch
            }

            try {
                // Directly call the suspend function
                val response = RetrofitHelper.apiService.getLikeCounts(recipeId)
                _likeCounts.value += (recipeId to response)
            } catch (e: Exception) {
                // Handle the exception here
                _likeCounts.value += (recipeId to null)
                e.printStackTrace() // Optional: log the exception for debugging
            }
        }
    }



    val isActionInProgressFlow = MutableStateFlow(isActionInProgress)

}
