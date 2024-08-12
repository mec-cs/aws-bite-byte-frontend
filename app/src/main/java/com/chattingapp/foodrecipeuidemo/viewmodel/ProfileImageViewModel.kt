package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileImageViewModel : ViewModel() {
    private val _profileImage = MutableLiveData<Bitmap?>()
    val profileImage: LiveData<Bitmap?> get() = _profileImage

    private val apiService = RetrofitHelper.apiService

    fun fetchProfileImage(profilePictureUrl: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    // Directly call the suspend function
                    val imageString = apiService.getImage(profilePictureUrl)
                    val decodedBytes = Base64.decode(imageString, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) {
                    null
                }
            }

            _profileImage.value = response

            if (response != null) {
                if (Constant.targetUserProfile == null) {
                    Constant.userProfile.bm = response
                } else {
                    Constant.targetUserProfile?.bm = response
                }
            }
        }
    }








}
