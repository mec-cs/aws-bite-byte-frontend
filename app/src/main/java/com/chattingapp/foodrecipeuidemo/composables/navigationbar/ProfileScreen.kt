package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.composables.profilepage.ProfileBanner
import com.chattingapp.foodrecipeuidemo.composables.recipe.RecipeDetailScreen
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.CommentViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.FollowCountsViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.LikeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileImageViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    val vmFollow = FollowCountsViewModel()
    val vmProfilePic = ProfileImageViewModel()
    val vmRecipePic = RecipeViewModel()
    Column {
        val navControllerRecipe = rememberNavController()

        NavHost(navController = navControllerRecipe, startDestination = "profile") {
            composable("profile") {
                //ProfileBanner(viewModel = FollowCountsViewModel(), profileImageViewModel = ProfileImageViewModel(), recipeViewModel = RecipeViewModel(), navControllerRecipe)
                ProfileBanner(vmFollow, vmProfilePic, vmRecipePic, navControllerRecipe)
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
                RecipeDetailScreen(navControllerRecipe, toggleStatus!!)
            }
        }
        //ProfileBanner(vmFollow, vmProfilePic, vmRecipePic)
    }
}