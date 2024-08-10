package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.FeedViewModel

@Composable
fun Feed(navController: NavHostController) {
    // Collect the followed recipes and isLoading state as state
    val feedViewModel:FeedViewModel = viewModel()

    val followedRecipes by feedViewModel.followedRecipes.observeAsState(emptyList())
    val isLoadingFollowed by feedViewModel.isLoading.observeAsState(false)

    val recommendedRecipes by feedViewModel.recommendedRecipes.observeAsState(emptyList())
    val isLoadingRecommended by feedViewModel.isLoadingRecommended.observeAsState(false)

    val cachedRecipes by feedViewModel.cachedRecipes.observeAsState(emptyList())
    val isLoadingCached by feedViewModel.isLoadingCached.observeAsState(false)
    

    // Fetch recipes when the composable is first displayed
    LaunchedEffect(Constant.userProfile.id) {
        feedViewModel.fetchRecommendedRecipes(userId = Constant.userProfile.id) // Replace with actual userId if needed
    }

    // Call the API when the composable is first composed
    LaunchedEffect(Constant.userProfile.id) {
        feedViewModel.fetchFollowedRecipes(Constant.userProfile.id)
    }

    // Call the API when the composable is first composed
    LaunchedEffect(Unit) {
        feedViewModel.fetchCachedRecipes()
    }

    if(isLoadingFollowed || isLoadingRecommended || isLoadingCached) {
        CircularProgressIndicator()
    }
    else{
        // Loading completed
        //Text(text = "feed")
        Log.d("FEED PAGE", followedRecipes.size.toString())
        Log.d("FEED PAGE", recommendedRecipes.size.toString())
        Log.d("FEED PAGE", cachedRecipes.size.toString())
    }



}