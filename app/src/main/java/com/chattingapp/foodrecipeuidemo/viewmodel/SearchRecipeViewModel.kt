package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.RecipeSearchResult
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SearchRecipeViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<RecipeSearchResult>>(emptyList())
    val searchResults: StateFlow<List<RecipeSearchResult>> = _searchResults

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Add a debounce to avoid rapid API calls
                .filter { it.isNotEmpty() }
                .collect { query ->
                    Log.d("SearchViewModel", "Search query updated: $query")
                    performSearch(query)
                }
        }
    }

    fun updateSearchQuery(query: String) {
        Log.d("SearchViewModel", "Updating search query to: $query")
        _searchQuery.value = query
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
        }
    }

    private suspend fun performSearch(query: String) {
        Log.d("SearchViewModel", "Performing search for query: $query")
        try {
            val result = RetrofitHelper.apiService.searchRecipes(query)
            _searchResults.value = result
            Log.d("SearchViewModel", "Search results received: ${result.size} recipes found")
        } catch (e: Exception) {
            Log.e("SearchViewModel", "Error performing search: ${e.message}", e)
            _searchResults.value = emptyList()
        }
    }
}
