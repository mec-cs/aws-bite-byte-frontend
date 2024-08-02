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
import java.util.concurrent.ConcurrentHashMap

class CommentViewModel : ViewModel() {

    private val _commentCount = MutableLiveData<Long>(-1)
    val commentCount: LiveData<Long> get() = _commentCount

    fun fetchCommentCount(recipeId: Long) {
        viewModelScope.launch {
            try {
                val count = RetrofitHelper.apiService.countCommentsByRecipeId(recipeId)
                Log.d("CommentViewModel", "Fetched comment count: $count")
                _commentCount.postValue(count)
            } catch (e: Exception) {
                _commentCount.postValue(0) // Optionally set a default value on error
                Log.e("CommentViewModel", "Error fetching comment count", e)
            }
        }
    }

    private val _userProfilesComment = MutableLiveData<List<UserProfile>>(emptyList())
    val userProfilesComment: LiveData<List<UserProfile>> get() = _userProfilesComment

    private val _profileImageCacheComment = MutableLiveData<MutableMap<Long, Bitmap?>>(mutableMapOf())
    val profileImageCacheComment: LiveData<MutableMap<Long, Bitmap?>> get() = _profileImageCacheComment

    private val _loadingStateComment = MutableLiveData<MutableMap<Long, Boolean>>(mutableMapOf())
    val loadingStateComment: LiveData<MutableMap<Long, Boolean>> get() = _loadingStateComment



    private val imageCache = ConcurrentHashMap<String, Bitmap>()

    fun fetchImage(comment: CommentProjection, onImageLoaded: (Bitmap?) -> Unit) {
        val cachedImage = imageCache[comment.profilePicture]
        if (cachedImage != null) {
            onImageLoaded(cachedImage)
            return
        }

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitHelper.apiService.getImage(comment.profilePicture!!).execute()
                    if (response.isSuccessful) {
                        val decodedBytes = Base64.decode(response.body(), Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            if (response != null) {
                imageCache[comment.profilePicture!!] = response
            }

            onImageLoaded(response)
        }
    }

    private val _comments = MutableLiveData<List<CommentProjection>>(emptyList())
    val comments: LiveData<List<CommentProjection>> get() = _comments

    private val _hasMorePages = MutableLiveData<Boolean>(true)
    val hasMorePages: LiveData<Boolean> get() = _hasMorePages

    var currentPage = 0
    private var isLoading = false
    private var isLastPage = false

    fun fetchComments(recipeId: Long) {
        if (isLoading || !hasMorePages.value!!) {
            Log.d("CommentViewModel", "Skipping fetchComments: isLoading=$isLoading, hasMorePages=${hasMorePages.value}")
            return
        }

        isLoading = true
        Log.d("CommentViewModel", "Fetching comments for recipeId=$recipeId, currentPage=$currentPage")

        RetrofitHelper.apiService.getComments(recipeId, currentPage).enqueue(object :
            Callback<List<CommentProjection>> {
            override fun onResponse(call: Call<List<CommentProjection>>, response: Response<List<CommentProjection>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newComments = response.body() ?: emptyList()
                    Log.d("CommentViewModel", "Fetched ${newComments.size} new comments")
                    if (newComments.isNotEmpty()) {
                        val currentList = _comments.value?.toMutableList() ?: mutableListOf()
                        val existingIds = currentList.map { it.id }.toSet()
                        val filteredComments = newComments.filter { it.id !in existingIds }
                        Log.d("CommentViewModel", "Filtered ${filteredComments.size} new comments")
                        if (filteredComments.isNotEmpty()) {
                            currentList.addAll(filteredComments)
                            _comments.value = currentList
                            currentPage += 1
                            Log.d("CommentViewModel", "Current page updated to $currentPage")
                        }
                    } else {
                        _hasMorePages.value = false
                        Log.d("CommentViewModel", "No more pages available")
                    }
                } else {
                    Log.e("CommentViewModel", "Response error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<CommentProjection>>, t: Throwable) {
                isLoading = false
                Log.e("CommentViewModel", "Failed to load comments", t)
            }
        })
    }

    fun fetchMoreComments(recipeId: Long) {
        if (isLastPage || isLoading) {
            Log.d("CommentViewModel", "Skipping fetchMoreComments: isLastPage=$isLastPage")
            return
        }

        Log.d("CommentViewModel", "Fetching more comments for recipeId=$recipeId, currentPage=$currentPage")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.apiService.getComments(recipeId, currentPage).execute()
                if (response.isSuccessful) {
                    response.body()?.let { newComments ->
                        val currentComments = _comments.value ?: emptyList()
                        _comments.postValue(currentComments + newComments)
                        Log.d("CommentViewModel", "Fetched and added ${newComments.size} new comments")

                        // Fetch user profiles for the new comments

                        // Check if this is the last page
                        if (newComments.isEmpty()) {
                            isLastPage = true
                            Log.d("CommentViewModel", "This is the last page")
                        } else {
                            currentPage++
                            Log.d("CommentViewModel", "Current page updated to $currentPage")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Network error while fetching more comments", e)
            }
        }
    }

    fun resetState() {
        _comments.value = emptyList()
        _commentCount.value = -1
        _userProfilesComment.value = emptyList()
        _profileImageCacheComment.value = mutableMapOf()
        _hasMorePages.value = true
        currentPage = 0
        isLastPage = false
        isLoading = false
        Log.d("CommentViewModel", "State reset successfully")
    }
}
