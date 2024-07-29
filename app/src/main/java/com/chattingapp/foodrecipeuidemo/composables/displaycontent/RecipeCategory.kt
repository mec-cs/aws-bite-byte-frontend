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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.composables.recipe.DisplayRecipe
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryClickViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryFavoriteViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryMostLikedViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryUserLikedViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCategory(navController: NavController, cardId: String?) {

    val selectedTab = rememberSaveable  { mutableStateOf(cardId) }
    val recipeViewModelLike = RecipeViewModel()
    val recipeViewModelClick = RecipeViewModel()
    val recipeViewModelFavorite = RecipeViewModel()
    val recipeViewModelUserLike = RecipeViewModel()

    val categoryMostLikedViewModel: CategoryMostLikedViewModel = viewModel()
    val recipesLike by categoryMostLikedViewModel.recipes.collectAsState()
    val isLoadingLike by categoryMostLikedViewModel.isLoading.collectAsState()
    val errorMessageLike by categoryMostLikedViewModel.errorMessage.collectAsState()

    val categoryClickViewModel: CategoryClickViewModel = viewModel()
    val recipesClick by categoryClickViewModel.recipes.collectAsState()
    val isLoadingClick by categoryClickViewModel.isLoading.collectAsState()
    val errorMessageClick by categoryClickViewModel.errorMessage.collectAsState()

    val categoryFavoriteViewModel: CategoryFavoriteViewModel = viewModel()
    val recipeListFavorite by categoryFavoriteViewModel.recipeList.observeAsState(emptyList())
    val isLoadingFavorite by categoryFavoriteViewModel.isLoading.observeAsState(false)
    val isLoadingCountFavorite by categoryFavoriteViewModel.isLoadingCount.observeAsState(false)
    val favoriteCount = categoryFavoriteViewModel.favoriteCount.observeAsState()

    val categoryUserLikeViewModel: CategoryUserLikedViewModel = viewModel()
    val recipeListUserLike by categoryUserLikeViewModel.recipes.collectAsState(emptyList())
    val isLoadingUserLike by categoryUserLikeViewModel.isLoading.collectAsState(false)
    val isLoadingCountUserLike by categoryUserLikeViewModel.isLoading.collectAsState(false)


    val listStateLike = rememberLazyListState()
    val listStateClick = rememberLazyListState()
    val listStateFavorite = rememberLazyListState()
    val listStateUserLike = rememberLazyListState()

    var isLikeFetched by rememberSaveable { mutableStateOf(false) }
    var isClickFetched by rememberSaveable { mutableStateOf(false) }
    var isFavoriteFetched by rememberSaveable { mutableStateOf(false) }
    var isUserLikeFetched by rememberSaveable { mutableStateOf(false) }


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
            if(selectedTab.value != "Favorites"){
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
                        Button(onClick = { selectedTab.value = "Trends" }) {
                            Text("Trends")
                        }
                    }
                }
            }


            when (selectedTab.value) {
                "Most Liked" -> {
                    LaunchedEffect(cardId) {
                        cardId?.let {
                            if(!isLikeFetched){
                                categoryMostLikedViewModel.fetchMostLikedIds()
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
                                Constant.isCardScreen = true
                                DisplayRecipe(recipe = recipe, viewModel = recipeViewModelLike, navController = navController)
                            }

                        }
                        LaunchedEffect(listStateLike) {
                            snapshotFlow { listStateLike.layoutInfo.visibleItemsInfo.lastOrNull() }
                                .collect { lastVisibleItem ->
                                    if (lastVisibleItem != null && lastVisibleItem.index == recipesLike.size - 1) {
                                        categoryMostLikedViewModel.loadMoreRecipes()
                                        Log.d("CALLING API", "FETCHED MORE")
                                        delay(1000)
                                        Log.d("recipes.size", recipesLike.size.toString())
                                        Log.d("recipes.size", recipesLike.toString())
                                    }
                                }
                        }
                    }
                }
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
                                Constant.isCardScreen = true
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
                "Favorites" -> {
                    LaunchedEffect(cardId) {
                        cardId?.let {
                            if(!isFavoriteFetched){
                                categoryFavoriteViewModel.fetchFavoriteCount(Constant.userProfile.id)
                                categoryFavoriteViewModel.fetchRecipes(Constant.userProfile.id)
                                isFavoriteFetched = true
                            }
                        }
                    }
                    if(isLoadingFavorite){
                        CircularProgressIndicator()
                    }
                    else{
                        LazyColumn(
                            state = listStateFavorite,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(recipeListFavorite) { recipe ->
                                Constant.isCardScreen = true
                                DisplayRecipe(recipe = recipe, viewModel = recipeViewModelFavorite, navController = navController)
                            }

                        }
                    }


                    LaunchedEffect(listStateFavorite) {
                        snapshotFlow { listStateFavorite.layoutInfo.visibleItemsInfo.lastOrNull() }
                            .collect { lastVisibleItem ->
                                if(!isLoadingCountFavorite && favoriteCount.value != -1L){
                                    if (lastVisibleItem != null && lastVisibleItem.index >= categoryFavoriteViewModel.recipeListDetail.size - 1 && favoriteCount.value!! > recipeListFavorite.size) {
                                        Log.d("LOAD MORE RECIPES", "ProfileBanner: ")
                                        categoryFavoriteViewModel.loadMoreRecipes(Constant.userProfile.id)
                                        delay(1000)
                                    }
                                }

                            }
                    }

                }
                "Trends" -> {
                    LaunchedEffect(cardId) {
                        cardId?.let {
                            if(!isUserLikeFetched){
                                categoryUserLikeViewModel.fetchMostClickedLastTwoIds()
                                isUserLikeFetched = true
                            }
                        }
                    }
                    if (isLoadingUserLike && recipeListUserLike.isEmpty()) {
                        CircularProgressIndicator()
                    } else {
                        errorMessageLike?.let {
                            Text(text = it)
                        }
                        LazyColumn(
                            state = listStateUserLike,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(recipeListUserLike) { recipe ->
                                Constant.isCardScreen = true
                                DisplayRecipe(recipe = recipe, viewModel = recipeViewModelUserLike, navController = navController)
                            }

                        }
                        LaunchedEffect(listStateUserLike) {
                            snapshotFlow { listStateUserLike.layoutInfo.visibleItemsInfo.lastOrNull() }
                                .collect { lastVisibleItem ->
                                    if (lastVisibleItem != null && lastVisibleItem.index == recipeListUserLike.size - 1) {
                                        categoryUserLikeViewModel.loadMoreRecipes()
                                        Log.d("CALLING API", "FETCHED MORE")
                                        delay(1000)
                                        Log.d("recipes.size", recipeListUserLike.size.toString())
                                        Log.d("recipes.size", recipeListUserLike.toString())
                                    }
                                }
                        }
                    }

                }
            }
        }
    }
}
