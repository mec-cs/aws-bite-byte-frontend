package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.RecipeSpecificDTO
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _recipeList = MutableStateFlow<List<RecipeProjection>>(emptyList())
    val recipeList: StateFlow<List<RecipeProjection>> get() = _recipeList

    private val _displayRecipes = MutableStateFlow(false)
    val displayRecipes: StateFlow<Boolean> get() = _displayRecipes



    var page = 0
    private var isLoading = false

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

            apiService.getRecipeDisplay(userId, page).enqueue(object : Callback<List<RecipeProjection>> {
                override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val newRecipes = response.body() ?: emptyList()
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

        apiService.getRecipeDisplay(userId, page).enqueue(object : Callback<List<RecipeProjection>> {
            override fun onResponse(call: Call<List<RecipeProjection>>, response: Response<List<RecipeProjection>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newRecipes = response.body() ?: emptyList()
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
                }
            }

            override fun onFailure(call: Call<List<RecipeProjection>>, t: Throwable) {
                isLoading = false
                Log.e("RecipeViewModel", "Failed to load more recipes", t)
                page -= 1
            }
        })
    }




    //
    // //
    // // CREATE RECIPE VIEW MODEL PART
    // //
    //
    var nullExceptionSave = false
    var nullExceptionDraft = false

    fun createRecipe(
        name: String,
        description: String,
        cuisine: String,
        course: String,
        diet: String,
        prepTime: String,
        ingredients: String,
        instructions: String,
        imageUri: String,
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
            val imageUriPart = imageUri.toRequestBody("text/plain".toMediaTypeOrNull())
            val ownerIdPart = ownerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = type.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Convert the imageUri to a MultipartBody.Part
            val imageFile = imageUri?.let {
                val file = File(it)
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

                    if (response.body() == null) {
                        Log.d("Save NULL Response", "Response is null, please check conditions")
                        nullExceptionSave = true
                    } else if (response.isSuccessful) {
                        Log.d("com.chattingapp.foodrecipeuidemo.composables.search.Recipe Created, HTTP: " + response.code(), response.body().toString())
                        nullExceptionSave = false
                    } else {
                        Log.d("onResponse Fail", "Response Unsuccessful!")
                        nullExceptionSave = false
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    Log.d("OnResponseContent", "${call}\n" + "$t")
                    Log.d("onFailure", "Fail to call the API function")
                    nullExceptionSave = false
                }
            })
        }
    }

    fun saveRecipeAsDraft(
        name: String?,
        description: String,
        cuisine: String,
        course: String,
        diet: String,
        prepTime: String,
        ingredients: String,
        instructions: String,
        imageUri: String?,
        ownerId: Long,
        type: Boolean,
        isImgChanged: Boolean
    ) {

        viewModelScope.launch {
            // Convert parameters to RequestBody
            val namePart = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val cuisinePart = cuisine.toRequestBody("text/plain".toMediaTypeOrNull())
            val coursePart = course.toRequestBody("text/plain".toMediaTypeOrNull())
            val dietPart = diet.toRequestBody("text/plain".toMediaTypeOrNull())
            val prepTimePart = prepTime.toRequestBody("text/plain".toMediaTypeOrNull())
            val ingredientsPart = ingredients.toRequestBody("text/plain".toMediaTypeOrNull())
            val instructionsPart = instructions.toRequestBody("text/plain".toMediaTypeOrNull())
            val imageUriPart = imageUri?.toRequestBody("text/plain".toMediaTypeOrNull())
            val ownerIdPart = ownerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = type.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val isImgChangedPart = isImgChanged.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Convert the imageUri to a MultipartBody.Part
            val imageFile = imageUri?.let {
                val file = File(it)
                MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            }

            // Call the API
            if (namePart != null && imageUriPart != null) {
                apiService.saveRecipeAsDraft(
                    file = imageFile,
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

                        if (response.body() == null) {
                            Log.d("Draft NULL Response", "Response is null, please check conditions")
                            nullExceptionDraft = true
                        } else if (response.isSuccessful) {
                            Log.d("Draft com.chattingapp.foodrecipeuidemo.composables.search.Recipe, HTTP: ", response.code().toString())
                            nullExceptionDraft = false
                        } else {
                            Log.d("onResponse Fail", "Response Unsuccessful!")
                            nullExceptionDraft = false
                        }
                    }

                    override fun onFailure(call: Call<Recipe>, t: Throwable) {
                        Log.d("OnResponseContent", "${call}\n" + "$t")
                        Log.d("onFailure", "Fail to call the API function\n$t \n$call")
                        nullExceptionDraft = false
                    }

                })
            }
        }
    }

    //
    // //
    // //
    //




    private val _recipe = mutableStateOf<RecipeSpecificDTO?>(null)
    val recipe: State<RecipeSpecificDTO?> = _recipe

    private val _isLoadingRecipe = mutableStateOf(true)
    val isLoadingRecipe: State<Boolean> = _isLoadingRecipe


    fun fetchRecipeById(id: Long) {
        viewModelScope.launch {
            _isLoadingRecipe.value = true
            try {
                val response = apiService.getRecipeById(id)
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
