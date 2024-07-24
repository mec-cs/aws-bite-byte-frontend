package com.chattingapp.foodrecipeuidemo.entity

import android.graphics.Bitmap
import com.chattingapp.foodrecipeuidemo.viewmodel.LikeViewModel
import java.time.LocalDateTime


data class RecipeProjection (
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val dateCreated: String? = null,
    val image: String? = null,
    val ownerId: Long? = null,
    var bmProfile: Bitmap? = null,
    var bmRecipe: Bitmap? = null,
    var relativeDate: String? = null


)