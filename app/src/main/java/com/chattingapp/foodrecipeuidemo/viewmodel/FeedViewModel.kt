/*package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelperRecommendation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel : ViewModel() {

    // LiveData to hold the followed recipes
    private val _followedRecipes = MutableLiveData<List<Long>>(emptyList())
    val followedRecipes: LiveData<List<Long>> = _followedRecipes

    // LiveData to indicate loading state
    private val _isLoadingFollowingsRecipes = MutableLiveData(false)
    val isLoadingFollowingsRecipes: LiveData<Boolean> = _isLoadingFollowingsRecipes

    fun fetchFollowedRecipes(followerId: Long) {
        // Set isLoading to true when starting the fetch
        _isLoadingFollowingsRecipes.value = true

        // Launch a coroutine to perform the network request
        viewModelScope.launch {
            try {
                // Call the suspend function to get the followed recipes
                val recipes = RetrofitHelper.apiService.getFollowedRecipes(followerId)

                // Update the LiveData with the fetched recipes
                _followedRecipes.value = recipes

                // Log for debugging
                Log.d("FeedViewModel", "Successfully fetched ${recipes.size} recipes")
            } catch (e: Exception) {
                // Handle any errors that occur during the request
                Log.e("FeedViewModel", "Error fetching followed recipes", e)
            } finally {
                // Set isLoading to false once the fetch is complete
                _isLoadingFollowingsRecipes.value = false
            }
        }
    }


    private val _recommendedRecipes = MutableLiveData<List<Long>>(emptyList())
    val recommendedRecipes: LiveData<List<Long>> = _recommendedRecipes

    private val _isLoadingRecommended = MutableLiveData(false)
    val isLoadingRecommended: LiveData<Boolean> = _isLoadingRecommended

    fun fetchRecommendedRecipes(userId: Long) {
        _isLoadingRecommended.value = true

        viewModelScope.launch {
            try {
                val recipes = RetrofitHelperRecommendation.apiService.recommendRecipes(userId)
                _recommendedRecipes.value = recipes
            } catch (e: Exception) {
                // Handle the error
                Log.e("FeedViewModel", "Error fetching recommended recipes", e)
            } finally {
                _isLoadingRecommended.value = false
            }
        }
    }


    private val _cachedRecipes = MutableLiveData<List<Long>>(emptyList())
    val cachedRecipes: LiveData<List<Long>> = _cachedRecipes

    private val _isLoadingCached = MutableLiveData(false)
    val isLoadingCached: LiveData<Boolean> = _isLoadingCached

    fun fetchCachedRecipes() {
        // Set isLoading to true when starting the fetch
        _isLoadingCached.value = true
        // Launch a coroutine to perform the network request
        viewModelScope.launch {
            try {
                // Call the suspend function to get cached recipes
                val recipes = RetrofitHelper.apiService.getCachedRecipes()

                // Update the LiveData with the fetched recipes
                _cachedRecipes.value = recipes

                // Log for debugging
                Log.d("FeedViewModel", "Successfully fetched ${recipes.size} cached recipes")
            } catch (e: Exception) {
                // Handle any errors that occur during the request
                Log.e("FeedViewModel", "Error fetching cached recipes", e)
            } finally {
                // Set isLoading to false once the fetch is complete
                _isLoadingCached.value = false
            }
        }
    }


    private var currentSmallestNumber: Long? = null
    private val generatedNumbersList = mutableListOf<Long>() // Global variable to store the result

    fun getNumbersBasedOnCachedRecipes() {
        if (currentSmallestNumber != 1L) {
            val cachedRecipesList = _cachedRecipes.value ?: emptyList()
            val startNumber = currentSmallestNumber ?: cachedRecipesList.firstOrNull()

            if (startNumber == null) {
                return
            }

            generatedNumbersList.clear()

            val endNumber = if (startNumber - 999 > 0) startNumber - 999 else 2
            for (i in startNumber-1 downTo endNumber-1) {
                generatedNumbersList.add(i)
            }

            val firstItem = generatedNumbersList.firstOrNull()
            val lastItem = generatedNumbersList.lastOrNull()

            generatedNumbersList.shuffle()
            currentSmallestNumber = endNumber - 1
        }
    }

    private val _initializationComplete = MutableLiveData(false)
    val initializationComplete: LiveData<Boolean> = _initializationComplete

    private val _mergeComplete = MutableLiveData(false)
    val mergeComplete: LiveData<Boolean> = _mergeComplete

    fun initializeCurrentSmallestNumber(){
        val cachedRecipesList = _cachedRecipes.value ?: emptyList()
        Log.d("initializeCurrentSmallestNumber", "bad")

        currentSmallestNumber = cachedRecipesList.firstOrNull()
        _initializationComplete.value = true
        Log.d("initializeCurrentSmallestNumber", "good")

    }
    private val _allIdsSize = MutableStateFlow(0) // New StateFlow for size
    val allIdsSize: StateFlow<Int> = _allIdsSize

    private var allIds: List<Long> = emptyList()


    fun mergeAndInterleaveRecipes() {
        Log.d("mergeAndInterleaveRecipes", "Starting merge")

        val mergedRecipes = mutableListOf<Long>()
        val cachedList = _cachedRecipes.value?.toMutableList() ?: mutableListOf()
        val followedList = _followedRecipes.value?.toMutableList() ?: mutableListOf()

        while (cachedList.isNotEmpty() || followedList.isNotEmpty()) {
            // Fetch first 20 from followed recipes
            val followedBatch = mutableListOf<Long>()
            repeat(20) {
                if (followedList.isNotEmpty()) {
                    followedBatch.add(followedList.removeAt(0))
                }
            }

            // Fetch first 20 from cached recipes
            val cachedBatch = mutableListOf<Long>()
            repeat(20) {
                if (cachedList.isNotEmpty()) {
                    cachedBatch.add(cachedList.removeAt(0))
                }
            }

            // Merge and shuffle the batches
            val combinedBatch = (followedBatch + cachedBatch).toMutableList()
            combinedBatch.shuffle()

            // Add the shuffled batch to the mergedRecipes list
            mergedRecipes.addAll(combinedBatch)
        }

        // Combine the recommended recipes and the merged list
        allIds = (_recommendedRecipes.value ?: emptyList()) + mergedRecipes
        Log.d("allIds", "allIds size BEFORE linked map: ${allIds.size}")

        // Remove duplicates while maintaining order
        allIds = LinkedHashSet(allIds).toList()
        Log.d("allIds", "allIds size AFTER linked map: ${allIds.size}")

        // Update the global list size
        _allIdsSize.value = allIds.size

        // Log final state
        Log.d("mergeAndInterleaveRecipes", "Final merged recipes without duplicates: $allIds")
        Log.d("mergeAndInterleaveRecipes", "Global list of IDs SIZE: ${allIds.size}")

        _mergeComplete.value = true
    }



    private var currentPage = 0
    private val pageSize = Constant.PAGE_SIZE_CLICK_LIKE
    private val _recipes = MutableStateFlow<List<RecipeProjection>>(emptyList())
    val recipes: StateFlow<List<RecipeProjection>> = _recipes

    private val _isLoadingRecipes = MutableStateFlow(false)
    val isLoadingRecipes: StateFlow<Boolean> = _isLoadingRecipes



    fun loadMoreRecipes() {
        //if (currentPage * pageSize >= allIds.size) return // No more pages
        if (currentPage * pageSize >= allIds.size){
            getNumbersBasedOnCachedRecipes()
            allIds += generatedNumbersList
            _allIdsSize.value = allIds.size
        }
        val idsToFetch = allIds.drop(currentPage * pageSize).take(pageSize)

        viewModelScope.launch {
            _isLoadingRecipes.value = true
            try {
                val fetchedRecipes = withContext(Dispatchers.IO) {
                    RetrofitHelper.apiService.getRecipes(idsToFetch)
                }
                //_recipes.value += fetchedRecipes // Append new recipes to the existing list
                val filteredRecipes = fetchedRecipes.filter { it.ownerId != Constant.userProfile.id }
                _recipes.value += filteredRecipes // Append new recipes to the existing list

                currentPage++
                if(filteredRecipes.isEmpty()){
                    loadMoreRecipes()
                }
            } catch (_: Exception) {

            } finally {
                _isLoadingRecipes.value = false
            }
        }
    }

}*/
package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelperRecommendation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel : ViewModel() {

    private val _followedRecipes = MutableStateFlow<List<Long>>(emptyList())
    val followedRecipes: StateFlow<List<Long>> = _followedRecipes

    private val _isLoadingFollowingsRecipes = MutableStateFlow(false)
    val isLoadingFollowingsRecipes: StateFlow<Boolean> = _isLoadingFollowingsRecipes

    fun fetchFollowedRecipes(followerId: Long) {
        _isLoadingFollowingsRecipes.value = true

        viewModelScope.launch {
            try {
                val recipes = RetrofitHelper.apiService.getFollowedRecipes(followerId)
                _followedRecipes.value = recipes
                Log.d("FeedViewModel", "Successfully fetched ${recipes.size} recipes")
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error fetching followed recipes", e)
            } finally {
                _isLoadingFollowingsRecipes.value = false
            }
        }
    }

    private val _recommendedRecipes = MutableStateFlow<List<Long>>(emptyList())
    val recommendedRecipes: StateFlow<List<Long>> = _recommendedRecipes

    private val _isLoadingRecommended = MutableStateFlow(false)
    val isLoadingRecommended: StateFlow<Boolean> = _isLoadingRecommended

    fun fetchRecommendedRecipes(userId: Long) {
        _isLoadingRecommended.value = true

        viewModelScope.launch {
            try {
                val recipes = RetrofitHelperRecommendation.apiService.recommendRecipes(userId)
                _recommendedRecipes.value = recipes
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error fetching recommended recipes", e)
            } finally {
                _isLoadingRecommended.value = false
            }
        }
    }

    private val _cachedRecipes = MutableStateFlow<List<Long>>(emptyList())
    val cachedRecipes: StateFlow<List<Long>> = _cachedRecipes

    private val _isLoadingCached = MutableStateFlow(false)
    val isLoadingCached: StateFlow<Boolean> = _isLoadingCached

    fun fetchCachedRecipes() {
        _isLoadingCached.value = true

        viewModelScope.launch {
            try {
                val recipes = RetrofitHelper.apiService.getCachedRecipes()
                _cachedRecipes.value = recipes
                Log.d("FeedViewModel", "Successfully fetched ${recipes.size} cached recipes")
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error fetching cached recipes", e)
            } finally {
                _isLoadingCached.value = false
            }
        }
    }

    private var currentSmallestNumber: Long? = null
    private val generatedNumbersList = mutableListOf<Long>()

    private fun getNumbersBasedOnCachedRecipes() {
        if (currentSmallestNumber != 1L) {
            val cachedRecipesList = _cachedRecipes.value
            val startNumber = currentSmallestNumber ?: cachedRecipesList.firstOrNull()

            if (startNumber == null) {
                return
            }

            generatedNumbersList.clear()

            val endNumber = if (startNumber - 999 > 0) startNumber - 999 else 2
            for (i in startNumber - 1 downTo endNumber - 1) {
                generatedNumbersList.add(i)
            }

            generatedNumbersList.shuffle()
            currentSmallestNumber = endNumber - 1
        }
    }

    private val _initializationComplete = MutableStateFlow(false)
    val initializationComplete: StateFlow<Boolean> = _initializationComplete

    private val _mergeComplete = MutableStateFlow(false)
    val mergeComplete: StateFlow<Boolean> = _mergeComplete

    fun initializeCurrentSmallestNumber() {
        val cachedRecipesList = _cachedRecipes.value
        Log.d("initializeCurrentSmallestNumber", "bad")

        currentSmallestNumber = cachedRecipesList.firstOrNull()
        _initializationComplete.value = true
        Log.d("initializeCurrentSmallestNumber", "good")
    }

    private val _allIdsSize = MutableStateFlow(0)
    val allIdsSize: StateFlow<Int> = _allIdsSize

    private var allIds: List<Long> = emptyList()

    fun mergeAndInterleaveRecipes() {
        Log.d("mergeAndInterleaveRecipes", "Starting merge")

        val mergedRecipes = mutableListOf<Long>()
        val cachedList = _cachedRecipes.value.toMutableList()
        val followedList = _followedRecipes.value.toMutableList()

        while (cachedList.isNotEmpty() || followedList.isNotEmpty()) {
                    val followedBatch = mutableListOf<Long>()
                    repeat(20) {
                        if (followedList.isNotEmpty()) {
                            followedBatch.add(followedList.removeAt(0))
                        }
                    }

                    val cachedBatch = mutableListOf<Long>()
                    repeat(20) {
                        if (cachedList.isNotEmpty()) {
                            cachedBatch.add(cachedList.removeAt(0))
                        }
                    }

                    val combinedBatch = (followedBatch + cachedBatch).toMutableList()
                    combinedBatch.shuffle()

                    mergedRecipes.addAll(combinedBatch)
                }

        allIds = (_recommendedRecipes.value ) + mergedRecipes
        Log.d("allIds", "allIds size BEFORE linked map: ${allIds.size}")

        allIds = LinkedHashSet(allIds).toList()
        Log.d("allIds", "allIds size AFTER linked map: ${allIds.size}")

        _allIdsSize.value = allIds.size

        Log.d("mergeAndInterleaveRecipes", "Final merged recipes without duplicates: $allIds")
        Log.d("mergeAndInterleaveRecipes", "Global list of IDs SIZE: ${allIds.size}")

        _mergeComplete.value = true
    }

    private var currentPage = 0
    private val pageSize = Constant.PAGE_SIZE_CLICK_LIKE
    private val _recipes = MutableStateFlow<List<RecipeProjection>>(emptyList())
    val recipes: StateFlow<List<RecipeProjection>> = _recipes

    private val _isLoadingRecipes = MutableStateFlow(false)
    val isLoadingRecipes: StateFlow<Boolean> = _isLoadingRecipes

    fun loadMoreRecipes() {
        if (currentPage * pageSize >= allIds.size) {
            getNumbersBasedOnCachedRecipes()
            allIds += generatedNumbersList
            _allIdsSize.value = allIds.size
        }
        val idsToFetch = allIds.drop(currentPage * pageSize).take(pageSize)

        viewModelScope.launch {
            _isLoadingRecipes.value = true
            try {
                val fetchedRecipes = withContext(Dispatchers.IO) {
                    RetrofitHelper.apiService.getRecipes(idsToFetch)
                }
                val filteredRecipes = fetchedRecipes.filter { it.ownerId != Constant.userProfile.id }
                _recipes.value += filteredRecipes

                currentPage++
                if (filteredRecipes.isEmpty()) {
                    loadMoreRecipes()
                }
            } catch (_: Exception) {
                // Handle error
            } finally {
                _isLoadingRecipes.value = false
            }
        }
    }
}
