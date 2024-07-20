package com.chattingapp.foodrecipeuidemo.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
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
                    val response = apiService.getImage(profilePictureUrl).execute()
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
            _profileImage.value = response
            if (response != null && Constant.targetUserProfile == null) {
                Constant.userProfile.bm = response
            }
            else if(response != null && Constant.targetUserProfile != null){
                Constant.targetUserProfile!!.bm = response
            }
        }
    }
}
