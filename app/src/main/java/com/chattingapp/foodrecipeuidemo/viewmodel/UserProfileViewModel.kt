package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.HttpException
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
                    // Directly call the suspend function
                    val imageString = apiService.getImage(recipe.ownerImage!!)
                    val decodedBytes = Base64.decode(imageString, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
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

    /*fun fetchUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userProfile = apiService.getUserProfileByEmail(Constant.user.email)

                // Handle the successful response
                withContext(Dispatchers.Main) {
                    Constant.userProfile = userProfile

                    val profileImageViewModel = ProfileImageViewModel()
                    profileImageViewModel.fetchProfileImage(userProfile.profilePicture)
                }
            } catch (e: Exception) {
                // Handle failure (e.g., network error, non-200 HTTP status)
                Log.e("API_CALL_FAILURE", "Failed to fetch user profile", e)
            }
        }
    }*/

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val profile = apiService.getUserProfileByEmail(Constant.user.email)
                withContext(Dispatchers.Main) {
                    _userProfile.value = profile
                    Constant.userProfile = profile
                    // Assuming ProfileImageViewModel is also managed by a dependency injection or similar
                    val profileImageViewModel = ProfileImageViewModel()
                    profileImageViewModel.fetchProfileImage(profile.profilePicture)
                }
            } catch (e: Exception) {
                Log.e("API_CALL_FAILURE", "Failed to fetch user profile", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveUser(userProfileDTO: UserProfileDTO, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Making the network call on IO dispatcher
                val response = withContext(Dispatchers.IO) {
                    RetrofitHelper.apiService.saveUser(userProfileDTO)
                }

                // Handling the response after it's received
                onSuccess(response)
            } catch (e: Exception) {
                // If an error occurs, invoke the onError callback
                onError("Failed to create user: ${e.message ?: "Unknown error"}")
            }
        }
    }

    private val _updateResult = MutableStateFlow<Boolean>(false)
    val updateResult: StateFlow<Boolean> get() = _updateResult

    private val _isLoadingChangeImage = MutableStateFlow(false)
    val isLoadingChangeImage: StateFlow<Boolean> get() = _isLoadingChangeImage

    fun changeProfilePicture(file: MultipartBody.Part, userProfileId: Long) {
        if(!_isLoadingChangeImage.value) {
            _isLoadingChangeImage.value = true
            viewModelScope.launch {
                try {
                    apiService.changeProfilePicture(file, userProfileId)
                    _updateResult.value = true
                } catch (e: HttpException) {
                    Log.d("PROFILE PICTURE ERROR:", e.message.orEmpty())
                    _updateResult.value = false
                } catch (e: Exception) {
                    Log.d("PROFILE PICTURE ERROR:", e.message.orEmpty())
                    _updateResult.value = false
                }
                finally {
                    _isLoadingChangeImage.value = false
                }
            }
        }
    }

}
