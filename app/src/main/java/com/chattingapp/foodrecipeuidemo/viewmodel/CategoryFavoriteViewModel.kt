package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

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

            viewModelScope.launch {
                try {
                    // Call the suspend function directly
                    val newRecipes = RetrofitHelper.apiService.getRecipesFavorite(userId, page)

                    if (newRecipes.isNotEmpty()) {
                        Log.d("CategoryFavoriteViewModel", "Fetched ${newRecipes.size} recipes")

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
                } catch (e: IOException) {
                    Log.e("CategoryFavoriteViewModel", "Network error while fetching recipes", e)
                } catch (e: HttpException) {
                    Log.e("CategoryFavoriteViewModel", "HTTP error while fetching recipes", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }



    fun loadMoreRecipes(userId: Long) {
        if (isLoadingMore || noMoreRecipes) return
        isLoadingMore = true
        Log.d("CategoryFavoriteViewModel", "Loading more recipes for user: $userId, page: $page")

        viewModelScope.launch {
            try {
                // Call the suspend function directly
                val newRecipes = RetrofitHelper.apiService.getRecipesFavorite(userId, page)

                if (newRecipes.isNotEmpty()) {
                    Log.d("CategoryFavoriteViewModel", "Loaded ${newRecipes.size} more recipes")

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
            } catch (e: IOException) {
                Log.e("CategoryFavoriteViewModel", "Network error while loading more recipes", e)
            } catch (e: HttpException) {
                Log.e("CategoryFavoriteViewModel", "HTTP error while loading more recipes", e)
            } finally {
                isLoadingMore = false
            }
        }
    }



    private val _favoriteCount = MutableLiveData<Long>(-1L)
    val favoriteCount: LiveData<Long> get() = _favoriteCount

    private val _isLoadingCount = MutableLiveData(false)
    val isLoadingCount: LiveData<Boolean> get() = _isLoadingCount

    fun fetchFavoriteCount(userId: Long) {
        viewModelScope.launch {
            _isLoadingCount.value = true
            try {
                // Directly call the suspend function
                val favoriteCount = RetrofitHelper.apiService.getFavoriteCount(userId)
                _favoriteCount.value = favoriteCount
                Log.d("FavoriteCountViewModel", "Favorite count: $favoriteCount")
            } catch (e: IOException) {
                Log.e("FavoriteCountViewModel", "Network error while fetching favorite count", e)
            } catch (e: HttpException) {
                Log.e("FavoriteCountViewModel", "HTTP error while fetching favorite count", e)
            } finally {
                _isLoadingCount.value = false
            }
        }
    }




}