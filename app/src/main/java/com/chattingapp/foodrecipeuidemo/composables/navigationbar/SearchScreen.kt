package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.composables.follow.FollowsPage
import com.chattingapp.foodrecipeuidemo.composables.profilepage.ProfileBanner
import com.chattingapp.foodrecipeuidemo.composables.recipe.RecipeDetailScreen
import com.chattingapp.foodrecipeuidemo.composables.search.SearchPageCall
import com.chattingapp.foodrecipeuidemo.viewmodel.CommentViewModel

@Composable
fun SearchScreen(navController: NavHostController) {
    Column {
        val navControllerSearch = rememberNavController()

        NavHost(navController = navControllerSearch, startDestination = "search") {
            composable("search") {
                //ProfileBanner(viewModel = FollowCountsViewModel(), profileImageViewModel = ProfileImageViewModel(), recipeViewModel = RecipeViewModel(), navControllerRecipe)
                SearchPageCall(navControllerSearch)
            }
            composable("recipeDetail/{toggleStatus}") { backStackEntry ->
                /*val recipeId = backStackEntry.arguments?.getLong("recipeId")
                val recipeName = backStackEntry.arguments?.getString("recipeName")
                val recipeDescription = backStackEntry.arguments?.getString("recipeDescription")
                val dateCreated = backStackEntry.arguments?.getString("dateCreated")
                val bmRecipe = backStackEntry.arguments?.get("bmRecipe")
                val isFavorite = backStackEntry.arguments?.getBoolean("isFavorite")
                val current = backStackEntry.arguments?.getInt("current")*/
                val toggleStatus = backStackEntry.arguments?.getString("toggleStatus")
                val commentViewModel = CommentViewModel()
                RecipeDetailScreen(navControllerSearch, toggleStatus!!)
            }
            composable("recipeDetail/{toggleStatus}") { backStackEntry ->
                /*val recipeId = backStackEntry.arguments?.getLong("recipeId")
                val recipeName = backStackEntry.arguments?.getString("recipeName")
                val recipeDescription = backStackEntry.arguments?.getString("recipeDescription")
                val dateCreated = backStackEntry.arguments?.getString("dateCreated")
                val bmRecipe = backStackEntry.arguments?.get("bmRecipe")
                val isFavorite = backStackEntry.arguments?.getBoolean("isFavorite")
                val current = backStackEntry.arguments?.getInt("current")*/
                val toggleStatus = backStackEntry.arguments?.getString("toggleStatus")
                val commentViewModel = CommentViewModel()
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