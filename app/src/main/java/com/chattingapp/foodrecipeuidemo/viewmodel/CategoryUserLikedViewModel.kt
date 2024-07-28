package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryUserLikedViewModel: ViewModel() {

    private val _recipeList = MutableLiveData<List<RecipeProjection>>(emptyList())
    val recipeList: LiveData<List<RecipeProjection>> get() = _recipeList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    var page = 0
    private var isLoadingMore = false

    var recipeListDetail: List<RecipeProjection> = emptyList()
    private var noMoreRecipes = false


    fun fetchRecipes(userId: Long) {
        if (page == 0 && !isLoadingMore) {
            _isLoading.value = true
            Log.d("CategoryLikeViewModel", "Fetching recipes for user: $userId, page: $page")

            RetrofitHelper.apiService.getRecipesLike(userId, page).enqueue(object :
                Callback<List<RecipeProjection>> {
                override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val newRecipes = response.body() ?: emptyList()
                        Log.d("CategoryLikeViewModel", "Fetched ${newRecipes.size} recipes")
                        if (newRecipes.isNotEmpty()) {
                            val currentList = _recipeList.value?.toMutableList() ?: mutableListOf()
                            val existingIds = currentList.map { it.id }.toSet()
                            val filteredRecipes = newRecipes.filter { it.id !in existingIds }
                            if (filteredRecipes.isNotEmpty()) {
                                currentList.addAll(filteredRecipes)
                                _recipeList.value = currentList
                                recipeListDetail = currentList
                                page += 1
                                Log.d("CategoryLikeViewModel", "Updated recipe list with ${filteredRecipes.size} new recipes")
                            }
                        }
                    } else {
                        Log.e("CategoryLikeViewModel", "Failed to fetch recipes: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<List<RecipeProjection>>, t: Throwable) {
                    _isLoading.value = false
                    Log.e("CategoryLikeViewModel", "Failed to load recipes", t)
                }
            })
        }
    }


    fun loadMoreRecipes(userId: Long) {
        if (isLoadingMore || noMoreRecipes) return
        isLoadingMore = true
        Log.d("CategoryLikeViewModel", "Loading more recipes for user: $userId, page: $page")

        RetrofitHelper.apiService.getRecipesLike(userId, page).enqueue(object :
            Callback<List<RecipeProjection>> {
            override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                isLoadingMore = false
                if (response.isSuccessful) {
                    val newRecipes = response.body() ?: emptyList()
                    Log.d("CategoryFavoriteViewModel", "Loaded ${newRecipes.size} more recipes")
                    if (newRecipes.isNotEmpty()) {
                        val currentList = _recipeList.value?.toMutableList() ?: mutableListOf()
                        val existingIds = currentList.map { it.id }.toSet()
                        val filteredRecipes = newRecipes.filter { it.id !in existingIds }
                        if (filteredRecipes.isNotEmpty()) {
                            currentList.addAll(filteredRecipes)
                            _recipeList.value = currentList
                            recipeListDetail = currentList
                            page += 1
                            Log.d("CategoryLikeViewModel", "Updated recipe list with ${filteredRecipes.size} new recipes")
                        }
                    } else {
                        noMoreRecipes = true
                    }
                } else {
                    Log.e("CategoryLikeViewModel", "Failed to load more recipes: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<RecipeProjection>>, t: Throwable) {
                isLoadingMore = false
                Log.e("CategoryLikeViewModel", "Failed to load more recipes", t)
                page -= 1
            }
        })
    }


    private val _likeCount = MutableLiveData<Long>(-1L)
    val likeCount: LiveData<Long> get() = _likeCount

    private val _isLoadingCount = MutableLiveData(false)
    val isLoadingCount: LiveData<Boolean> get() = _isLoadingCount

    fun fetchLikeCount(userId: Long) {
        _isLoadingCount.value = true
        RetrofitHelper.apiService.getLikeCountByUserId(userId).enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                _isLoadingCount.value = false
                if (response.isSuccessful) {
                    _likeCount.value = response.body()
                    Log.d("LikeCountViewModel", "Like count: ${response.body()}")
                } else {
                    Log.e("LikeCountViewModel", "Failed to fetch like count: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                _isLoadingCount.value = false
                Log.e("LikeCountViewModel", "Failed to fetch like count", t)
            }
        })
    }



}