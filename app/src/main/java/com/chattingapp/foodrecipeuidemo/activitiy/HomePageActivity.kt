package com.chattingapp.foodrecipeuidemo.activitiy

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.composables.displaycontent.RecipeCategory
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
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileImageViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

                                    val profileImageViewModel = ProfileImageViewModel()
                                    profileImageViewModel.fetchProfileImage(Constant.userProfile.profilePicture)

                                }
                            } else {
                                Log.d("API_CALL UnSuccessfull", "Non HTTP 200 error")
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
                                Constant.targetUserProfile = null
                                Constant.isCardScreen = false
                                Constant.isProfilePage = false
                                Constant.isSearchScreen = false
                                HomeScreen(navController)
                            }
                            composable("search") {
                                Constant.isCardScreen = false
                                Constant.isProfilePage = false
                                Constant.isSearchScreen = false
                                Constant.targetUserProfile = null
                                SearchScreen(navController)
                            }
                            composable("create recipe") {
                                Constant.isCardScreen = false
                                Constant.isProfilePage = false
                                Constant.isSearchScreen = false
                                Constant.targetUserProfile = null
                                val recipeViewModel = RecipeViewModel()
                                CreateRecipeScreen(navController)
                            }
                            composable("feed") {
                                Constant.isCardScreen = false
                                Constant.isProfilePage = false
                                Constant.isSearchScreen = false
                                Constant.targetUserProfile = null
                                Feed(navController)
                            }
                            composable("profile") {
                                Constant.targetUserProfile = null
                                Constant.isProfilePage = true
                                Constant.isCardScreen = false
                                Constant.isProfilePage = false
                                Constant.isSearchScreen = false

                                ProfileScreen(navController)
                            }
                            composable("recipeCategory/{cardId}") { backStackEntry ->
                                val cardId = backStackEntry.arguments?.getString("cardId")
                                RecipeCategory(navController, cardId)
                            }



                        }


                    }

                }
            }




        }
    }
}
