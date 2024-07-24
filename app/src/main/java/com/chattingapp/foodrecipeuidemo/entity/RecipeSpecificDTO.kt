package com.chattingapp.foodrecipeuidemo.entity

data class RecipeSpecificDTO(
    val cuisine: String,
    val course: String,
    val diet: String,
    val prepTime: String,
    val ingredients: String,
    val instructions: String
)
