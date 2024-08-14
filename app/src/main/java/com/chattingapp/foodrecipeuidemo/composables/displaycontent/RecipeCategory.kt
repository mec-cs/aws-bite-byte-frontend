package com.chattingapp.foodrecipeuidemo.composables.displaycontent

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.composables.placeholder.ErrorPlaceholder
import com.chattingapp.foodrecipeuidemo.composables.placeholder.NoRecipeUserPlaceholder
import com.chattingapp.foodrecipeuidemo.composables.placeholder.PageLoadingPlaceholder
import com.chattingapp.foodrecipeuidemo.composables.recipe.DisplayRecipe
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryClickViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryFavoriteViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryMostLikedViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.CategoryTrendsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCategory(navController: NavController, cardId: String?) {

    val selectedTab = rememberSaveable  { mutableStateOf(cardId) }

    val categoryMostLikedViewModel: CategoryMostLikedViewModel = viewModel()
    val recipesLike by categoryMostLikedViewModel.recipes.collectAsState()
    val isLoadingLike by categoryMostLikedViewModel.isLoading.collectAsState()
    val errorMessageLike by categoryMostLikedViewModel.errorMessage.collectAsState()
    val allIdSizeLike by categoryMostLikedViewModel.allIdsSize.collectAsState()

    val categoryClickViewModel: CategoryClickViewModel = viewModel()
    val recipesClick by categoryClickViewModel.recipes.collectAsState()
    val isLoadingClick by categoryClickViewModel.isLoading.collectAsState()
    val errorMessageClick by categoryClickViewModel.errorMessage.collectAsState()
    val allIdSizeClick by categoryClickViewModel.allIdsSize.collectAsState()

    val categoryFavoriteViewModel: CategoryFavoriteViewModel = viewModel()
    val recipeListFavorite by categoryFavoriteViewModel.recipeList.observeAsState(emptyList())
    val isLoadingFavorite by categoryFavoriteViewModel.isLoading.observeAsState(false)
    val isLoadingCountFavorite by categoryFavoriteViewModel.isLoadingCount.observeAsState(false)
    val errorMessageFavorite by categoryFavoriteViewModel.errorMessage.collectAsState()
    val favoriteCount = categoryFavoriteViewModel.favoriteCount.observeAsState()

    val categoryTrendsViewModel: CategoryTrendsViewModel = viewModel()
    val recipeListTrends by categoryTrendsViewModel.recipes.collectAsState(emptyList())
    val isLoadingTrends by categoryTrendsViewModel.isLoading.collectAsState(false)
    val errorMessageTrends by categoryTrendsViewModel.errorMessage.collectAsState()
    val allIdSizeTrends by categoryTrendsViewModel.allIdsSize.collectAsState()


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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Make the background transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp, 0.dp, 16.dp, 0.dp)
        ) {
            if(selectedTab.value != "Favorites"){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LazyRow {
                        item {
                            Text(
                                text = "Popular",
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable { selectedTab.value = "Popular" },
                                style = if (selectedTab.value == "Popular") {
                                    TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
                                } else {
                                    TextStyle(fontWeight = FontWeight.Normal, color = Color.Gray)
                                }
                            )
                        }
                        item {
                            Text(
                                text = "Most Liked",
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable { selectedTab.value = "Most Liked" },
                                style = if (selectedTab.value == "Most Liked") {
                                    TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
                                } else {
                                    TextStyle(fontWeight = FontWeight.Normal, color = Color.Gray)
                                }
                            )
                        }
                        item {
                            Text(
                                text = "Trends",
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .clickable { selectedTab.value = "Trends" },
                                style = if (selectedTab.value == "Trends") {
                                    TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
                                } else {
                                    TextStyle(fontWeight = FontWeight.Normal, color = Color.Gray)
                                }
                            )
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
                        PageLoadingPlaceholder()
                    } else {
                        errorMessageLike?.let {
                            ErrorPlaceholder()

                        }

                        if(allIdSizeLike == 0){
                            NoRecipeUserPlaceholder()
                        }
                        else{
                            LazyColumn(
                                state = listStateLike,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(recipesLike) { recipe ->
                                    Constant.isCardScreen = true
                                    DisplayRecipe(recipe = recipe, /*viewModel = recipeViewModelLike,*/ navController = navController/*, userProfileViewModelLike*/)
                                }

                            }
                            LaunchedEffect(listStateLike) {
                                snapshotFlow { listStateLike.layoutInfo.visibleItemsInfo.lastOrNull() }
                                    .collect { lastVisibleItem ->
                                        if (lastVisibleItem != null && lastVisibleItem.index == recipesLike.size - 1 && allIdSizeLike > recipesLike.size
                                            && allIdSizeLike >Constant.PAGE_SIZE_CLICK_LIKE) {
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
                        PageLoadingPlaceholder()
                    } else {
                        errorMessageClick?.let {
                            ErrorPlaceholder()

                        }
                        if(allIdSizeClick == 0){
                            NoRecipeUserPlaceholder()
                        }
                        else{
                            LazyColumn(
                                state = listStateClick,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(recipesClick) { recipe ->
                                    Constant.isCardScreen = true
                                    DisplayRecipe(recipe = recipe, /*viewModel = recipeViewModelClick,*/ navController = navController/*, userProfileViewModelClick*/)
                                }

                            }
                            LaunchedEffect(listStateClick) {
                                snapshotFlow { listStateClick.layoutInfo.visibleItemsInfo.lastOrNull() }
                                    .collect { lastVisibleItem ->
                                        if (lastVisibleItem != null && lastVisibleItem.index == recipesClick.size - 1
                                            && allIdSizeClick > recipesClick.size && allIdSizeClick > Constant.PAGE_SIZE_CLICK_LIKE) {
                                            categoryClickViewModel.loadMoreRecipes()
                                            Log.d("CALLING API", "FETCHED MORE")
                                            delay(1000)
                                            Log.d("recipes.size", recipesClick.size.toString())
                                            Log.d("recipes.size", allIdSizeClick.toString())
                                        }
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
                                //categoryFavoriteViewModel.fetchRecipes(Constant.userProfile.id)
                                isFavoriteFetched = true
                            }
                        }
                    }
                    //if(isLoadingFavorite){
                    if(favoriteCount.value == -1L){
                        Image(
                            painter = painterResource(id = R.drawable.yumbyte_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()

                        )
                    }
                    else{
                        errorMessageFavorite?.let {
                            ErrorPlaceholder()
                        }
                        if(favoriteCount.value!! == 0L){
                            NoRecipeUserPlaceholder()
                        }
                        else{
                            LaunchedEffect(cardId) {
                                cardId?.let {
                                    if(recipeListFavorite.isEmpty() && favoriteCount.value != 0L) {
                                        categoryFavoriteViewModel.fetchRecipes(Constant.userProfile.id)
                                    }
                                }

                            }
                            if(isLoadingFavorite){
                                PageLoadingPlaceholder()
                            }
                            else{
                                LazyColumn(
                                    state = listStateFavorite,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(recipeListFavorite) { recipe ->
                                        Constant.isCardScreen = true
                                        DisplayRecipe(recipe = recipe, /*viewModel = recipeViewModelFavorite,*/ navController = navController/*, userProfileViewModelFavorite*/)
                                    }

                                }
                                LaunchedEffect(listStateFavorite) {
                                    snapshotFlow { listStateFavorite.layoutInfo.visibleItemsInfo.lastOrNull() }
                                        .collect { lastVisibleItem ->
                                            if(!isLoadingCountFavorite && favoriteCount.value != -1L){
                                                if (lastVisibleItem != null && lastVisibleItem.index >= categoryFavoriteViewModel.recipeListDetail.size - 1
                                                    && favoriteCount.value!! > recipeListFavorite.size && favoriteCount.value!! > 10) {
                                                    Log.d("LOAD MORE RECIPES", "ProfileBanner: ")
                                                    categoryFavoriteViewModel.loadMoreRecipes(Constant.userProfile.id)
                                                    delay(1000)
                                                }
                                            }

                                        }
                                }
                            }


                        }


                    }




                }
                "Trends" -> {
                    LaunchedEffect(cardId) {
                        cardId?.let {
                            if(!isUserLikeFetched){
                                categoryTrendsViewModel.fetchMostClickedLastTwoIds()
                                isUserLikeFetched = true
                            }
                        }
                    }
                    if (isLoadingTrends && recipeListTrends.isEmpty()) {
                        PageLoadingPlaceholder()
                    } else {
                        errorMessageTrends?.let {
                            ErrorPlaceholder()
                        }
                        if(allIdSizeTrends == 0){
                            NoRecipeUserPlaceholder()
                        }
                        else{
                            LazyColumn(
                                state = listStateUserLike,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(recipeListTrends) { recipe ->
                                    Constant.isCardScreen = true
                                    DisplayRecipe(recipe = recipe, /*viewModel = recipeViewModelUserLike,*/ navController = navController/*, userProfileViewModelUserLike*/)
                                }

                            }
                            LaunchedEffect(listStateUserLike) {
                                snapshotFlow { listStateUserLike.layoutInfo.visibleItemsInfo.lastOrNull() }
                                    .collect { lastVisibleItem ->
                                        if (lastVisibleItem != null && lastVisibleItem.index == recipeListTrends.size - 1
                                            && allIdSizeTrends > recipeListTrends.size && allIdSizeTrends > Constant.PAGE_SIZE_CLICK_LIKE) {
                                            categoryTrendsViewModel.loadMoreRecipes()
                                            Log.d("CALLING API", "FETCHED MORE")
                                            delay(1500)
                                            Log.d("recipes.size", recipeListTrends.size.toString())
                                            Log.d("recipes.size", recipeListTrends.toString())
                                        }
                                    }
                            }
                        }

                    }

                }
            }
        }
    }
}
