package com.chattingapp.foodrecipeuidemo.credentials

import android.util.Log
import org.mindrot.jbcrypt.BCrypt

object PasswordUtil {


    fun hashPassword(password: String): String {
        val salt = BCrypt.gensalt()
        Log.d("HASH: ",BCrypt.hashpw(password, salt))
        return BCrypt.hashpw(password, salt)
    }
}
