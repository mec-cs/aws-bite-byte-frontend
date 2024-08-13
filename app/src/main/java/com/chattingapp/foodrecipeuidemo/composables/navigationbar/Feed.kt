package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.chattingapp.foodrecipeuidemo.composables.placeholder.PageLoadingPlaceholder
import com.chattingapp.foodrecipeuidemo.composables.recipe.DisplayRecipe
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.FeedViewModel
import kotlinx.coroutines.delay

@Composable
fun Feed(navController: NavHostController) {
    // Collect the followed recipes and isLoading state as state
    val feedViewModel: FeedViewModel = viewModel()

    val followedRecipes by feedViewModel.followedRecipes.collectAsState()
    val isLoadingFollowed by feedViewModel.isLoadingFollowingsRecipes.collectAsState()

    val recommendedRecipes by feedViewModel.recommendedRecipes.collectAsState()
    val isLoadingRecommended by feedViewModel.isLoadingRecommended.collectAsState()

    val cachedRecipes by feedViewModel.cachedRecipes.collectAsState()
    val isLoadingCached by feedViewModel.isLoadingCached.collectAsState()

    val initializationComplete by feedViewModel.initializationComplete.collectAsState()
    val mergeComplete by feedViewModel.mergeComplete.collectAsState()

    var isFirstTimeFetch by rememberSaveable { mutableStateOf(true) }
    var isFirstTimeSetup by rememberSaveable { mutableStateOf(true) }
    var isFirstTimeLoad by rememberSaveable { mutableStateOf(true) }

    // Fetch recipes when the composable is first displayed
    if(isFirstTimeFetch) {
        LaunchedEffect(Unit) {
            if (followedRecipes.isEmpty()) {
                feedViewModel.fetchFollowedRecipes(Constant.userProfile.id)
            }
            if (recommendedRecipes.isEmpty()) {
                feedViewModel.fetchRecommendedRecipes(userId = Constant.userProfile.id)
            }
            if (cachedRecipes.isEmpty()) {
                feedViewModel.fetchCachedRecipes()
            }
            isFirstTimeFetch = false
        }
    }



    if(isLoadingFollowed || isLoadingRecommended || isLoadingCached) {
        PageLoadingPlaceholder()
    }
    else{

        if(isFirstTimeSetup) {
            LaunchedEffect(Unit) {
                if (!initializationComplete) {
                    feedViewModel.initializeCurrentSmallestNumber()
                }
                if (!mergeComplete) {
                    feedViewModel.mergeAndInterleaveRecipes()
                }
                isFirstTimeSetup = false
            }
        }
            if(initializationComplete && mergeComplete){
                // write your code to display the recommended recipes
                val recipes by feedViewModel.recipes.collectAsState()
                val isLoadingRecipes by feedViewModel.isLoadingRecipes.collectAsState()
                val allIdSizeRecipes by feedViewModel.allIdsSize.collectAsState()
                val listStateRecipes = rememberLazyListState()

                if(isFirstTimeLoad) {
                    LaunchedEffect(Unit) {
                        if (recipes.isEmpty()) {
                            feedViewModel.loadMoreRecipes()
                        }
                        isFirstTimeLoad = false
                    }
                }

                if (isLoadingRecipes && recipes.isEmpty()) {
                    PageLoadingPlaceholder()
                }
                else {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight()
                        .padding(16.dp, 32.dp, 16.dp, 0.dp)
                    ) {
                    Text(
                        text = "Feed",
                        fontSize = 24.sp,       // Increase font size
                        fontWeight = FontWeight.Bold,  // Make the text bold
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    LazyColumn(
                        state = listStateRecipes,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        items(recipes) { recipe ->
                            Constant.isFeedScreen = true
                            if(recipe.ownerId != Constant.userProfile.id)
                                DisplayRecipe(recipe = recipe, navController = navController)
                        }

                    }

                    LaunchedEffect(listStateRecipes) {
                        snapshotFlow { listStateRecipes.layoutInfo.visibleItemsInfo.lastOrNull() }
                            .collect { lastVisibleItem ->
                                if (lastVisibleItem != null && lastVisibleItem.index == recipes.size - 1 /*&& allIdSizeClick > recipesClick.size*/) {
                                    feedViewModel.loadMoreRecipes()
                                    Log.d("CALLING API", "FETCHED MORE")
                                    delay(1000)
                                    Log.d("recipes.size", recipes.size.toString())
                                    Log.d("recipes.size", allIdSizeRecipes.toString())
                                }
                            }
                    }
                }
            }
        }


    }



}