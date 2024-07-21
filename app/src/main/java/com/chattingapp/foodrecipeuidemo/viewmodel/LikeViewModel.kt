package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.Like
import com.chattingapp.foodrecipeuidemo.entity.LikeCountResponse
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitAPICredentials
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.await
import retrofit2.awaitResponse

class LikeViewModel(private val recipeApiService: RetrofitAPICredentials = RetrofitHelper.apiService) : ViewModel() {

    private val _likeCount = MutableLiveData<LikeCountResponse>()
    val likeCount: LiveData<LikeCountResponse> get() = _likeCount

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean> get() = _isLiked

    private val _isDisliked = MutableLiveData<Boolean>()
    val isDisliked: LiveData<Boolean> get() = _isDisliked

    private var isLoading = false
    private val _isLikedMap = MutableStateFlow<Map<Long, Like>>(emptyMap())
    val isLikedMap: StateFlow<Map<Long, Like>> = _isLikedMap

    fun checkLike(userId: Long, recipeId: Long) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                val response = recipeApiService.getLike(recipeId, userId).await()
                _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                    put(recipeId, response)
                }
                _isLiked.value = response.type
                _isDisliked.value = !response.type
            } catch (e: Exception) {
                e.printStackTrace()
                _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                    put(recipeId, Like(-1, -1, -1, false))
                }
                _isLiked.value = false
                _isDisliked.value = false
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleLike(like: Like) {
        viewModelScope.launch {
            try {
                if (_isDisliked.value == true) {
                    _isDisliked.value = false
                }

                _isLiked.value = !_isLiked.value!! // Toggle the like state

                if (_isLiked.value == true) {
                    val response = recipeApiService.addLike(like).awaitResponse()
                    if (response.isSuccessful) {
                        // Handle successful addition of like
                        Log.d("LikeToggle", "Like added successfully")
                    } else {
                        // Handle error response
                        Log.e("LikeToggle", "Error adding like: ${response.errorBody()?.string()}")
                        _isLiked.value = false // Revert state if the request failed
                    }
                } else {
                    val response = recipeApiService.deleteLike(like.recipeId, like.userId).awaitResponse()
                    if (response.isSuccessful) {
                        // Handle successful removal of like
                        Log.d("LikeToggle", "Like removed successfully")
                    } else {
                        // Handle error response
                        Log.e("LikeToggle", "Error removing like: ${response.errorBody()?.string()}")
                        _isLiked.value = true // Revert state if the request failed
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isLiked.value = !_isLiked.value!! // Revert the like state in case of an error
            }
        }
    }


    fun toggleDislike(like: Like) {
        if (_isLiked.value == true) {
            _isLiked.value = false
        }
        _isDisliked.value = _isDisliked.value != true

        viewModelScope.launch {
            try {
                if (_isDisliked.value == true) {
                    val response = recipeApiService.addLike(like).awaitResponse()
                    if (response.isSuccessful) {
                        // Handle successful addition of dislike
                    } else {
                        // Handle error response
                        _isDisliked.value = false
                    }
                } else {
                    val response = recipeApiService.deleteLike(like.recipeId, like.userId).awaitResponse()
                    if (response.isSuccessful) {
                        // Handle successful removal of dislike
                    } else {
                        // Handle error response
                        _isDisliked.value = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isDisliked.value = !_isDisliked.value!! // Revert the dislike state in case of an error
            }
        }
    }

    fun fetchLikeCounts(recipeId: Long) {
        viewModelScope.launch {
            try {
                val response = recipeApiService.getLikeCounts(recipeId).awaitResponse()
                if (response.isSuccessful) {
                    _likeCount.value = response.body()
                } else {
                    // Handle the error here
                }
            } catch (e: Exception) {
                // Handle the exception here
            }
        }
    }
}
