package com.chattingapp.foodrecipeuidemo.activitiy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.chattingapp.foodrecipeuidemo.activitiy.ui.theme.FoodRecipeUiDemoTheme
import com.chattingapp.foodrecipeuidemo.composables.authorizeuser.ForgotPassword

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodRecipeUiDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ForgotPassword()
                }
            }
        }
    }
}
