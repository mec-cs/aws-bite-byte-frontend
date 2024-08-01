package com.chattingapp.foodrecipeuidemo.entity

import java.time.LocalDateTime

data class UserFollowsResponse(
    val id: UserFollowsId,
    val follower: UserProfile,
    val followed: UserProfile,
    val dateCreated: LocalDateTime
)

data class UserFollowsId(
    val followerId: Long,
    val followedId: Long
)
