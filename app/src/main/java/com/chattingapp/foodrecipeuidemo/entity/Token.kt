package com.chattingapp.foodrecipeuidemo.entity

class Token(
    var token: String,
    var userId: Long // Store user ID instead of the user instance
) {
    constructor() : this("", 0L)
}
