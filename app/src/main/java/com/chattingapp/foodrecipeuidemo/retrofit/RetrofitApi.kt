package com.chattingapp.foodrecipeuidemo.retrofit

import com.chattingapp.foodrecipeuidemo.entity.SearchCriteria
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitAPI {


    // CREDENTIALS API
    @POST("/credentials/create-user/")
    fun saveUser(@Body userProfileDTO: UserProfileDTO?): Call<String>

    @POST("/credentials/check-login-credentials/")
    fun checkLoginCredentials(@Body user: User) : Call<User>

    @GET("/credentials/get-user-token/")
    fun getUserByToken(@Query("token") token:String) : Call<User>



    // SEARCH API
    @POST("/search-profile/search")
    fun getUsersByUsername(@Body searchCriteria: SearchCriteria) : Call<List<UserProfile>>



    // PROFILE-PHOTO DOWNLOADER API
    @POST("/profile-picture-downloader/download/images")
    fun getProfilePicturesList(@Body ppList: List<String>) : Call<List<String>>


    }