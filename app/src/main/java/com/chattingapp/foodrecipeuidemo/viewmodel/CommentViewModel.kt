package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.Comment
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
                    // Directly call the suspend function
                    val response = RetrofitHelper.apiService.getImage(comment.profilePicture!!)
                    val decodedBytes = Base64.decode(response, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
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
                        val existingIds = currentComments.map { it.id }.toSet()
                        val filteredComments = newComments.filter { it.id !in existingIds }

                        _comments.postValue(currentComments + filteredComments)
                        Log.d("CommentViewModel", "Fetched and added ${filteredComments.size} new comments")

                       /* if (filteredComments.isEmpty()) {
                            //isLastPage = true
                            Log.d("CommentViewModel", "This is the last page")
                        } else {*/
                            currentPage++
                            Log.d("CommentViewModel", "Current page updated to $currentPage")
                        //}
                    }
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Network error while fetching more comments", e)
            }
        }
    }




    fun addComment(recipeId: Long, commentText: String) {
        val comment = Comment(-1, Constant.userProfile.id, commentText, null, recipeId)
        Log.d("CommentViewModel", "Attempting to add comment: $comment")

        viewModelScope.launch {
            try {
                RetrofitHelper.apiService.addComment(comment).enqueue(object : Callback<Comment> {
                    override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                        Log.d("CommentViewModel", "API call made to add comment: ${response.raw()}")
                        if (response.isSuccessful) {
                            Log.d("CommentViewModel", "Successfully added comment: ${response.body()}")
                            val newComment = response.body()
                            if (newComment != null) {
                                val commentProjection = CommentProjection(
                                    newComment.id,
                                    newComment.ownerId,
                                    newComment.comment,
                                    newComment.dateCreated,
                                    Constant.userProfile.username,
                                    Constant.userProfile.profilePicture
                                )
                                val updatedComments = _comments.value?.toMutableList() ?: mutableListOf()
                                updatedComments.add(0, commentProjection)  // Insert at the start of the list
                                _comments.value = updatedComments

                                Log.d("CommentViewModel", "Updated comments: $updatedComments")

                                // Optionally update comment count
                                val currentCount = _commentCount.value ?: 0
                                _commentCount.value = currentCount + 1
                                Log.d("CommentViewModel", "Updated comment count: ${_commentCount.value}")
                            } else {
                                Log.e("CommentViewModel", "Response body is null despite successful response")
                            }
                        } else {
                            Log.e("CommentViewModel", "Failed to add comment: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Comment>, t: Throwable) {
                        Log.e("CommentViewModel", "Exception occurred while adding comment", t)
                    }
                })
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Exception occurred while adding comment", e)
            }
        }
    }


    fun deleteComment(commentId: Long) {
        Log.d("CommentViewModel", "Attempting to delete comment with id: $commentId")

        viewModelScope.launch {
            try {
                RetrofitHelper.apiService.deleteComment(commentId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("CommentViewModel", "API call made to delete comment: ${response.raw()}")
                        if (response.isSuccessful) {
                            Log.d("CommentViewModel", "Successfully deleted comment with id: $commentId")

                            // Update the comment list after deletion
                            val updatedComments = _comments.value?.filter { it.id != commentId } ?: mutableListOf()
                            _comments.value = updatedComments

                            Log.d("CommentViewModel", "Updated comments after deletion: $updatedComments")

                            // Optionally update comment count
                            val currentCount = _commentCount.value ?: 0
                            _commentCount.value = (currentCount - 1).coerceAtLeast(0) // Ensure count doesn't go below 0
                            Log.d("CommentViewModel", "Updated comment count: ${_commentCount.value}")
                        } else {
                            Log.e("CommentViewModel", "Failed to delete comment: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("CommentViewModel", "Exception occurred while deleting comment", t)
                    }
                })
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Exception occurred while deleting comment", e)
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
