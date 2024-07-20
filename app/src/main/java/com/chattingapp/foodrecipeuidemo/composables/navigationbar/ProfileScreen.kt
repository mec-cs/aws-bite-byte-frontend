package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.composables.profilepage.ProfileBanner
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.FollowCountsViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileImageViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    val vmFollow = FollowCountsViewModel()
    val vmProfilePic = ProfileImageViewModel()
    val vmRecipePic = RecipeViewModel()
    Column {
        ProfileBanner(vmFollow, vmProfilePic, vmRecipePic)
    }
}