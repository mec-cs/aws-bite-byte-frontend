package com.chattingapp.foodrecipeuidemo.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPIEmail {

    @GET("send-verification-code/")
    fun sendVerificationEmail(@Query("email") email:String) : Call<Int>



}