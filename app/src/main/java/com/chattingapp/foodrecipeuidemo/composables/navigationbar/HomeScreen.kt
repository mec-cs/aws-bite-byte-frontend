package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.composables.cards.CardsList

@Composable
fun HomeScreen(navController: NavController) {
    // Your home screen UI
    CardsList()
}


