package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.composables.cards.CardsList
import com.chattingapp.foodrecipeuidemo.composables.displaycontent.RecipeCategory
import com.chattingapp.foodrecipeuidemo.composables.recipe.RecipeDetailScreen

@Composable
fun HomeScreen(navController: NavController) {
    // Your home screen UI
    val navControllerCategory = rememberNavController()

    NavHost(navControllerCategory, startDestination = "cardlist") {
        composable("recipeCategory/{cardId}") { backStackEntry ->
            RecipeCategory(navControllerCategory, backStackEntry.arguments?.getString("cardId"))
        }
        composable("recipeDetail/{toggleStatus}") { backStackEntry ->
            RecipeDetailScreen(navControllerCategory, backStackEntry.arguments?.getString("toggleStatus") ?: "Details")
        }
        // Add other destinations if needed
        composable("cardlist") { backStackEntry ->
            CardsList(navControllerCategory)
        }
    }

}


