package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelperRecommendation
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedViewModel : ViewModel() {

    // LiveData to hold the followed recipes
    private val _followedRecipes = MutableLiveData<List<Long>>(emptyList())
    val followedRecipes: LiveData<List<Long>> = _followedRecipes

    // LiveData to indicate loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchFollowedRecipes(followerId: Long) {
        Log.d("FeedViewModel", "Fetching followed recipes for followerId: $followerId")

        // Set isLoading to true when starting the fetch
        _isLoading.value = true

        viewModelScope.launch {
            val call = RetrofitHelper.apiService.getFollowedRecipes(followerId)
            call.enqueue(object : Callback<List<Long>> {
                override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                    if (response.isSuccessful) {
                        val recipes = response.body() ?: emptyList()

                        // Debugging: Print the response body
                        Log.d("FeedViewModel", "Response body: $recipes")

                        Log.d("FeedViewModel", "Successfully fetched ${recipes.size} recipes")
                        _followedRecipes.value = recipes
                    } else {
                        Log.e("FeedViewModel", "Error: ${response.code()} - ${response.message()}")
                    }

                    // Set isLoading to false once the fetch is complete
                    _isLoading.value = false
                }

                override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                    Log.e("FeedViewModel", "Error fetching followed recipes", t)

                    // Set isLoading to false in case of failure
                    _isLoading.value = false
                }
            })
        }
    }


    private val _recommendedRecipes = MutableLiveData<List<Long>>(emptyList())
    val recommendedRecipes: LiveData<List<Long>> = _recommendedRecipes

    private val _isLoadingRecommended = MutableLiveData(false)
    val isLoadingRecommended: LiveData<Boolean> = _isLoadingRecommended

    fun fetchRecommendedRecipes(userId: Long) {
        Log.d("FeedViewModel", "Fetching followed recipes for userId: $userId")

        _isLoadingRecommended.value = true

        viewModelScope.launch {
            val call = RetrofitHelperRecommendation.apiService.recommendRecipes(userId)
            call.enqueue(object : Callback<List<Long>> {
                override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                    if (response.isSuccessful) {
                        val recipes = response.body() ?: emptyList()
                        Log.d("FeedViewModel", "Response body: $recipes")
                        _recommendedRecipes.value = recipes
                    } else {
                        Log.e("FeedViewModel", "Error: ${response.code()} - ${response.message()}")
                    }
                    _isLoadingRecommended.value = false
                }

                override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                    Log.e("FeedViewModel", "Error fetching followed recipes", t)
                    _isLoadingRecommended.value = false
                }
            })
        }
    }

    private val _cachedRecipes = MutableLiveData<List<Long>>(emptyList())
    val cachedRecipes: LiveData<List<Long>> = _cachedRecipes

    private val _isLoadingCached = MutableLiveData(false)
    val isLoadingCached: LiveData<Boolean> = _isLoadingCached

    fun fetchCachedRecipes() {

        _isLoadingCached.value = true

        viewModelScope.launch {
            val call = RetrofitHelper.apiService.getCachedRecipes()
            call.enqueue(object : Callback<List<Long>> {
                override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                    if (response.isSuccessful) {
                        val recipes = response.body() ?: emptyList()
                        Log.d("FeedViewModel", "Response body: $recipes")
                        _cachedRecipes.value = recipes
                    } else {
                        Log.e("FeedViewModel", "Error: ${response.code()} - ${response.message()}")
                    }
                    _isLoadingCached.value = false
                }

                override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                    Log.e("FeedViewModel", "Error fetching followed recipes", t)
                    _isLoadingCached.value = false
                }
            })
        }
    }
}
