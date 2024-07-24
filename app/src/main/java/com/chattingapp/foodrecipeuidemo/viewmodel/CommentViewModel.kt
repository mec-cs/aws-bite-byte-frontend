package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.launch
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


    private val _comments = MutableLiveData<List<CommentProjection>>()
    val comments: LiveData<List<CommentProjection>> get() = _comments

    private val _hasMorePages = MutableLiveData<Boolean>(true)
    val hasMorePages: LiveData<Boolean> get() = _hasMorePages

    private var currentPage = 0
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

}
