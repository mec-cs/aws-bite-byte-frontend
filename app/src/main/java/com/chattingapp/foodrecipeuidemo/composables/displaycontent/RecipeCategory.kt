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
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryLikeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCategory(navController: NavController, cardId: String?) {
    val selectedTab = remember { mutableStateOf(cardId) }
    val recipeViewModel = RecipeViewModel()
    val categoryLikeViewModel: CategoryLikeViewModel = viewModel()
    val recipes by categoryLikeViewModel.recipes.collectAsState()
    val isLoading by categoryLikeViewModel.isLoading.collectAsState()
    val errorMessage by categoryLikeViewModel.errorMessage.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(cardId) {
        cardId?.let {
            categoryLikeViewModel.fetchMostLikedIds()
        }
    }

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
                    if (isLoading && recipes.isEmpty()) {
                        CircularProgressIndicator()
                    } else {
                        errorMessage?.let {
                            Text(text = it)
                        }
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(recipes) { recipe ->

                                DisplayRecipe(recipe = recipe, viewModel = recipeViewModel, navController = navController)
                            }

                        }
                        LaunchedEffect(listState) {
                            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
                                .collect { lastVisibleItem ->
                                    if (lastVisibleItem != null && lastVisibleItem.index == recipes.size - 1) {
                                        categoryLikeViewModel.loadMoreRecipes()
                                        Log.d("CALLING API", "FETCHED MORE")
                                        delay(1000)
                                        Log.d("recipes.size", recipes.size.toString())
                                        Log.d("recipes.size", recipes.toString())
                                    }
                                }
                        }
                    }
                }
                // Handle other tabs as needed
                "Popular" -> {/*PopularRecipes()*/}
                "Favorites" -> {/*FavoriteRecipes()*/}
                "Liked" -> {/*LikedRecipes()*/}
            }
        }
    }
}
