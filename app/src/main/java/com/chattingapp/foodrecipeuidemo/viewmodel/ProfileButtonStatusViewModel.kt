package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update

class ProfileButtonStatusViewModel : ViewModel() {

    // Use MutableStateFlow instead of mutableStateOf
    private val _isFollowing = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isFollowing: StateFlow<Map<String, Boolean>> = _isFollowing

    private val _isRemoved = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isRemoved: StateFlow<Map<String, Boolean>> = _isRemoved

    fun setIsFollowing(userId: String, status: Boolean) {
        _isFollowing.update { currentMap ->
            currentMap.toMutableMap().apply {
                put(userId, status)
            }
        }
    }

    fun setIsRemoved(userId: String, status: Boolean) {
        _isRemoved.update { currentMap ->
            currentMap.toMutableMap().apply {
                put(userId, status)
            }
        }
    }
}