package com.chattingapp.foodrecipeuidemo.emailvalidator

object EmailValidator {
    fun isEmailValid(email:String): Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}