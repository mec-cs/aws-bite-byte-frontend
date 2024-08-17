package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.RecipeSearchResult
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class SearchUserViewModel: ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<UserProfile>>(emptyList())
    val searchResults: StateFlow<List<UserProfile>> = _searchResults

    init {
        // Observe search query changes and perform search
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Add a debounce to avoid rapid API calls
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collect { query ->
                    Log.d("SearchViewModel", "Search query updated: $query") // Log the search query
                    performSearch(query)
                }
        }
    }

    fun updateSearchQuery(query: String) {
        Log.d("SearchViewModel", "Updating search query to: $query") // Log when the query is updated
        _searchQuery.value = query
        if(_searchQuery.value == ""){
            _searchResults.value = emptyList()
        }
    }

    private suspend fun performSearch(query: String) {
        Log.d("SearchViewModel", "Performing search for query: $query") // Log before making the API call
        try {
            val result = RetrofitHelper.apiService.searchUsers(query)
            _searchResults.value = result
            Log.d("SearchViewModel", "Search results received: ${result.size} recipes found") // Log the result size
        } catch (e: Exception) {
            Log.e("SearchViewModel", "Error performing search: ${e.message}", e) // Log the error
            _searchResults.value = emptyList()
        }
    }

    private val imageCache = ConcurrentHashMap<String, Bitmap>()

    fun fetchImage(profileImage: String, onImageLoaded: (Bitmap?) -> Unit) {
        val cachedImage = imageCache[profileImage]
        if (cachedImage != null) {
            onImageLoaded(cachedImage)
            return
        }

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    // Directly call the suspend function
                    val imageString = RetrofitHelper.apiService.getImage(profileImage)
                    val decodedBytes = Base64.decode(imageString, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) {
                    null
                }
            }

            if (response != null) {
                imageCache[profileImage] = response
            }

            onImageLoaded(response)
        }
    }


}