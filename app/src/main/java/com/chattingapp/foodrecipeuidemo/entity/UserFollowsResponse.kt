package com.chattingapp.foodrecipeuidemo.entity


data class UserFollowsResponse(
    val id: UserFollowsId,
    val follower: UserProfile,
    val followed: UserProfile,
    val dateCreated: String
)

data class UserFollowsId(
    val followerId: Long,
    val followedId: Long
)
