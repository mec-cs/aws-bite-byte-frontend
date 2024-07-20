package com.chattingapp.foodrecipeuidemo.activitiy

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.AppNavigationBar
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.CreateRecipeScreen
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.Feed
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.HomeScreen
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.ProfileScreen
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.SearchScreen
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import com.chattingapp.foodrecipeuidemo.theme.FoodRecipeUiDemoTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodRecipeUiDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val apiService = RetrofitHelper.apiService

                    apiService.getUserProfileByEmail(Constant.user.email).enqueue(object :
                        Callback<UserProfile> {

                        override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                            if (response.isSuccessful) {
                                if (response.body() != null) {
                                    Constant.userProfile = response.body()!!
                                    // Display or process the response body for successful cases
                                    //Log.e("API_CALL_PROFILE", Constant.userProfile.profilePicture)
                                }
                            } else {

                            }
                        }

                        override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                            Log.e("API_CALL_FAILURE", "Failed to create user", t)

                        }
                    })


                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = { AppNavigationBar(navController = navController) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") {
                                Constant.isProfilePage = false
                                Constant.targetUserProfile = null
                                HomeScreen(navController)
                            }
                            composable("search") {
                                Constant.isProfilePage = false
                                Constant.targetUserProfile = null
                                SearchScreen(navController)
                            }
                            composable("create recipe") {
                                Constant.isProfilePage = false
                                Constant.targetUserProfile = null
                                CreateRecipeScreen(navController)
                            }
                            composable("feed") {
                                Constant.isProfilePage = false
                                Constant.targetUserProfile = null
                                Feed(navController)
                            }
                            composable("profile") {
                                Constant.targetUserProfile = null
                                Constant.isProfilePage = true
                                ProfileScreen(navController)
                            }

                            // Add more destinations as needed

                        }
                    }


                }
            }




        }
    }
}
