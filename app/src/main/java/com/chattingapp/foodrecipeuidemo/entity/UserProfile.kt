package com.chattingapp.foodrecipeuidemo.entity

import android.graphics.Bitmap

class UserProfile(
    var username: String,
    var profilePicture: String,
    var id: Long = 0L, // Add an ID to identify users
    var bm: Bitmap? = null

)