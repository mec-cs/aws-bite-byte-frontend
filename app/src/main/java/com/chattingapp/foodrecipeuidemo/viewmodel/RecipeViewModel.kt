package com.chattingapp.foodrecipeuidemo.viewmodel

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

class RecipeViewModel : ViewModel() {
    private val apiService = RetrofitHelper.apiService
    var listSize = 0
    var recipeListDetail: List<RecipeProjection> = emptyList()

    private val _recipeList = MutableStateFlow<List<RecipeProjection>>(emptyList())
    val recipeList: StateFlow<List<RecipeProjection>> get() = _recipeList

    private val _displayRecipes = MutableStateFlow(false)

    private var page = 0
    private var isLoading = false


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
