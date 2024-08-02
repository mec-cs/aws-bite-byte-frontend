package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class TokenViewModel: ViewModel() {

    fun deleteToken(userId: Long, token: String) {
        viewModelScope.launch {
            try {
                val response: Response<Unit> = RetrofitHelper.apiService.deleteToken(userId, token)
                if (response.isSuccessful) {
                    // Handle successful response
                } else {
                    // Handle unsuccessful response
                }
            } catch (e: HttpException) {
                // Handle HTTP exception
            } catch (e: Exception) {
                // Handle other exceptions
            }
        }
    }

}