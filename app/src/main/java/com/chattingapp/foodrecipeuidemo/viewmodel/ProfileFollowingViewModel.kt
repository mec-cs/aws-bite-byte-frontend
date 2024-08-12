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
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class ProfileFollowingViewModel: ViewModel() {
    fun fetchImage(user: UserFollowsResponse, onImageLoaded: (Bitmap?) -> Unit) {
        val cachedImage = imageCache[user.followed.profilePicture]
        if (cachedImage != null) {
            onImageLoaded(cachedImage)
            return
        }

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    // Call the suspend function directly
                    val imageString = RetrofitHelper.apiService.getImage(user.followed.profilePicture)
                    val decodedBytes = Base64.decode(imageString, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) {
                    null
                }
            }

            if (response != null) {
                imageCache[user.followed.profilePicture] = response
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

            viewModelScope.launch {
                try {
                    // Call the suspend function directly
                    val newUsers = RetrofitHelper.apiService.getFollowingsByUserId(userId, page)
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
                } catch (e: IOException) {
                    Log.e("ProfileFollowerViewModel", "Network Error: ${e.message}", e)
                } catch (e: HttpException) {
                    Log.e("ProfileFollowerViewModel", "HTTP Error: ${e.message}", e)
                } catch (e: Exception) {
                    Log.e("ProfileFollowerViewModel", "Unexpected Error: ${e.message}", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }


    fun loadMoreUsers(userId: Long) {
        if (_isLoading.value) return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Call the suspend function directly
                val newUsers = RetrofitHelper.apiService.getFollowingsByUserId(userId, page)
                Log.d("ProfileFollowerViewModel", "Loaded more users: ${newUsers.size}")

                if (newUsers.isNotEmpty()) {
                    // Append new users to the existing list
                    val currentList = _userList.value.toMutableList()
                    currentList.addAll(newUsers)
                    _userList.value = currentList
                    userListDetail = currentList // Update detail list
                    page += 1
                }
            } catch (e: IOException) {
                Log.e("ProfileFollowerViewModel", "Network Error: ${e.message}", e)
            } catch (e: HttpException) {
                Log.e("ProfileFollowerViewModel", "HTTP Error: ${e.message}", e)
            } catch (e: Exception) {
                Log.e("ProfileFollowerViewModel", "Unexpected Error: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }




}