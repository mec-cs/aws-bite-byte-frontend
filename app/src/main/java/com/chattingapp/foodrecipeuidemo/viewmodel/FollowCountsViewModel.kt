package com.chattingapp.foodrecipeuidemo.viewmodel

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
    private val _followCounts = MutableStateFlow<FollowCountsDTO?>(null)
    val followCounts: StateFlow<FollowCountsDTO?> get() = _followCounts

    private val apiService = RetrofitHelper.apiService

    private var isActionInProgress = false



    fun fetchFollowersCount(userId: Long) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getFollowersCount(userId)
                }
                _followCounts.value = response
            } catch (e: Exception) {
                // Handle or log the exception
                _followCounts.value = null
            }
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
                        adjustFollowerCount(increment = true)
                    }
                    delay(1000)
                } catch (e: Exception) {
                    // Handle the exception
                }

            }
        }

    }


    private val _isUnfollowing = MutableStateFlow(false)

    fun unfollowUser(followerId: Long, followedId: Long) {
        if(!isActionInProgress) {
            isActionInProgress = true
            viewModelScope.launch {
                _isUnfollowing.value = true
                try {
                    val response =
                        apiService.removeUserFollows(FollowRequest(followerId, followedId))
                    if (response.isSuccessful) {
                        _isFollowing.value = false
                        isActionInProgress = false
                        adjustFollowerCount(increment = false)
                    }
                    delay(1000)
                } catch (e: Exception) {
                    // Handle the exception
                }
            }
        }
    }

    private fun adjustFollowerCount(increment: Boolean) {
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