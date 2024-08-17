package com.chattingapp.foodrecipeuidemo.retrofit

import com.chattingapp.foodrecipeuidemo.entity.AuthenticationDTO
import com.chattingapp.foodrecipeuidemo.entity.ChangePasswordRequest
import com.chattingapp.foodrecipeuidemo.entity.Comment
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection
import com.chattingapp.foodrecipeuidemo.entity.FollowCountsDTO
import com.chattingapp.foodrecipeuidemo.entity.FollowRequest
import com.chattingapp.foodrecipeuidemo.entity.Like
import com.chattingapp.foodrecipeuidemo.entity.LikeCountResponse
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.RecipeSearchResult
import com.chattingapp.foodrecipeuidemo.entity.RecipeSpecificDTO
import com.chattingapp.foodrecipeuidemo.entity.SearchCriteria
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.entity.UserFollowsResponse
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
    suspend fun saveUser(@Body userProfileDTO: UserProfileDTO?): String

    @POST("credentials/check-login-credentials/")
    suspend fun checkLoginCredentials(@Body authenticationDTO: AuthenticationDTO) : User

    @GET("credentials/get-user-token/")
    suspend fun getUserByToken(@Query("token") token:String) : User

    @PUT("credentials/verify-email/")
    suspend fun verifyUser(@Query("email") email:String) : Boolean

    // EMAIL API
    @GET("email-sender/send-verification-code/")
    suspend fun sendVerificationEmail(@Query("email") email:String) : Int

    // PROFILE API
    @GET("/profile-api/get-user-profile-by-email/")
    suspend fun getUserProfileByEmail(@Query("email") email:String): UserProfile

    @GET("/profile-api/user/{id}/followers-followings/count")
    suspend fun getFollowersCount(@Path("id") id: Long): FollowCountsDTO

    @GET("/profile-picture-downloader/download/{fileName}")
    suspend fun getImage(@Path("fileName") imageName:String): String

    // RECIPE PICTURE DOWNLOADER API
    @GET("/recipe-picture-downloader/download/{fileName}")
    suspend fun getImageRecipe(@Path("fileName") imageName:String): String

    // RECIPE API
    @GET("profile-recipe/get-recipe/{ownerId}/{page}")
    suspend fun getRecipeDisplay(@Path("ownerId") ownerId: Long, @Path("page") page: Int): List<RecipeProjection>

    @GET("favorite/check-favorite")
    suspend fun checkFavorite(@Query("userId") userId:Long, @Query("recipeId") recipeId:Long): Boolean

    @DELETE("favorite/delete/{userId}/{recipeId}")
    suspend fun deleteFavorite(@Path("userId") userId: Long, @Path("recipeId") recipeId: Long): Boolean

    @POST("favorite/add")
    suspend fun addFavorite(@Query("userId") userId: Long, @Query("recipeId") recipeId: Long): Boolean

    @GET("like-dislike/count")
    suspend fun getLikeCounts(@Query("recipeId") recipeId: Long): LikeCountResponse

    @GET("like-dislike/check-like")
    suspend fun getLike(@Query("recipeId") recipeId: Long, @Query("userId") userId:Long): Boolean

    @POST("like-dislike/add-like")
    suspend fun addLike(@Body like:Like): Like

    @DELETE("like-dislike/remove-like/{recipeId}/{userId}")
    suspend fun deleteLike(@Path("recipeId") recipeId: Long , @Path("userId") userId: Long): ResponseBody

    @Multipart
    @POST("create-recipe/create-recipe")
    suspend fun createTheRecipe(
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
    ): Recipe

    @GET("recipe-getter/specific-fields/{id}")
    suspend fun getRecipeSpecificById(@Path("id") id: Long): RecipeSpecificDTO

    @POST("click/add-click")
    suspend fun addClick(@Query("userId") userId: Long, @Query("recipeId") recipeId: Long)

    @GET("comment/count/{recipeId}")
    suspend fun countCommentsByRecipeId(@Path("recipeId") recipeId: Long): Long
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    @GET("comment/get-comments/{recipeId}/{page}")
    suspend fun getComments(@Path("recipeId") recipeId: Long, @Path("page") page: Int): List<CommentProjection>

    @POST("recipe-getter/get-recipes")
    suspend fun getRecipes(@Query("ids") ids: List<Long>): List<RecipeProjection>

    @GET("like-dislike/most-liked-recipes")
    suspend fun getMostLikedRecipes(): List<Long>
// limit number and digits, background colors, loading symbol, food colors dont go beyond 7-8 slides,
    // give expla. about application in the first slide, how the world is (give info)
    // happy path , ss , video, get real time feedback from people and record them
    // market it,
    @GET("click/most-clicked-recipes")
    suspend fun getMostClickedRecipes(): List<Long>

    @GET("favorite/get-recipe/{ownerId}/{page}")
    suspend fun getRecipesFavorite(@Path("ownerId") ownerId: Long, @Path("page") page: Int): List<RecipeProjection>

    @GET("favorite/favorites/count/{userId}")
    suspend fun getFavoriteCount(@Path("userId") userId: Long): Long

    @GET("click/most-clicked/last-two-days")
    suspend fun getMostClickedRecipesLastTwo(): List<Long>

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
    suspend fun getFollowersByUserId(@Path("userId") userId: Long, @Path("page") page: Int): List<UserFollowsResponse>

    @GET("profile-api/users/{userId}/{page}/followings")
    suspend fun getFollowingsByUserId(@Path("userId") userId: Long, @Path("page") page: Int): List<UserFollowsResponse>

    @DELETE("credentials/delete-token")
    suspend fun deleteToken(@Query("userId") userId: Long, @Query("token") token: String): Response<Unit>

    @POST("comment/save-comment")
    suspend fun addComment(@Body comment: Comment): Comment

    @DELETE("comment/delete/{id}")
    suspend fun deleteComment(@Path("id") id: Long): Boolean

    @Multipart
    @POST("profile-api/change-profile-picture")
    suspend fun changeProfilePicture(@Part file: MultipartBody.Part, @Query("userId") userProfileId: Long) // Use Unit instead of Void for suspend functions

    @GET("/profile-recipe/{followerId}/followed-recipes")
    suspend fun getFollowedRecipes(@Path("followerId") followerId: Long): List<Long>

    @GET("recommend")
    suspend fun recommendRecipes(@Query("user_id") userId: Long): List<Long>

    @GET("recipe-getter/get-last-ten-percent-of-recipes")
    suspend fun getCachedRecipes(): List<Long>

    @GET("credentials/exists-by-email/{email}")
    suspend fun userExistsByEmail(@Path("email") email: String): Boolean

    @GET("email-sender/send-change-password-code/")
    suspend fun sendChangePasswordEmail(@Query("email") email:String) : Int

    @POST("credentials/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Boolean

    // SEARCH API
    @POST("search-profile/search")
    fun getUsersByUsername(@Body searchCriteria: SearchCriteria) : Call<List<UserProfile>>

    @POST("search-recipe/search")
    suspend fun searchRecipes(@Query("query") query: String): List<RecipeSearchResult>

}