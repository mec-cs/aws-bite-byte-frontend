package com.chattingapp.foodrecipeuidemo.entity

data class Recipe(
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
    var ownerId: Long? = null,
    var type: Boolean? = null
) {
    // Additional methods if necessary
}
