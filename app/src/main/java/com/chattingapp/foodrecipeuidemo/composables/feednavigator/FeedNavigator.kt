package com.chattingapp.foodrecipeuidemo.composables.feednavigator

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.Feed
import com.chattingapp.foodrecipeuidemo.composables.recipe.RecipeDetailScreen
import com.chattingapp.foodrecipeuidemo.viewmodel.FollowCountsViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileImageViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel

@Composable
fun FeedNavigator(navController: NavController) {
    val vmFollow = FollowCountsViewModel()
    val vmProfilePic = ProfileImageViewModel()
    val vmRecipePic = RecipeViewModel()
    Column {
        val navControllerRecipe = rememberNavController()

        NavHost(navController = navControllerRecipe, startDestination = "feedNavigator") {
            composable("feedNavigator") {
                //ProfileBanner(viewModel = FollowCountsViewModel(), profileImageViewModel = ProfileImageViewModel(), recipeViewModel = RecipeViewModel(), navControllerRecipe)
                Feed(navControllerRecipe)
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
                RecipeDetailScreen(navControllerRecipe, toggleStatus!!)
            }

        }
        //ProfileBanner(vmFollow, vmProfilePic, vmRecipePic)
    }
}