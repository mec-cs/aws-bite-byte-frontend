package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.constant.Constant
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
import java.io.File

class CreateRecipeViewModel: ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> get() = _isSuccess




    private val _errorMessage = MutableStateFlow<String?>(null)

    fun createRecipe(
        recipeName: String,
        description: String,
        cuisine: String,
        course: String,
        diet: String,
        prepTime: String,
        ingredients: String,
        instructions: String,
        selectedImageFile: File?
    ) {
        viewModelScope.launch {
            _isSuccess.emit(false)
            _isLoading.value = true
            try {
                val namePart = recipeName.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val cuisinePart = cuisine.toRequestBody("text/plain".toMediaTypeOrNull())
                val coursePart = course.toRequestBody("text/plain".toMediaTypeOrNull())
                val dietPart = diet.toRequestBody("text/plain".toMediaTypeOrNull())
                val prepTimePart = prepTime.toRequestBody("text/plain".toMediaTypeOrNull())
                val ingredientsPart = ingredients.toRequestBody("text/plain".toMediaTypeOrNull())
                val instructionsPart = instructions.toRequestBody("text/plain".toMediaTypeOrNull())
                val imageUriPart = selectedImageFile?.name?.toRequestBody("text/plain".toMediaTypeOrNull())
                val ownerIdPart = Constant.userProfile.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val typePart = "true".toRequestBody("text/plain".toMediaTypeOrNull())

                val imageFile = selectedImageFile?.let {
                    MultipartBody.Part.createFormData("file", it.name, it.asRequestBody("image/jpeg".toMediaTypeOrNull()))
                }

                withContext(Dispatchers.IO) {
                    RetrofitHelper.apiService.createTheRecipe(
                        file = imageFile!!,
                        name = namePart,
                        description = descriptionPart,
                        cuisine = cuisinePart,
                        course = coursePart,
                        diet = dietPart,
                        prepTime = prepTimePart,
                        ingredients = ingredientsPart,
                        instructions = instructionsPart,
                        image = imageUriPart!!,
                        ownerId = ownerIdPart,
                        type = typePart
                    )
                }

                _isSuccess.emit(true)
                _errorMessage.value = null
            } catch (e: Exception) {
                _isSuccess.emit(false)
                _errorMessage.value = "Failed to create recipe: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}