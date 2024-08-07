package com.chattingapp.foodrecipeuidemo.activitiy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
