package com.chattingapp.foodrecipeuidemo.composables.feednavigator

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.composables.navigationbar.Feed
import com.chattingapp.foodrecipeuidemo.composables.recipe.RecipeDetailScreen

@Composable
fun FeedNavigator() {

    Column {
        val navControllerRecipe = rememberNavController()

        NavHost(navController = navControllerRecipe, startDestination = "feedNavigator") {
            composable("feedNavigator") {
                Feed(navControllerRecipe)
            }
            composable("recipeDetail/{toggleStatus}") { backStackEntry ->
                val toggleStatus = backStackEntry.arguments?.getString("toggleStatus")
                RecipeDetailScreen(navControllerRecipe, toggleStatus!!)
            }

        }
    }
}