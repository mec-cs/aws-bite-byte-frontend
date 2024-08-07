package com.chattingapp.foodrecipeuidemo.entity

data class ChangePasswordRequest(
    val email: String,
    val newPassword: String
)