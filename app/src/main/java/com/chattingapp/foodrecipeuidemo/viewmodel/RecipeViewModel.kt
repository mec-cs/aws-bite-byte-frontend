package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class RecipeViewModel : ViewModel() {
    private val apiService = RetrofitHelper.apiService
    private val imageCache = ConcurrentHashMap<String, Bitmap>()
    var listSize = 0
    var recipeListDetail: List<RecipeProjection> = emptyList()
    /*fun fetchImage(recipe: RecipeProjection, onImageLoaded: (Bitmap?) -> Unit) {
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
    }*/


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

            if (response != null) {
                imageCache[recipe.image!!] = response
            }

            onImageLoaded(response)
        }
    }

    private val _recipeList = MutableLiveData<List<RecipeProjection>>(emptyList())
    val recipeList: LiveData<List<RecipeProjection>> get() = _recipeList

    private val _displayRecipes = MutableLiveData(false)
    val displayRecipes: LiveData<Boolean> get() = _displayRecipes

    var page = 0
    private var isLoading = false

    /*fun fetchRecipes(userId: Long) {
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
    }*/

    fun updateRecipeList(newRecipes: List<RecipeProjection>) {
        _recipeList.value = newRecipes
        recipeListDetail = newRecipes
    }

    /*fun fetchRecipes(userId: Long) {
        if (isLoading) return
        isLoading = true

        RetrofitHelper.apiService.getRecipeDisplay(userId, page).enqueue(object : Callback<List<RecipeProjection>> {
            override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newRecipes = response.body() ?: emptyList()
                    updateRecipeList(newRecipes)
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
                        val updatedList = _recipeList.value?.toMutableList() ?: mutableListOf()
                        updatedList.addAll(newRecipes)
                        updateRecipeList(updatedList)
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
    }*/

    private fun getCurrentRecipeIds(): Set<Long?> {
        return _recipeList.value?.map { it.id }.orEmpty().toSet()
    }

    // Function to filter out duplicates
    private fun filterUniqueRecipes(newRecipes: List<RecipeProjection>): List<RecipeProjection> {
        val currentIds = getCurrentRecipeIds()
        return newRecipes.filterNot { it.id in currentIds }
    }

    fun fetchRecipes(userId: Long) {
        if(page == 0){


        if (isLoading) return
        isLoading = true

        RetrofitHelper.apiService.getRecipeDisplay(userId, page).enqueue(object : Callback<List<RecipeProjection>> {
            override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newRecipes = response.body() ?: emptyList()
                    if (newRecipes.isNotEmpty()) {
                        val currentList = _recipeList.value?.toMutableList() ?: mutableListOf()
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
                }
            }

            override fun onFailure(call: Call<List<RecipeProjection>>, t: Throwable) {
                isLoading = false
                Log.e("RecipeViewModel", "Failed to load recipes", t)
            }
        })
        }
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
                        val currentList = _recipeList.value?.toMutableList() ?: mutableListOf()
                        val existingIds = currentList.map { it.id }.toSet()
                        val filteredRecipes = newRecipes.filter { it.id !in existingIds }
                        if (filteredRecipes.isNotEmpty()) {
                            currentList.addAll(filteredRecipes)
                            _recipeList.value = currentList
                            recipeListDetail = currentList
                            page += 1
                        }
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

    fun createRecipe(
        name: String,
        description: String,
        cuisine: String,
        course: String,
        diet: String,
        prepTime: String,
        ingredients: String,
        instructions: String,
        imageUri: Uri?,
        ownerId: Long,
        type: Boolean
    ) {
        viewModelScope.launch {
            // Convert parameters to RequestBody
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val cuisinePart = cuisine.toRequestBody("text/plain".toMediaTypeOrNull())
            val coursePart = course.toRequestBody("text/plain".toMediaTypeOrNull())
            val dietPart = diet.toRequestBody("text/plain".toMediaTypeOrNull())
            val prepTimePart = prepTime.toRequestBody("text/plain".toMediaTypeOrNull())
            val ingredientsPart = ingredients.toRequestBody("text/plain".toMediaTypeOrNull())
            val instructionsPart = instructions.toRequestBody("text/plain".toMediaTypeOrNull())
            val imageUriPart = imageUri.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val ownerIdPart = ownerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = type.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Convert the imageUri to a MultipartBody.Part
            val imageFile = imageUri?.let {
                val file = File(it.path)
                MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            }

            // Call the API
            apiService.createTheRecipe(
                file = imageFile!!,
                name = namePart,
                description = descriptionPart,
                cuisine = cuisinePart,
                course = coursePart,
                diet = dietPart,
                prepTime = prepTimePart,
                ingredients = ingredientsPart,
                instructions = instructionsPart,
                image = imageUriPart,
                ownerId = ownerIdPart,
                type = typePart
            ).enqueue(object : Callback<Recipe> {

                override fun onResponse(
                    call: Call<Recipe>,
                    response: Response<Recipe>
                ) {
                    Log.d("OnResponseContent", "${call}\n" + "$response")

                    if (response.isSuccessful) {
                        Log.d("Recipe Created, HTTP: " + response.code(), response.body().toString())
                    } else {
                        Log.d("onResponse Fail", "Response Unsuccessful!")
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    Log.d("OnResponseContent", "${call}\n" + "$t")

                    Log.d("onFailure", "Fail to call the API function")
                }
            })
        }
    }

    fun saveRecipeAsDraft(
        name: String,
        description: String,
        cuisine: String,
        course: String,
        diet: String,
        prepTime: String,
        ingredients: String,
        instructions: String,
        imageUri: Uri?,
        ownerId: Long,
        type: Boolean,
        isImgChanged: Boolean
    ) {
        viewModelScope.launch {
            // Convert parameters to RequestBody
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val cuisinePart = cuisine.toRequestBody("text/plain".toMediaTypeOrNull())
            val coursePart = course.toRequestBody("text/plain".toMediaTypeOrNull())
            val dietPart = diet.toRequestBody("text/plain".toMediaTypeOrNull())
            val prepTimePart = prepTime.toRequestBody("text/plain".toMediaTypeOrNull())
            val ingredientsPart = ingredients.toRequestBody("text/plain".toMediaTypeOrNull())
            val instructionsPart = instructions.toRequestBody("text/plain".toMediaTypeOrNull())
            val imageUriPart = imageUri.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val ownerIdPart = ownerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = type.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val isImgChangedPart = isImgChanged.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Convert the imageUri to a MultipartBody.Part
            val imageFile = imageUri?.let {
                val file = File(it.path)
                MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            }

            // Call the API
            apiService.saveRecipeAsDraft(
                file = imageFile!!,
                name = namePart,
                description = descriptionPart,
                cuisine = cuisinePart,
                course = coursePart,
                diet = dietPart,
                prepTime = prepTimePart,
                ingredients = ingredientsPart,
                instructions = instructionsPart,
                image = imageUriPart,
                ownerId = ownerIdPart,
                type = typePart,
                isImgChanged = isImgChangedPart
            ).enqueue(object : Callback<Recipe> {
                override fun onResponse(
                    call: Call<Recipe>,
                    response: Response<Recipe>
                ) {
                    Log.d("OnResponseContent", "${call}\n" + "$response")

                    if (response.isSuccessful) {
                        Log.d("Draft Recipe, HTTP: ", response.code().toString())
                    } else {
                        Log.d("onResponse Fail", "Response Unsuccessful!")
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    Log.d("OnResponseContent", "${call}\n" + "$t")

                    Log.d("onFailure", "Fail to call the API function\n$t \n$call")
                }

            })
        }
    }

}
