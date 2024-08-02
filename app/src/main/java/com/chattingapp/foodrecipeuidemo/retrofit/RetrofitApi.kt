package com.chattingapp.foodrecipeuidemo.retrofit

import com.chattingapp.foodrecipeuidemo.entity.AuthenticationDTO
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection
import com.chattingapp.foodrecipeuidemo.entity.FollowCountsDTO
import com.chattingapp.foodrecipeuidemo.entity.FollowRequest
import com.chattingapp.foodrecipeuidemo.entity.Like
import com.chattingapp.foodrecipeuidemo.entity.LikeCountResponse
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.RecipeSpecificDTO
import com.chattingapp.foodrecipeuidemo.entity.SearchCriteria
import com.chattingapp.foodrecipeuidemo.entity.SearchRecipeDTO
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.entity.UserFollowsResponse
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import com.chattingapp.foodrecipeuidemo.entity.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
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
    fun getUserProfileById(@Path("id") id: Long): Call<UserProfile>

    @POST("recipe-getter/get-recipes")
    suspend fun getRecipes(@Query("ids") ids: List<Long>): List<RecipeProjection>

    @GET("like-dislike/most-liked-recipes")
    fun getMostLikedRecipes(): Call<List<Long>>

    @GET("click/most-clicked-recipes")
    fun getMostClickedRecipes(): Call<List<Long>>

    @GET("favorite/get-recipe/{ownerId}/{page}")
    fun getRecipesFavorite(@Path("ownerId") ownerId: Long, @Path("page") page: Int): Call<List<RecipeProjection>>

    @GET("favorite/favorites/count/{userId}")
    fun getFavoriteCount(@Path("userId") userId: Long): Call<Long>

    @GET("like-dislike/likes/count/{userId}")
    fun getLikeCountByUserId(@Path("userId") userId: Long): Call<Long>

    @GET("like-dislike/get-recipe/{ownerId}/{page}")
    fun getRecipesLike(@Path("ownerId") ownerId: Long, @Path("page") page: Int): Call<List<RecipeProjection>>

    @GET("click/most-clicked/last-two-days")
    fun getMostClickedRecipesLastTwo(): Call<List<Long>>

    @GET("profile-getter/{id}")
    fun getUserProfile(@Path("id") id: Long): Call<UserProfileResponse>

    @GET("profile-api/check-follow")
    suspend fun checkIfUserFollows(
        @Query("followerId") followerId: Long,
        @Query("followedId") followedId: Long
    ): Boolean

    @POST("profile-api/add-user-follows")
    suspend fun addUserFollows(@Body followRequest: FollowRequest): Response<String>

    @POST("profile-api/remove-user-follows")
    suspend fun removeUserFollows(@Body followRequest: FollowRequest): Response<String>

    @GET("profile-api/users/{userId}/{page}/followers")
    fun getFollowersByUserId(@Path("userId") userId: Long, @Path("page") page: Int): Call<List<UserFollowsResponse>>

    @GET("profile-api/users/{userId}/{page}/followings")
    fun getFollowingsByUserId(@Path("userId") userId: Long, @Path("page") page: Int): Call<List<UserFollowsResponse>>

    @DELETE("credentials/delete-token")
    suspend fun deleteToken(@Query("userId") userId: Long, @Query("token") token: String): Response<Unit>
}