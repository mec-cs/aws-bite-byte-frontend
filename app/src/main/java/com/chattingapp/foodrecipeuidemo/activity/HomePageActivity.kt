package com.chattingapp.foodrecipeuidemo.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.Coil
import com.chattingapp.foodrecipeuidemo.activity.ui.theme.MyAppTheme
import com.chattingapp.foodrecipeuidemo.coil.CoilSetup
import com.chattingapp.foodrecipeuidemo.composables.displaycontent.RecipeCategory
import com.chattingapp.foodrecipeuidemo.composables.feednavigator.FeedNavigator
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.AppNavigationBar
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.CreateRecipeScreen
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.HomeScreen
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.ProfileScreen
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.SearchScreen
import com.chattingapp.foodrecipeuidemo.composables.placeholder.PageLoadingPlaceholder
import com.chattingapp.foodrecipeuidemo.composables.placeholder.PublicHealthAnnouncement
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import com.chattingapp.foodrecipeuidemo.theme.FoodRecipeUiDemoTheme
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.UserProfileViewModel

class HomePageActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val imageLoaderProvider = CoilSetup(this)
                    Coil.setImageLoader(imageLoaderProvider.imageLoader)

                    val userProfileViewModel:UserProfileViewModel = viewModel()
                    var isFirstTime by remember { mutableStateOf(true) }
                    val userProfile by userProfileViewModel.userProfile.collectAsState()
                    val isLoading by userProfileViewModel.isLoading.collectAsState()

                    if(isFirstTime) {
                        LaunchedEffect(Unit) {
                            userProfileViewModel.fetchUserProfile()
                            isFirstTime = false
                        }
                    }


                    val navController = rememberNavController()

                    if(!isFirstTime && !isLoading && userProfile != null) {
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
                                    Constant.isFeedScreen = false
                                    HomeScreen(navController)
                                }
                                composable("search") {
                                    Constant.isCardScreen = false
                                    Constant.isProfilePage = false
                                    Constant.isSearchScreen = false
                                    Constant.isFeedScreen = false
                                    Constant.targetUserProfile = null
                                    SearchScreen(navController)
                                }
                                composable("create recipe") {
                                    Constant.isCardScreen = false
                                    Constant.isProfilePage = false
                                    Constant.isSearchScreen = false
                                    Constant.isFeedScreen = false
                                    Constant.targetUserProfile = null
                                    CreateRecipeScreen(navController)
                                }
                                composable("feed") {
                                    Constant.isCardScreen = false
                                    Constant.isProfilePage = false
                                    Constant.isSearchScreen = false
                                    Constant.isFeedScreen = false
                                    Constant.targetUserProfile = null
                                    FeedNavigator(navController = navController)
                                }
                                composable("profile") {
                                    Constant.targetUserProfile = null
                                    Constant.isProfilePage = true
                                    Constant.isCardScreen = false
                                    Constant.isFeedScreen = false
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
                    else{
                        PublicHealthAnnouncement()
                    }

                }
            }




        }
    }
}
