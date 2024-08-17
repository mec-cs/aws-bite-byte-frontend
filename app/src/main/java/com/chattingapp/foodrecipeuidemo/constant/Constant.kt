package com.chattingapp.foodrecipeuidemo.constant

import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.RecipeSpecificDTO
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.entity.UserProfile

object Constant {
    lateinit var user:User
    lateinit var userProfile:UserProfile
    var targetUserProfile: UserProfile? = null
    var recipeSpecificDTO: RecipeSpecificDTO? = null
    var isProfilePage = false
    const val MAX_DESCRIPTION_SIZE = 60
    const val MAX_COMMENT_SIZE = 60
    const val PAGE_SIZE_CLICK_LIKE = 10
    var recipeDetailProjection: RecipeProjection? = null
    var isSearchScreen = false
    var isCardScreen = false
    var isFeedScreen = false
    var deletedCommentCount = 0
    const val PAGE_SIZE_PROFILE = 20
    const val MINIMUM_PASSWORD_SIZE = 8
    const val RECIPE_IMAGE_URL = "https://bytebite-recipe.s3.eu-central-1.amazonaws.com/"
    const val USER_IMAGE_URL = "https://bytebite-profile.s3.eu-central-1.amazonaws.com/"
}