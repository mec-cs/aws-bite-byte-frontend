package com.chattingapp.foodrecipeuidemo

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.chattingapp.foodrecipeuidemo.activitiy.EmailActivity
import com.chattingapp.foodrecipeuidemo.activitiy.HomePageActivity
import com.chattingapp.foodrecipeuidemo.composables.authorizeuser.LoginPage
import com.chattingapp.foodrecipeuidemo.composables.authorizeuser.SignupPage
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelperCredentials
import com.chattingapp.foodrecipeuidemo.theme.FoodRecipeUiDemoTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodRecipeUiDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authToken = getAuthToken(this)

                    val apiService = RetrofitHelperCredentials.apiService

                    if (authToken != null) {
                        apiService.getUserByToken(authToken).enqueue(object : Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                val user = response.body()
                                if (user != null) {
                                    Constant.user = user
                                    if(user.verified){
                                        navigateToHomePage()
                                    }
                                    else{
                                        navigateToEmailPage()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<User>, t: Throwable) {
                            }
                        })
                    }
                    else{
                        LoginManager()
                    }
                }
            }
        }

    }
    private fun navigateToHomePage() {
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun navigateToEmailPage() {
        val intent = Intent(this, EmailActivity::class.java)
        startActivity(intent)
        finish()
    }
}
@Composable
fun LoginManager() {

    var showLogin by remember { mutableStateOf(true) }


    if (showLogin) {
        LoginPage(onSwitchToSignup = { showLogin = false })
    } else {
        SignupPage(onSwitchToLogin = { showLogin = true })
    }
}

fun getAuthToken(context: Context): String? {
    // Access the SharedPreferences
    val sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)

    // Check if the auth token exists
    if (sharedPreferences.contains("auth_token")) {
        // Retrieve and return the auth token
        return sharedPreferences.getString("auth_token", null)
    }

    // Return null if the auth token doesn't exist
    return null
}

