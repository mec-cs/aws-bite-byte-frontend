package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class UserProfileViewModel : ViewModel() {

    private val apiService = RetrofitHelper.apiService
    private val imageCache = ConcurrentHashMap<String, Bitmap>()

    fun fetchImage(recipe: RecipeProjection, onImageLoaded: (Bitmap?) -> Unit) {
        val cachedImage = imageCache[recipe.image]
        if (cachedImage != null) {
            onImageLoaded(cachedImage)
            return
        }

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getImage(recipe.ownerImage!!).execute()
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
}
