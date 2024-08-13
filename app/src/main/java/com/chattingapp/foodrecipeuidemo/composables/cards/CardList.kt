package com.chattingapp.foodrecipeuidemo.composables.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun CardsList(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 16.dp, 16.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CardViewMostPopularRecipes(navController, "Popular")
        }
        item {
            CardViewMostLikedRecipes(navController, "Most Liked")
        }
        item {
            CardViewTrendsRecipes(navController, "Trends")
        }
        item {
            CardViewFavoriteRecipes(navController, "Favorites")
        }

    }
}