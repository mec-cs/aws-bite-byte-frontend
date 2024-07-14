package com.chattingapp.foodrecipeuidemo.entity

class User(
    var email: String,
    var password: String,
    var token: String?,
    var verified: Boolean = false
) {

    // Secondary constructor with email, password, and verified flag
    constructor(email: String, password: String, verified: Boolean) : this(email, password, null, verified)



    // Secondary constructor with default values
    constructor() : this("", "", null, false)

    override fun toString(): String {
        return "User(email='$email', password='$password', token=$token, verified=$verified)"
    }


}
