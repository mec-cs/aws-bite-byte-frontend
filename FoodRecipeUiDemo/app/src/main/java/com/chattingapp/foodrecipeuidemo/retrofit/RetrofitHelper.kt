package com.chattingapp.foodrecipeuidemo.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

var gson = GsonBuilder()
    .setLenient()
    .create()

object RetrofitHelper {
    // Change it if your private IP different than this
    private const val BASE_URL = "http://192.168.1.70:8765"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create()) //important
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val apiService: RetrofitAPI = retrofit.create(RetrofitAPI::class.java)
}