package com.chattingapp.foodrecipeuidemo.entity

data class FollowRequest(
    val followerId: Long,
    val followedId: Long
)