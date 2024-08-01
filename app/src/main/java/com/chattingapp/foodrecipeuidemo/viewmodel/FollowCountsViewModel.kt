package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.FollowCountsDTO
import com.chattingapp.foodrecipeuidemo.entity.FollowRequest
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowCountsViewModel : ViewModel() {
    private val _followCounts = MutableLiveData<FollowCountsDTO>()
    val followCounts: LiveData<FollowCountsDTO> get() = _followCounts

    private val apiService = RetrofitHelper.apiService

    private var isActionInProgress = false

    fun fetchFollowersCount(userId: Long) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getFollowersCount(userId).execute()
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            response?.let { _followCounts.value = it }
        }
    }


    private val _isFollowing = MutableStateFlow<Boolean?>(null)
    val isFollowing: StateFlow<Boolean?> get() = _isFollowing

    private val _isChecking = MutableStateFlow(true)
    val isChecking: StateFlow<Boolean> = _isChecking

    fun checkIfUserFollows(followerId: Long, followedId: Long) {
        _isChecking.value = true
        viewModelScope.launch {
            try {
                val result = apiService.checkIfUserFollows(followerId, followedId)
                _isFollowing.value = result
            } catch (e: Exception) {
                _isFollowing.value = false
            } finally {
                _isChecking.value = false

            }
        }
    }

    fun followUser(followerId: Long, followedId: Long) {
        if(!isActionInProgress){
            isActionInProgress = true
            viewModelScope.launch {
                try {
                    val response = apiService.addUserFollows(FollowRequest(followerId, followedId))
                    if (response.isSuccessful) {
                        _isFollowing.value = true
                        isActionInProgress = false
                        adjustFollowerCount(followerId, followedId, increment = true)
                    }
                    delay(1000)
                } catch (e: Exception) {
                    // Handle the exception
                }

            }
        }

    }

    fun unfollowUser(followerId: Long, followedId: Long) {
        if(!isActionInProgress) {
            isActionInProgress = true
            viewModelScope.launch {
                try {
                    val response =
                        apiService.removeUserFollows(FollowRequest(followerId, followedId))
                    if (response.isSuccessful) {
                        _isFollowing.value = false
                        isActionInProgress = false
                        adjustFollowerCount(followerId, followedId, increment = false)
                    }
                    delay(1000)
                } catch (e: Exception) {
                    // Handle the exception
                }
            }
        }
    }

    private fun adjustFollowerCount(userId: Long, targetId: Long, increment: Boolean) {
        if(!isActionInProgress) {
            viewModelScope.launch {
                // Fetch the current follower count
                val currentCounts = _followCounts.value
                currentCounts?.let {
                    val newCount = if (increment) {
                        it.followersCount + 1
                    } else {
                        it.followersCount - 1
                    }
                    // Update the follower count
                    _followCounts.value = it.copy(followersCount = newCount)
                }
            }
        }
    }

    val isActionInProgressFlow = MutableStateFlow(isActionInProgress)
}