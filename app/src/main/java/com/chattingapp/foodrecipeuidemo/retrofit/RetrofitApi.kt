package com.chattingapp.foodrecipeuidemo.retrofit

import com.chattingapp.foodrecipeuidemo.entity.AuthenticationDTO
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection
import com.chattingapp.foodrecipeuidemo.entity.FollowCountsDTO
import com.chattingapp.foodrecipeuidemo.entity.Like
import com.chattingapp.foodrecipeuidemo.entity.LikeCountResponse
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.RecipeSpecificDTO
import com.chattingapp.foodrecipeuidemo.entity.SearchCriteria
import com.chattingapp.foodrecipeuidemo.entity.SearchRecipeDTO
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @POST("search-recipe/search")
    fun getRecipesByNames(@Body searchRecipeDTO: SearchRecipeDTO) : Call<List<Recipe>>


    // PROFILE-PHOTO DOWNLOADER API
    @POST("/profile-picture-downloader/download/images")
    fun getProfilePicturesList(@Body ppList: List<String>) : Call<List<String>>

    @GET("/profile-picture-downloader/download/{fileName}")
    fun getImage(@Path("fileName") imageName:String): Call<String>


    // RECIPE PICTURE DOWNLOADER API
    @GET("/recipe-picture-downloader/download/{fileName}")
    fun getImageRecipe(@Path("fileName") imageName:String): Call<String>

    @POST("/recipe-picture-downloader/download/images")
    fun getRecipeImagesList(@Body recipeList: List<String>) : Call<List<String>>


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
    fun getLike(@Query("recipeId") recipeId: Long, @Query("userId") userId:Long): Call<Boolean>

    @POST("like-dislike/add-like")
    fun addLike(@Body like:Like): Call<Like>

    @DELETE("like-dislike/remove-like/{recipeId}/{userId}")
    fun deleteLike(@Path("recipeId") recipeId: Long , @Path("userId") userId: Long): Call<Void>


    // MANAGE RECIPE API
    @Multipart
    @POST("create-recipe/create-draft")
    fun saveRecipeAsDraft(
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("cuisine") cuisine: RequestBody,
        @Part("course") course: RequestBody,
        @Part("diet") diet: RequestBody,
        @Part("prepTime") prepTime: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("instructions") instructions: RequestBody,
        @Part("image") image: RequestBody,
        @Part("ownerId") ownerId: RequestBody,
        @Part("type") type: RequestBody,
        @Part("isImgChanged") isImgChanged: RequestBody
    ): Call<Recipe>

    @Multipart
    @POST("create-recipe/create-recipe")
    fun createTheRecipe(
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("cuisine") cuisine: RequestBody,
        @Part("course") course: RequestBody,
        @Part("diet") diet: RequestBody,
        @Part("prepTime") prepTime: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("instructions") instructions: RequestBody,
        @Part("image") image: RequestBody,
        @Part("ownerId") ownerId: RequestBody,
        @Part("type") type: RequestBody
    ): Call<Recipe>

    @GET("recipe-getter/specific-fields/{id}")
    suspend fun getRecipeById(@Path("id") id: Long): RecipeSpecificDTO

    @POST("click/add-click")
    suspend fun addClick(@Query("userId") userId: Long, @Query("recipeId") recipeId: Long)

    @GET("comment/count/{recipeId}")
    suspend fun countCommentsByRecipeId(@Path("recipeId") recipeId: Long): Long

    @GET("comment/get-comments/{recipeId}/{page}")
    fun getComments(@Path("recipeId") recipeId: Long, @Path("page") page: Int): Call<List<CommentProjection>>

    @GET("profile-api/get-user-profile/{id}")
    suspend fun getUserProfileById(@Path("id") id: Long): UserProfile
}