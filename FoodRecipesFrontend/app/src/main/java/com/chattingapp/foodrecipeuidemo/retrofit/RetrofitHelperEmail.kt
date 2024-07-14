package com.chattingapp.foodrecipeuidemo.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory



object RetrofitHelperEmail {
    var gson = GsonBuilder()
    .setLenient()
    .create()
    private const val BASE_URL = "http://192.168.1.100:8765/email-sender/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create()) //important
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val apiService: RetrofitAPIEmail = retrofit.create(RetrofitAPIEmail::class.java)
}