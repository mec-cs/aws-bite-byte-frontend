package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.launch

class ClickHistoryViewModel : ViewModel() {
    private val api = RetrofitHelper.apiService

    fun addClick(userId: Long, recipeId: Long) {
        viewModelScope.launch {
            try {
                api.addClick(userId, recipeId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
