package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ConcurrentHashMap

class RecipeViewModel : ViewModel() {
    private val apiService = RetrofitHelper.apiService
    private val imageCache = ConcurrentHashMap<String, Bitmap>()

    fun fetchImage(recipe: RecipeProjection, onImageLoaded: (Bitmap?) -> Unit) {
        val cachedImage = imageCache[recipe.image]
        if (cachedImage != null) {
            onImageLoaded(cachedImage)
            return
        }

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getImageRecipe(recipe.image!!).execute()
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
            response?.let { imageCache[recipe.image!!] = it }
            onImageLoaded(response)
        }
    }

    private val _recipeList = MutableLiveData<List<RecipeProjection>>(emptyList())
    val recipeList: LiveData<List<RecipeProjection>> get() = _recipeList

    private val _displayRecipes = MutableLiveData(false)
    val displayRecipes: LiveData<Boolean> get() = _displayRecipes

    private var page = 0
    private var isLoading = false

    fun fetchRecipes(userId: Long) {
        if (isLoading) return
        isLoading = true

        RetrofitHelper.apiService.getRecipeDisplay(userId, page).enqueue(object : Callback<List<RecipeProjection>> {
            override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newRecipes = response.body() ?: emptyList()
                    _recipeList.value = newRecipes
                    _displayRecipes.value = true
                    page += 1
                }
            }

            override fun onFailure(call: Call<List<RecipeProjection>>, t: Throwable) {
                isLoading = false
                Log.e("RecipeViewModel", "Failed to load recipes", t)
            }
        })
    }

    fun loadMoreRecipes(userId: Long) {
        if (isLoading) return
        isLoading = true

        RetrofitHelper.apiService.getRecipeDisplay(userId, page).enqueue(object : Callback<List<RecipeProjection>> {
            override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newRecipes = response.body() ?: emptyList()
                    if (newRecipes.isNotEmpty()) {
                        // Append new recipes to existing list
                        _recipeList.value = _recipeList.value?.let { it + newRecipes }
                        page += 1
                    }
                }
            }

            override fun onFailure(call: Call<List<RecipeProjection>>, t: Throwable) {
                isLoading = false
                Log.e("RecipeViewModel", "Failed to load more recipes", t)
                page -= 1
            }
        })
    }

}
