package com.chattingapp.foodrecipeuidemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class FavoriteViewModel : ViewModel() {

    private val _isFavoriteMap = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val isFavoriteMap: StateFlow<Map<Long, Boolean>> = _isFavoriteMap

    private var isLoading = false

    /*fun checkFavorite(userId: Long, recipeId: Long) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                val response = RetrofitHelper.apiService.checkFavorite(userId, recipeId).await()
                _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap().apply {
                    put(recipeId, response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap().apply {
                    put(recipeId, false)
                }
            } finally {
                isLoading = false
            }
        }
    }*/

    fun checkFavorite(userId: Long, recipeId: Long) {
        // Avoid re-fetching if the status is already known
        if (_isFavoriteMap.value.containsKey(recipeId) || isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                val response = RetrofitHelper.apiService.checkFavorite(userId, recipeId).await()
                _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap().apply {
                    put(recipeId, response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap().apply {
                    put(recipeId, false)
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteFavorite(userId: Long, recipeId: Long) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                RetrofitHelper.apiService.deleteFavorite(userId, recipeId).enqueue(object :
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap().apply {
                                put(recipeId, false)
                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            } finally {
                isLoading = false
            }
        }
    }

    fun addFavorite(userId: Long, recipeId: Long) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                RetrofitHelper.apiService.addFavorite(userId, recipeId).enqueue(object :
                    Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            _isFavoriteMap.value = _isFavoriteMap.value.toMutableMap().apply {
                                put(recipeId, response.body() ?: false)
                            }
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            } finally {
                isLoading = false
            }
        }
    }

}