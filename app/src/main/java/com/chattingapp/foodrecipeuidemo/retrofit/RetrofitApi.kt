package com.chattingapp.foodrecipeuidemo.retrofit

import com.chattingapp.foodrecipeuidemo.entity.AuthenticationDTO
import com.chattingapp.foodrecipeuidemo.entity.FollowCountsDTO
import com.chattingapp.foodrecipeuidemo.entity.Like
import com.chattingapp.foodrecipeuidemo.entity.LikeCountResponse
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.SearchCriteria
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitAPICredentials {

    // CREDENTIALS API
    @POST("credentials/create-user/")
    fun saveUser(@Body userProfileDTO: UserProfileDTO?): Call<String>

    @POST("credentials/check-login-credentials/")
    fun checkLoginCredentials(@Body authenticationDTO: AuthenticationDTO) : Call<User>

    @GET("credentials/get-user-token/")
    fun getUserByToken(@Query("token") token:String) : Call<User>

    @PUT("credentials/verify-email/")
    fun verifyUser(@Query("email") email:String) : Call<Boolean>


    // EMAIL API
    @GET("email-sender/send-verification-code/")
    fun sendVerificationEmail(@Query("email") email:String) : Call<Int>


    // PROFILE API
    @GET("/profile-api/get-user-profile-by-email/")
    fun getUserProfileByEmail(@Query("email") email:String): Call<UserProfile>

    @GET("/profile-api/user/{id}/followers-followings/count")
    fun getFollowersCount(@Path("id") id: Long): Call<FollowCountsDTO>


    // SEARCH API
    @POST("search-profile/search")
    fun getUsersByUsername(@Body searchCriteria: SearchCriteria) : Call<List<UserProfile>>


    // PROFILE-PHOTO DOWNLOADER API
    @POST("/profile-picture-downloader/download/images")
    fun getProfilePicturesList(@Body ppList: List<String>) : Call<List<String>>

    @GET("/profile-picture-downloader/download/{fileName}")
    fun getImage(@Path("fileName") imageName:String): Call<String>

    @GET("/recipe-picture-downloader/download/{fileName}")
    fun getImageRecipe(@Path("fileName") imageName:String): Call<String>


    // RECIPE API

    @GET("profile-recipe/get-recipe/{ownerId}/{page}")
    fun getRecipeDisplay(@Path("ownerId") ownerId: Long, @Path("page") page: Int): Call<List<RecipeProjection>>

    @GET("favorite/check-favorite")
    fun checkFavorite(@Query("userId") userId:Long, @Query("recipeId") recipeId:Long): Call<Boolean>

    @DELETE("favorite/delete/{userId}/{recipeId}")
    fun deleteFavorite(@Path("userId") userId: Long, @Path("recipeId") recipeId: Long): Call<Void>

    @POST("favorite/add")
    fun addFavorite(@Query("userId") userId: Long, @Query("recipeId") recipeId: Long): Call<Boolean>

    @GET("like-dislike/count")
    fun getLikeCounts(@Query("recipeId") recipeId: Long): Call<LikeCountResponse>

    @GET("like-dislike/check-like")
    fun getLike(@Query("recipeId") recipeId: Long, @Query("userId") userId:Long): Call<Like>

    @POST("like-dislike/add-like")
    fun addLike(@Body like:Like): Call<Like>

    @DELETE("like-dislike/remove-like/{recipeId}/{userId}")
    fun deleteLike(@Path("recipeId") recipeId: Long , @Path("userId") userId: Long): Call<Void>
}