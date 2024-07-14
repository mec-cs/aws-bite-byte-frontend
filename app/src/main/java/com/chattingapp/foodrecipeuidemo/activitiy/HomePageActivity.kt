package com.chattingapp.foodrecipeuidemo.activitiy

import android.os.Bundle
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
import com.chattingapp.foodrecipeuidemo.theme.FoodRecipeUiDemoTheme

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

                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = { AppNavigationBar(navController = navController) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") { HomeScreen(navController) }
                            composable("search") { SearchScreen(navController) }
                            composable("create recipe") { CreateRecipeScreen(navController) }
                            composable("feed") { Feed(navController) }
                            composable("profile") { ProfileScreen(navController) }

                            // Add more destinations as needed

                        }
                    }


                }
            }




        }
    }
}
