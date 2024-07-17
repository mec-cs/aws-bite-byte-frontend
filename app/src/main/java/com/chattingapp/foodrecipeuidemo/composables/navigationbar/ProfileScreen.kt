package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.composables.profilepage.ProfileBanner

@Composable
fun ProfileScreen(navController: NavController) {
    ProfileBanner()

}