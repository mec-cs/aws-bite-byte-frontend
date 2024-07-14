package com.chattingapp.foodrecipeuidemo.retrofit

import com.chattingapp.foodrecipeuidemo.entity.AuthenticationDTO
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface RetrofitAPICredentials {
    @POST("create-user/")
    fun saveUser(@Body userProfileDTO: UserProfileDTO?): Call<String>

    @POST("check-login-credentials/")
    fun checkLoginCredentials(@Body authenticationDTO: AuthenticationDTO) : Call<User>

    @GET("get-user-token/")
    fun getUserByToken(@Query("token") token:String) : Call<User>

    @PUT("verify-email/")
    fun verifyUser(@Query("email") email:String) : Call<Boolean>

}