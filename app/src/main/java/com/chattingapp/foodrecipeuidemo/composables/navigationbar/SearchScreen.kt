package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.composables.follow.FollowsPage
import com.chattingapp.foodrecipeuidemo.composables.profilepage.ProfileBanner
import com.chattingapp.foodrecipeuidemo.composables.recipe.RecipeDetailScreen
import com.chattingapp.foodrecipeuidemo.composables.search.SearchPageCall

@Composable
fun SearchScreen() {
    Column {
        val navControllerSearch = rememberNavController()

        NavHost(navController = navControllerSearch, startDestination = "search") {
            composable("search") {
                SearchPageCall(navControllerSearch)
            }
            composable("recipeDetail/{toggleStatus}") { backStackEntry ->

                val toggleStatus = backStackEntry.arguments?.getString("toggleStatus")
                RecipeDetailScreen(navControllerSearch, toggleStatus!!)
            }
            composable("recipeDetail/{toggleStatus}") { backStackEntry ->
                val toggleStatus = backStackEntry.arguments?.getString("toggleStatus")
                RecipeDetailScreen(navControllerSearch, toggleStatus!!)
            }
            composable("profileFollows/{followType}/{followerCount}/{followingCount}") { backStackEntry ->
                val followType = backStackEntry.arguments?.getString("followType")
                val followerCount = backStackEntry.arguments?.getString("followerCount")
                val followingCount = backStackEntry.arguments?.getString("followingCount")


                FollowsPage(navControllerSearch, followType, followerCount, followingCount)
            }
            composable("profile") {
                ProfileBanner(navControllerSearch)
            }
        }

    }
}