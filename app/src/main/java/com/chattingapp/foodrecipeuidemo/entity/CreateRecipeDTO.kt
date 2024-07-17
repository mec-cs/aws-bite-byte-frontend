package com.chattingapp.foodrecipeuidemo.entity

import java.time.LocalDateTime

data class CreateRecipeDTO(
    var id: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var cuisine: String? = null,
    var course: String? = null,
    var diet: String? = null,
    var prepTime: String? = null,
    var ingredients: String? = null,
    var instructions: String? = null,
    var image: String? = null,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    var ownerId: Long? = null,
    var file: String? = null,  // Using String to represent base64-encoded file
    var type: Boolean? = null,
    var isImgChanged: Boolean? = null
)
