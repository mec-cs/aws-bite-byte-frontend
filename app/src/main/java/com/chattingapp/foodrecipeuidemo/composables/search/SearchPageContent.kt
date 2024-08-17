package com.chattingapp.foodrecipeuidemo.composables.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.composables.profilepage.SearchUserDisplay
import com.chattingapp.foodrecipeuidemo.composables.recipe.SearchRecipeDisplay
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.SearchRecipeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.SearchUserViewModel

@Composable
fun SearchPageCall(navController: NavController) {
    val selectedTab = rememberSaveable  { mutableStateOf("Recipe") }

    val searchRecipeViewModel: SearchRecipeViewModel = viewModel()
    val searchRecipeQuery by searchRecipeViewModel.searchQuery.collectAsState()
    val searchRecipeResults by searchRecipeViewModel.searchResults.collectAsState()

    val searchUserViewModel: SearchUserViewModel = viewModel()
    val searchUserQuery by searchUserViewModel.searchQuery.collectAsState()
    val searchUserResults by searchUserViewModel.searchResults.collectAsState()

    val listStateRecipe = rememberLazyListState()
    val listStateUser = rememberLazyListState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
    ){
            TextField(
                value = if(selectedTab.value == "Recipe") searchRecipeQuery else searchUserQuery, // Observed state from ViewModel
                onValueChange = { newQuery ->
                    if(selectedTab.value == "Recipe"){
                        searchRecipeViewModel.updateSearchQuery(newQuery) // Update the query in ViewModel
                    }
                    else{
                        searchUserViewModel.updateSearchQuery(newQuery) // Update the query in ViewModel
                    }
                },
                placeholder = {
                    Text(
                        text = if (selectedTab.value == "Recipe") "Search recipe" else "Search user"
                    )
                },
                trailingIcon = {
                    if (searchRecipeQuery.isNotEmpty() && selectedTab.value == "Recipe") {
                        IconButton(onClick = { searchRecipeViewModel.updateSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear text"
                            )
                        }
                    }
                    else if(searchUserQuery.isNotEmpty() && selectedTab.value == "User"){
                        IconButton(onClick = { searchUserViewModel.updateSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear text"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )


            LazyRow {
                item {
                    Text(
                        text = "Recipe",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                selectedTab.value = "Recipe"
                                //viewModel.updateSearchQuery("")
                            },
                        style = if (selectedTab.value == "Recipe") {
                            TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
                        } else {
                            TextStyle(fontWeight = FontWeight.Normal, color = Color.Gray)
                        }
                    )
                }
                item {
                    Text(
                        text = "User",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable {
                                selectedTab.value = "User"
                                //viewModel.updateSearchQuery("")
                            },
                        style = if (selectedTab.value == "User") {
                            TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
                        } else {
                            TextStyle(fontWeight = FontWeight.Normal, color = Color.Gray)
                        }
                    )
                }

            }
        if(selectedTab.value == "Recipe"){
            if(searchRecipeResults.isNotEmpty()){
                LazyColumn(
                    state = listStateRecipe,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchRecipeResults) { recipe ->
                        Constant.isSearchScreen = true
                        SearchRecipeDisplay(recipe, navController)
                    }

                }
            }
        }
        else if(selectedTab.value == "User"){
            if(searchUserResults.isNotEmpty()){
                LazyColumn(
                    state = listStateUser,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchUserResults) { user ->
                        Constant.isSearchScreen = true
                        SearchUserDisplay(user, navController, searchUserViewModel)
                    }

                }
            }
        }



    }
}
