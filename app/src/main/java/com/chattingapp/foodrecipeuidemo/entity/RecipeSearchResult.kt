package com.chattingapp.foodrecipeuidemo.entity

data class RecipeSearchResult(
    val id: Long,
    val name: String,
    val image: String,
    val ownerId: Long,
    val ownerUsername: String,
    val description: String,
    val dateCreated: String
)