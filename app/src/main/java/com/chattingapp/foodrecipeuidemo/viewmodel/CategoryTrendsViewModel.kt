package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class CategoryTrendsViewModel: ViewModel() {

    private val _recipes = MutableStateFlow<List<RecipeProjection>>(emptyList())
    val recipes: StateFlow<List<RecipeProjection>> = _recipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var currentPage = 0
    private val pageSize = Constant.PAGE_SIZE_CLICK_LIKE
    private var allIds: List<Long> = emptyList()

    private val _allIdsSize = MutableStateFlow(0) // New StateFlow for size
    val allIdsSize: StateFlow<Int> = _allIdsSize



    fun fetchMostClickedLastTwoIds() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Directly call the suspend function
                val mostClickedIds = RetrofitHelper.apiService.getMostClickedRecipesLastTwo()
                allIds = mostClickedIds ?: emptyList()
                _allIdsSize.value = allIds.size
                if(_allIdsSize.value > 0) {
                    loadMoreRecipes()
                }
            } catch (e: IOException) {
                _errorMessage.value = "Network Error: ${e.message}"
            } catch (e: HttpException) {
                _errorMessage.value = "HTTP Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun loadMoreRecipes() {
        if (currentPage * pageSize >= allIds.size) return // No more pages

        val idsToFetch = allIds.drop(currentPage * pageSize).take(pageSize)

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedRecipes = withContext(Dispatchers.IO) {
                    RetrofitHelper.apiService.getRecipes(idsToFetch)
                }
                _recipes.value = _recipes.value + fetchedRecipes // Append new recipes to the existing list
                currentPage++
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }



}