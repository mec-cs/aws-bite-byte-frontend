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
    val MAX_DESCRIPTION_SIZE = 60
    val MAX_COMMENT_SIZE = 30
    val PAGE_SIZE_CLICK_LIKE = 10
    var recipeDetailProjection: RecipeProjection? = null
    var isSearchScreen = false
    var isCardScreen = false
    var isFeedScreen = false
    const val CREATE_ERROR_DIALOG = "Please fill the necessary parts!"
    const val NULL_EXCEPTION_ERROR = "Invalid request, please fill crucial parts!"
    var deletedCommentCount = 0
    val PAGE_SIZE_PROFILE = 20

}