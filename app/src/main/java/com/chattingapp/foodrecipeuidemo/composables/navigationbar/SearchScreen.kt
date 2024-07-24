package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import com.chattingapp.foodrecipeuidemo.composables.search.SearchPageCall
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun SearchScreen(navController: NavHostController) {
    SearchPageCall()
}