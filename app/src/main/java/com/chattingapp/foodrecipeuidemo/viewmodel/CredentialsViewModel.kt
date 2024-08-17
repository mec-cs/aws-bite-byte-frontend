package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CredentialsViewModel : ViewModel() {

    private val _verificationResult = MutableStateFlow<Boolean?>(null)
    val verificationResult: StateFlow<Boolean?> get() = _verificationResult

    private val apiService = RetrofitHelper.apiService

    fun verifyUser(email: String) {
        viewModelScope.launch {
            try {
                val result = apiService.verifyUser(email)
                _verificationResult.value = result
            } catch (e: HttpException) {
                Log.d("EMAIL VERIFY ERROR:", e.message.orEmpty())
                _verificationResult.value = false
            } catch (e: Exception) {
                Log.d("EMAIL VERIFY ERROR:", e.message.orEmpty())
                _verificationResult.value = false
            }
        }
    }


    private val _serverCode = MutableStateFlow<Int?>(-756215454) // Use null to indicate no value yet
    val serverCode: StateFlow<Int?> get() = _serverCode



    fun sendEmail(email: String) {
        viewModelScope.launch {
            try {
                val result = apiService.sendVerificationEmail(email)
                _serverCode.value = result
            } catch (e: HttpException) {
                Log.d("EMAIL SEND ERROR:", e.message.orEmpty())
                _serverCode.value = 0 // Handle error case appropriately
            } catch (e: Exception) {
                Log.d("EMAIL SEND ERROR:", e.message.orEmpty())
                _serverCode.value = 0 // Handle error case appropriately
            }
        }
    }

}