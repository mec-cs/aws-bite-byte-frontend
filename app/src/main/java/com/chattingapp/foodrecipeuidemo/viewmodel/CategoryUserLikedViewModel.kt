package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class CategoryUserLikedViewModel: ViewModel() {

    private val _recipes = MutableStateFlow<List<RecipeProjection>>(emptyList())
    val recipes: StateFlow<List<RecipeProjection>> = _recipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var currentPage = 0
    private val pageSize = Constant.PAGE_SIZE_CLICK_LIKE
    private var allIds: List<Long> = emptyList()

    fun fetchMostClickedLastTwoIds() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitHelper.apiService.getMostClickedRecipesLastTwo().execute()
                }
                if (response.isSuccessful) {
                    allIds = response.body() ?: emptyList()
                    loadMoreRecipes()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
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