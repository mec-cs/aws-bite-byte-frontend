package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun SearchScreen(navController: NavHostController) {
    Text(
        text = "Search"
    )
}