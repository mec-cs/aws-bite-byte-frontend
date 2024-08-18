package com.chattingapp.foodrecipeuidemo.retrofit

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitHelper {

    private const val BASE_URL = "http://[IP-ADDRESS]:8765/"
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

    private const val BASE_URL = "http://[IP-ADDRESS]:5000/"
    private const val API_KEY = "f57d10a3-f19e-4e17-8e77-c058ab937156"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Create an OkHttpClient with an interceptor to add the API key to all requests
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val original: Request = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header("x-api-key", API_KEY) // Add the API key here
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    // Create the Retrofit instance
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient) // Set the OkHttpClient for Retrofit
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // Create the API service instance
    val apiService: RetrofitAPICredentials = retrofit.create(RetrofitAPICredentials::class.java)
}