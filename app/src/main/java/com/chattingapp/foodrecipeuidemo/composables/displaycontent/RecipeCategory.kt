package com.chattingapp.foodrecipeuidemo.composables.displaycontent

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.composables.recipe.DisplayRecipe
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryClickViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryLikeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCategory(navController: NavController, cardId: String?) {
    val selectedTab = remember { mutableStateOf(cardId) }
    val recipeViewModelLike = RecipeViewModel()
    val recipeViewModelClick = RecipeViewModel()
    val categoryLikeViewModel: CategoryLikeViewModel = viewModel()
    val recipesLike by categoryLikeViewModel.recipes.collectAsState()
    val isLoadingLike by categoryLikeViewModel.isLoading.collectAsState()
    val errorMessageLike by categoryLikeViewModel.errorMessage.collectAsState()

    val categoryClickViewModel: CategoryClickViewModel = viewModel()
    val recipesClick by categoryClickViewModel.recipes.collectAsState()
    val isLoadingClick by categoryClickViewModel.isLoading.collectAsState()
    val errorMessageClick by categoryClickViewModel.errorMessage.collectAsState()

    val listStateLike = rememberLazyListState()
    val listStateClick = rememberLazyListState()

    var isLikeFetched = false
    var isClickFetched = false



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${selectedTab.value}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyRow {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { selectedTab.value = "Popular" }) {
                        Text("Popular")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { selectedTab.value = "Most Liked" }) {
                        Text("Most Liked")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { selectedTab.value = "Favorites" }) {
                        Text("Favorites")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { selectedTab.value = "Liked" }) {
                        Text("Liked")
                    }
                }
            }

            when (selectedTab.value) {
                "Most Liked" -> {
                    LaunchedEffect(cardId) {
                        cardId?.let {
                            if(!isLikeFetched){
                                categoryLikeViewModel.fetchMostLikedIds()
                                isLikeFetched = true
                            }
                        }
                    }
                    if (isLoadingLike && recipesLike.isEmpty()) {
                        CircularProgressIndicator()
                    } else {
                        errorMessageLike?.let {
                            Text(text = it)
                        }
                        LazyColumn(
                            state = listStateLike,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(recipesLike) { recipe ->

                                DisplayRecipe(recipe = recipe, viewModel = recipeViewModelLike, navController = navController)
                            }

                        }
                        LaunchedEffect(listStateLike) {
                            snapshotFlow { listStateLike.layoutInfo.visibleItemsInfo.lastOrNull() }
                                .collect { lastVisibleItem ->
                                    if (lastVisibleItem != null && lastVisibleItem.index == recipesLike.size - 1) {
                                        categoryLikeViewModel.loadMoreRecipes()
                                        Log.d("CALLING API", "FETCHED MORE")
                                        delay(1000)
                                        Log.d("recipes.size", recipesLike.size.toString())
                                        Log.d("recipes.size", recipesLike.toString())
                                    }
                                }
                        }
                    }
                }
                // Handle other tabs as needed
                "Popular" -> {
                    LaunchedEffect(cardId) {
                        cardId?.let {
                            if(!isClickFetched){
                                categoryClickViewModel.fetchMostLikedIds()
                                isClickFetched = true
                            }
                        }
                    }
                    if (isLoadingClick && recipesClick.isEmpty()) {
                        CircularProgressIndicator()
                    } else {
                        errorMessageClick?.let {
                            Text(text = it)
                        }
                        LazyColumn(
                            state = listStateClick,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(recipesClick) { recipe ->

                                DisplayRecipe(recipe = recipe, viewModel = recipeViewModelClick, navController = navController)
                            }

                        }
                        LaunchedEffect(listStateClick) {
                            snapshotFlow { listStateClick.layoutInfo.visibleItemsInfo.lastOrNull() }
                                .collect { lastVisibleItem ->
                                    if (lastVisibleItem != null && lastVisibleItem.index == recipesClick.size - 1) {
                                        categoryClickViewModel.loadMoreRecipes()
                                        Log.d("CALLING API", "FETCHED MORE")
                                        delay(1000)
                                        Log.d("recipes.size", recipesClick.size.toString())
                                        Log.d("recipes.size", recipesClick.toString())
                                    }
                                }
                        }
                    }

                }
                "Favorites" -> {/*FavoriteRecipes()*/}
                "Liked" -> {/*LikedRecipes()*/}
            }
        }
    }
}
