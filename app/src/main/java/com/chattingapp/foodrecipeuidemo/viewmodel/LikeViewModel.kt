package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.runtime.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikeViewModel() : ViewModel() {

    private val _likeCount = MutableLiveData<LikeCountResponse>()
    val likeCount: LiveData<LikeCountResponse> get() = _likeCount

    private val _isLiked = MutableLiveData<Boolean>(false)
    val isLiked: LiveData<Boolean> get() = _isLiked

    private val _loadingState = MutableStateFlow<Map<Long, Boolean>>(emptyMap()) // New loading state
    val loadingState: StateFlow<Map<Long, Boolean>> = _loadingState

    private var isLoading = false
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
                val response = RetrofitHelper.apiService.getLike(recipeId, userId).await()
                _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                    put(recipeId, response)
                }
                _checkedLikeStatus.value = _checkedLikeStatus.value + recipeId // Mark as checked
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

    fun toggleLike(like: Like) {
        viewModelScope.launch {
            try {
                val currentLikeState = _isLikedMap.value[like.recipeId] ?: false
                val newLikeState = !currentLikeState
                _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                    put(like.recipeId, newLikeState)
                }

                if (newLikeState) {
                    val response = RetrofitHelper.apiService.addLike(like).awaitResponse()
                    if (response.isSuccessful) {
                        updateLikeCount(like.recipeId, 1)

                        // Handle successful addition of like
                        Log.d("LikeToggle", "Like added successfully")
                    } else {
                        // Handle error response
                        Log.e("LikeToggle", "Error adding like: ${response.errorBody()?.string()}")
                        _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                            put(like.recipeId, false)
                        }
                    }
                } else {
                    val response = RetrofitHelper.apiService.deleteLike(like.recipeId, like.userId).awaitResponse()
                    if (response.isSuccessful) {
                        // Handle successful removal of like
                        updateLikeCount(like.recipeId, -1)

                        Log.d("LikeToggle", "Like removed successfully")
                    } else {
                        // Handle error response
                        _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                            put(like.recipeId, true)
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _isLikedMap.value = _isLikedMap.value.toMutableMap().apply {
                    put(like.recipeId, _isLikedMap.value[like.recipeId] ?: false)
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
                val response = RetrofitHelper.apiService.getLikeCounts(recipeId).awaitResponse()
                if (response.isSuccessful) {
                    _likeCounts.value = _likeCounts.value + (recipeId to response.body())
                } else {
                    // Handle the error here
                    _likeCounts.value = _likeCounts.value + (recipeId to null)
                }
            } catch (e: Exception) {
                // Handle the exception here
                _likeCounts.value = _likeCounts.value + (recipeId to null)
            }
        }
    }





    fun addLike(like: Like) {
        viewModelScope.launch(Dispatchers.IO) {
            RetrofitHelper.apiService.addLike(like).enqueue(object : Callback<Like> {
                override fun onResponse(call: Call<Like>, response: Response<Like>) {

                }

                override fun onFailure(call: Call<Like>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }

    fun deleteLike(recipeId: Long, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            RetrofitHelper.apiService.deleteLike(recipeId, userId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }

}
