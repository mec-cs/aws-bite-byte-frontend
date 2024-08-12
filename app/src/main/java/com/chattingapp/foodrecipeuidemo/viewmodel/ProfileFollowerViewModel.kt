package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.UserFollowsResponse
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ConcurrentHashMap

class ProfileFollowerViewModel : ViewModel() {

    fun fetchImage(user: UserFollowsResponse, onImageLoaded: (Bitmap?) -> Unit) {
        val cachedImage = imageCache[user.follower.profilePicture]
        if (cachedImage != null) {
            onImageLoaded(cachedImage)
            return
        }

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    // Directly call the suspend function
                    val imageString = RetrofitHelper.apiService.getImage(user.follower.profilePicture)
                    val decodedBytes = Base64.decode(imageString, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) {
                    null
                }
            }

            if (response != null) {
                imageCache[user.follower.profilePicture] = response
            }

            onImageLoaded(response)
        }
    }

    var page = 0
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _userList = MutableStateFlow<List<UserFollowsResponse>>(emptyList())
    val userList: StateFlow<List<UserFollowsResponse>> get() = _userList

    private val _displayUser = MutableStateFlow(false)
    val displayUser: StateFlow<Boolean> get() = _displayUser

    var userListDetail: List<UserFollowsResponse> = emptyList()
    private val imageCache = ConcurrentHashMap<String, Bitmap>()

    fun fetchUsers(userId: Long) {
        if (page == 0 && !_isLoading.value) {
            _isLoading.value = true

            RetrofitHelper.apiService.getFollowersByUserId(userId, page).enqueue(object : Callback<List<UserFollowsResponse>> {
                override fun onResponse(call: Call<List<UserFollowsResponse>>, response: Response<List<UserFollowsResponse>>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val newUsers = response.body() ?: emptyList()
                        Log.d("ProfileFollowerViewModel", "Fetched users: ${newUsers.size}")
                        if (newUsers.isNotEmpty()) {
                            val currentList = _userList.value.toMutableList()
                            val existingIds = currentList.map { it.id }.toSet()
                            val filteredUsers = newUsers.filter { it.id !in existingIds }
                            if (filteredUsers.isNotEmpty()) {
                                currentList.addAll(filteredUsers)
                                _userList.value = currentList
                                userListDetail = currentList
                                _displayUser.value = true
                                page += 1
                            }
                        }
                    } else {
                        Log.e("ProfileFollowerViewModel", "Failed to fetch users: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<UserFollowsResponse>>, t: Throwable) {
                    _isLoading.value = false
                    Log.e("ProfileFollowerViewModel", "Error fetching users", t)
                }
            })
        }
    }

    fun loadMoreUser(userId: Long) {
        if (_isLoading.value) return
        _isLoading.value = true

        RetrofitHelper.apiService.getFollowersByUserId(userId, page).enqueue(object : Callback<List<UserFollowsResponse>> {
            override fun onResponse(call: Call<List<UserFollowsResponse>>, response: Response<List<UserFollowsResponse>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val newUsers = response.body() ?: emptyList()
                    Log.d("ProfileFollowerViewModel", "Loaded more users: ${newUsers.size}")
                    if (newUsers.isNotEmpty()) {
                        val currentList = _userList.value.toMutableList()
                        val existingIds = currentList.map { it.id }.toSet()
                        val filteredUsers = newUsers.filter { it.id !in existingIds }
                        if (filteredUsers.isNotEmpty()) {
                            currentList.addAll(filteredUsers)
                            _userList.value = currentList
                            userListDetail = currentList
                            page += 1
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<UserFollowsResponse>>, t: Throwable) {
                _isLoading.value = false
                Log.e("ProfileFollowerViewModel", "Error loading more users", t)
                page -= 1
            }
        })
    }
}
