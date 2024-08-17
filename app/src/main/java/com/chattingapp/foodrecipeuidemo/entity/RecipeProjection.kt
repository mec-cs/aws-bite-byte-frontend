package com.chattingapp.foodrecipeuidemo.entity

import android.graphics.Bitmap


data class RecipeProjection (
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val dateCreated: String? = null,
    val image: String? = null,
    val ownerId: Long? = null,
    var bmProfile: Bitmap? = null,
    var relativeDate: String? = null,
    var username: String? = null,
    var ownerImage:String? = null
)