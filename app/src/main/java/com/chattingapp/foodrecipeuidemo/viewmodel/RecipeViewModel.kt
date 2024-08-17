package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.RecipeSpecificDTO
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class RecipeViewModel : ViewModel() {
    private val apiService = RetrofitHelper.apiService
    private val imageCache = ConcurrentHashMap<String, Bitmap>()
    var listSize = 0
    var recipeListDetail: List<RecipeProjection> = emptyList()

    private val _recipeList = MutableStateFlow<List<RecipeProjection>>(emptyList())
    val recipeList: StateFlow<List<RecipeProjection>> get() = _recipeList

    private val _displayRecipes = MutableStateFlow(false)
    val displayRecipes: StateFlow<Boolean> get() = _displayRecipes



    var page = 0
    private var isLoading = false

    /*fun fetchImage(recipeImage: String, onImageLoaded: (Bitmap?) -> Unit) {
        val cachedImage = imageCache[recipeImage]
        if (cachedImage != null) {
            onImageLoaded(cachedImage)
            return
        }

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    // Directly call the suspend function
                    val imageString = apiService.getImageRecipe(recipeImage)
                    val decodedBytes = Base64.decode(imageString, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) {
                    null
                }
            }

            if (response != null) {
                imageCache[recipeImage] = response
            }

            onImageLoaded(response)
        }
    }*/

    private fun getCurrentRecipeIds(): Set<Long?> {
        return _recipeList.value.map { it.id }.toSet()
    }

    // Function to filter out duplicates
    private fun filterUniqueRecipes(newRecipes: List<RecipeProjection>): List<RecipeProjection> {
        val currentIds = getCurrentRecipeIds()
        return newRecipes.filterNot { it.id in currentIds }
    }

    fun fetchRecipes(userId: Long) {
        if (page == 0) {
            if (isLoading) return
            isLoading = true

            viewModelScope.launch {
                try {
                    val newRecipes = withContext(Dispatchers.IO) {
                        apiService.getRecipeDisplay(userId, page)
                    }

                    isLoading = false
                    if (newRecipes.isNotEmpty()) {
                        val currentList = _recipeList.value.toMutableList()
                        val existingIds = currentList.map { it.id }.toSet()
                        val filteredRecipes = newRecipes.filter { it.id !in existingIds }
                        if (filteredRecipes.isNotEmpty()) {
                            currentList.addAll(filteredRecipes)
                            _recipeList.value = currentList
                            recipeListDetail = currentList
                            _displayRecipes.value = true
                            page += 1
                        }
                    }
                } catch (e: Exception) {
                    isLoading = false
                    Log.e("RecipeViewModel", "Failed to load recipes", e)
                }
            }
        }
    }

    fun loadMoreRecipes(userId: Long) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                val newRecipes = withContext(Dispatchers.IO) {
                    apiService.getRecipeDisplay(userId, page)
                }

                if (newRecipes.isNotEmpty()) {
                    val currentList = _recipeList.value.toMutableList()
                    val existingIds = currentList.map { it.id }.toSet()
                    val filteredRecipes = newRecipes.filter { it.id !in existingIds }
                    if (filteredRecipes.isNotEmpty()) {
                        currentList.addAll(filteredRecipes)
                        _recipeList.value = currentList
                        recipeListDetail = currentList
                        page += 1
                    }
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Failed to load more recipes", e)
                page -= 1
            } finally {
                isLoading = false
            }
        }
    }



    private val _recipe = mutableStateOf<RecipeSpecificDTO?>(null)
    val recipe: State<RecipeSpecificDTO?> = _recipe

    private val _isLoadingRecipe = mutableStateOf(true)
    val isLoadingRecipe: State<Boolean> = _isLoadingRecipe


    fun fetchRecipeById(id: Long) {
        viewModelScope.launch {
            _isLoadingRecipe.value = true
            try {
                val response = apiService.getRecipeSpecificById(id)
                _recipe.value = response
                Log.d("RecipeViewModel", "Recipe fetched: ${response.diet}")
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipe", e)
                _recipe.value = null
            } finally {
                _isLoadingRecipe.value = false
            }
        }
    }

}
