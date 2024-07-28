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

class CategoryFavoriteViewModel : ViewModel(){

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
            Log.d("CategoryFavoriteViewModel", "Fetching recipes for user: $userId, page: $page")

            RetrofitHelper.apiService.getRecipesFavorite(userId, page).enqueue(object : Callback<List<RecipeProjection>> {
                override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val newRecipes = response.body() ?: emptyList()
                        Log.d("CategoryFavoriteViewModel", "Fetched ${newRecipes.size} recipes")
                        if (newRecipes.isNotEmpty()) {
                            val currentList = _recipeList.value?.toMutableList() ?: mutableListOf()
                            val existingIds = currentList.map { it.id }.toSet()
                            val filteredRecipes = newRecipes.filter { it.id !in existingIds }
                            if (filteredRecipes.isNotEmpty()) {
                                currentList.addAll(filteredRecipes)
                                _recipeList.value = currentList
                                recipeListDetail = currentList
                                page += 1
                                Log.d("CategoryFavoriteViewModel", "Updated recipe list with ${filteredRecipes.size} new recipes")
                            }
                        }
                    } else {
                        Log.e("CategoryFavoriteViewModel", "Failed to fetch recipes: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<List<RecipeProjection>>, t: Throwable) {
                    _isLoading.value = false
                    Log.e("CategoryFavoriteViewModel", "Failed to load recipes", t)
                }
            })
        }
    }


    fun loadMoreRecipes(userId: Long) {
        if (isLoadingMore || noMoreRecipes) return
        isLoadingMore = true
        Log.d("CategoryFavoriteViewModel", "Loading more recipes for user: $userId, page: $page")

        RetrofitHelper.apiService.getRecipesFavorite(userId, page).enqueue(object : Callback<List<RecipeProjection>> {
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
                            Log.d("CategoryFavoriteViewModel", "Updated recipe list with ${filteredRecipes.size} new recipes")
                        }
                    } else {
                        noMoreRecipes = true
                    }
                } else {
                    Log.e("CategoryFavoriteViewModel", "Failed to load more recipes: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<RecipeProjection>>, t: Throwable) {
                isLoadingMore = false
                Log.e("CategoryFavoriteViewModel", "Failed to load more recipes", t)
                page -= 1
            }
        })
    }


    private val _favoriteCount = MutableLiveData<Long>(-1L)
    val favoriteCount: LiveData<Long> get() = _favoriteCount

    private val _isLoadingCount = MutableLiveData(false)
    val isLoadingCount: LiveData<Boolean> get() = _isLoadingCount

    fun fetchFavoriteCount(userId: Long) {
        _isLoadingCount.value = true
        RetrofitHelper.apiService.getFavoriteCount(userId).enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                _isLoadingCount.value = false
                if (response.isSuccessful) {
                    _favoriteCount.value = response.body()
                    Log.d("FavoriteCountViewModel", "Favorite count: ${response.body()}")
                } else {
                    Log.e("FavoriteCountViewModel", "Failed to fetch favorite count: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                _isLoadingCount.value = false
                Log.e("FavoriteCountViewModel", "Failed to fetch favorite count", t)
            }
        })
    }



}