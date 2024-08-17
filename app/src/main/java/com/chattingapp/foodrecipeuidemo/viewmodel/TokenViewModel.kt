package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TokenViewModel: ViewModel() {

    fun deleteToken(userId: Long, token: String) {
        viewModelScope.launch {
            try {
                RetrofitHelper.apiService.deleteToken(userId, token)

            } catch (e: HttpException) {
                // Handle HTTP exception
            } catch (e: Exception) {
                // Handle other exceptions
            }
        }
    }

}