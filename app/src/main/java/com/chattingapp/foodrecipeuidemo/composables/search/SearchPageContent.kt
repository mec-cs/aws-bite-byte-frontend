package com.chattingapp.foodrecipeuidemo.composables.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.chattingapp.foodrecipeuidemo.composables.recipe.DisplayRecipe
import com.chattingapp.foodrecipeuidemo.composables.recipe.SearchRecipeDisplay
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.SearchViewModel

@Composable
fun SearchPageCall(navController: NavController) {
    val selectedTab = rememberSaveable  { mutableStateOf("Recipe") }

    val viewModel: SearchViewModel = viewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
    ){
            TextField(
                value = searchQuery, // Observed state from ViewModel
                onValueChange = { newQuery ->
                    viewModel.updateSearchQuery(newQuery) // Update the query in ViewModel
                },
                placeholder = {
                    Text(
                        text = if (selectedTab.value == "Recipe") "Search recipe" else "Search user"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
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
                                viewModel.updateSearchQuery("")
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
                                viewModel.updateSearchQuery("")
                                       },
                        style = if (selectedTab.value == "User") {
                            TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
                        } else {
                            TextStyle(fontWeight = FontWeight.Normal, color = Color.Gray)
                        }
                    )
                }

            }

            if(searchResults.isNotEmpty()){
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { recipe ->
                        Constant.isSearchScreen = true
                        SearchRecipeDisplay(recipe, navController)
                    }

                }
            }


    }
}
