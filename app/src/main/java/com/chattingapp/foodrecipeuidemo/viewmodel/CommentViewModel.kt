package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentViewModel : ViewModel() {

    private val _commentCount = MutableLiveData<Long>()
    val commentCount: LiveData<Long> get() = _commentCount

    fun fetchCommentCount(recipeId: Long) {
        viewModelScope.launch {
            try {
                val count = RetrofitHelper.apiService.countCommentsByRecipeId(recipeId)
                _commentCount.postValue(count)
            } catch (e: Exception) {
                // Optionally handle errors
                _commentCount.postValue(0) // Optionally set a default value on error
                // Log or show error message
                Log.e("CommentViewModel", "Error fetching comment count", e)
            }
        }
    }


    private val _userProfilesComment = MutableLiveData<List<UserProfile>>()
    val userProfilesComment: LiveData<List<UserProfile>> get() = _userProfilesComment

    private val _profileImageCacheComment = MutableLiveData<MutableMap<Long, Bitmap?>>()
    val profileImageCacheComment: LiveData<MutableMap<Long, Bitmap?>> get() = _profileImageCacheComment

    private val _loadingStateComment = MutableLiveData<MutableMap<Long, Boolean>>()
    val loadingStateComment: LiveData<MutableMap<Long, Boolean>> get() = _loadingStateComment

    fun fetchUserProfiles(commentOwnerIds: List<Long>) {
        val currentLoadingState = _loadingStateComment.value ?: emptyMap()
        val idsToFetch = commentOwnerIds.filter { id -> currentLoadingState[id] != true }

        if (idsToFetch.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val profiles = mutableListOf<UserProfile>()
            val newLoadingState = currentLoadingState.toMutableMap()
            idsToFetch.forEach { id ->
                newLoadingState[id] = true
            }
            _loadingStateComment.postValue(newLoadingState)

            try {
                idsToFetch.forEach { id ->
                    val response = RetrofitHelper.apiService.getUserProfileById(id).execute()
                    if (response.isSuccessful) {
                        response.body()?.let { userProfile ->
                            profiles.add(userProfile)
                            userProfile.profilePicture?.let { fetchProfileImageComment(userProfile.id, it) }
                        }
                    }
                }
                val currentProfiles = _userProfilesComment.value ?: emptyList()
                _userProfilesComment.postValue(currentProfiles + profiles)
            } catch (e: Exception) {
                // Handle network error
            } finally {
                idsToFetch.forEach { id ->
                    newLoadingState[id] = false
                }
                _loadingStateComment.postValue(newLoadingState)
            }
        }
    }

    private fun fetchProfileImageComment(userId: Long, profilePictureUrl: String) {
        if (_profileImageCacheComment.value?.contains(userId) == true) return

        viewModelScope.launch {
            _loadingStateComment.value = _loadingStateComment.value?.toMutableMap()?.apply {
                put(userId, true)
            } ?: mutableMapOf(userId to true)

            try {
                val response = withContext(Dispatchers.IO) {
                    val imageResponse = RetrofitHelper.apiService.getImage(profilePictureUrl).execute()
                    if (imageResponse.isSuccessful) {
                        val decodedBytes = Base64.decode(imageResponse.body(), Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    } else {
                        null
                    }
                }

                _profileImageCacheComment.value = _profileImageCacheComment.value?.toMutableMap()?.apply {
                    put(userId, response)
                } ?: mutableMapOf(userId to response)

            } catch (e: Exception) {
                e.printStackTrace()
                _profileImageCacheComment.value = _profileImageCacheComment.value?.toMutableMap()?.apply {
                    put(userId, null)
                } ?: mutableMapOf(userId to null)
            } finally {
                _loadingStateComment.value = _loadingStateComment.value?.toMutableMap()?.apply {
                    put(userId, false)
                } ?: mutableMapOf(userId to false)
            }
        }
    }


    private val _comments = MutableLiveData<List<CommentProjection>>()
    val comments: LiveData<List<CommentProjection>> get() = _comments

    private val _hasMorePages = MutableLiveData<Boolean>(true)
    val hasMorePages: LiveData<Boolean> get() = _hasMorePages

    var currentPage = 0
    private var isLoading = false

    fun fetchComments(recipeId: Long) {
        if (isLoading || !hasMorePages.value!!) return

        isLoading = true
        RetrofitHelper.apiService.getComments(recipeId, currentPage).enqueue(object :
            Callback<List<CommentProjection>> {
            override fun onResponse(call: Call<List<CommentProjection>>, response: Response<List<CommentProjection>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newComments = response.body() ?: emptyList()
                    if (newComments.isNotEmpty()) {
                        val currentList = _comments.value?.toMutableList() ?: mutableListOf()
                        val existingIds = currentList.map { it.id }.toSet()
                        val filteredComments = newComments.filter { it.id !in existingIds }
                        if (filteredComments.isNotEmpty()) {
                            currentList.addAll(filteredComments)
                            _comments.value = currentList
                            currentPage += 1
                        }
                    } else {
                        _hasMorePages.value = false
                    }
                } else {
                    // Handle non-successful response
                    Log.e("CommentViewModel", "Response error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<CommentProjection>>, t: Throwable) {
                isLoading = false
                Log.e("CommentViewModel", "Failed to load comments", t)
            }
        })
    }
    private var isLastPage = false

    fun fetchMoreComments(recipeId: Long) {
        if (isLastPage) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.apiService.getComments(recipeId, currentPage).execute()
                if (response.isSuccessful) {
                    response.body()?.let { newComments ->
                        val currentComments = _comments.value ?: emptyList()
                        _comments.postValue(currentComments + newComments)

                        // Fetch user profiles for the new comments
                        val newOwnerIds = newComments.map { it.ownerId!! }.distinct()
                        fetchUserProfiles(newOwnerIds)

                        // Check if this is the last page
                        if (newComments.isEmpty()) {
                            isLastPage = true
                        } else {
                            currentPage++
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle network error
            }
        }
    }

}
