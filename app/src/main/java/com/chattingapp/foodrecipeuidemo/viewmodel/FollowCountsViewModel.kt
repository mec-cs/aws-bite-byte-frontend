package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.FollowCountsDTO
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowCountsViewModel : ViewModel() {
    private val _followCounts = MutableLiveData<FollowCountsDTO>()
    val followCounts: LiveData<FollowCountsDTO> get() = _followCounts

    private val apiService = RetrofitHelper.apiService

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
}