package com.chattingapp.foodrecipeuidemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chattingapp.foodrecipeuidemo.credentials.PasswordUtil
import com.chattingapp.foodrecipeuidemo.entity.ChangePasswordRequest
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ForgotPasswordViewModel : ViewModel() {

    private val _userExists = MutableStateFlow<Boolean?>(null)
    val userExists: StateFlow<Boolean?> = _userExists

    fun checkUserExistsByEmail(email: String) {
        viewModelScope.launch {
            try {
                val exists = RetrofitHelper.apiService.userExistsByEmail(email)
                _userExists.value = exists
            } catch (e: Exception) {
                // Handle the exception if needed, e.g., log it or show a message
                _userExists.value = null
            }
        }
    }

    private val _serverCode = MutableStateFlow<Int?>(-756215454) // Use null to indicate no value yet
    val serverCode: StateFlow<Int?> get() = _serverCode



    fun sendEmail(email: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitHelper.apiService.sendChangePasswordEmail(email)
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

    fun changePassword(email: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val hashedPassword = PasswordUtil.hashPassword(newPassword)
                val request = ChangePasswordRequest(email, hashedPassword)
                val success = RetrofitHelper.apiService.changePassword(request)

                if (success) {
                    onSuccess()
                } else {
                    onError("Failed to change password.")
                }
            } catch (e: Exception) {
                onError("Something went wrong")
            }
        }
    }
}