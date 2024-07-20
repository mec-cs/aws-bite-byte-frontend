package com.chattingapp.foodrecipeuidemo.constant

import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.entity.UserProfile

object Constant {
    lateinit var user:User
    lateinit var userProfile:UserProfile
    var targetUserProfile: UserProfile? = null
    var isProfilePage = false
    var MAX_TEXT_SIZE = 60
}