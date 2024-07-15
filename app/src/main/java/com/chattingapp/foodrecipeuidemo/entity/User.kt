package com.chattingapp.foodrecipeuidemo.entity

class User(
    var email: String,
    var password: String,
    var tokens: MutableSet<Token>? = mutableSetOf(),
    var verified: Boolean = false,
    var id: Long = 0L // Add an ID to identify users
) {
    constructor() : this("", "", mutableSetOf(), false)
}
