package com.chattingapp.foodrecipeuidemo.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitHelper {

    private const val BASE_URL = "http://192.168.1.100:8765/"
    private const val API_TOKEN = "f57d10a3-f19e-4e17-8e77-c058ab937156"

    // Create an OkHttpClient with an Interceptor to add the API token to requests
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestWithToken = originalRequest.newBuilder()
                .header("Authorization", "Bearer $API_TOKEN")
                .build()
            chain.proceed(requestWithToken)
        }
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)  // Attach OkHttpClient to Retrofit
        .addConverterFactory(ScalarsConverterFactory.create()) // For raw responses
        .addConverterFactory(GsonConverterFactory.create(gson)) // For JSON responses
        .build()

    val apiService: RetrofitAPICredentials = retrofit.create(RetrofitAPICredentials::class.java)
}

object RetrofitHelperRecommendation {
    var gson = GsonBuilder()
        .setLenient()
        .create()
    private const val BASE_URL = "http://192.168.1.100:5000/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create()) //important
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val apiService: RetrofitAPICredentials = retrofit.create(RetrofitAPICredentials::class.java)
}