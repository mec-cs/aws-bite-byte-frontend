package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.composables.placeholder.SearchRecipePlaceholder
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.RecipeSearchResult
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel

@Composable
fun SearchRecipeDisplay(recipe: RecipeSearchResult, navController: NavController){
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val viewModel: RecipeViewModel = viewModel()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(recipe.id) {
        //Log.d("DisplayRecipe", "Fetching image for recipe: ${recipe.id}")
        viewModel.fetchImage(recipe.image) {
            bitmap = it
            isLoading = false
            //Log.d("DisplayRecipe", "Image fetched for recipe: ${recipe.id}")
        }
    }
    if(!isLoading){
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {

                    // navigate to the Recipe Details Page
                    /*val recipeProObj = RecipeProjection(id = recipe.id, name = recipe.name, description = recipe.description, dateCreated = recipe.dateCreated, image = recipe.image, ownerId = recipe.ownerId, bmRecipe = recipe.bm)
                    val relDate = CalculateDate.formatDateForUser(recipe.dateCreated!!)

                    recipeProObj.relativeDate = relDate

                    Constant.recipeDetailProjection = recipeProObj
                    Constant.isSearchScreen = true*/
                    /*viewModel.fetchRecipeById(recipe.id) { fetchedRecipe ->
                        // Log the fetched recipe data
                        Log.d("RecipeDetails", "Fetched Recipe: $fetchedRecipe")

                        // Navigate to the Recipe Details Page after fetching the data
                        navController.navigate("recipeDetail/${"Details"}")
                    }*/

                    val recipeProObj = RecipeProjection(id = recipe.id, name = recipe.name, description = recipe.description, dateCreated = recipe.dateCreated, image = recipe.image, ownerId = recipe.ownerId, bmRecipe = bitmap)
                    val relDate = CalculateDate.formatDateForUser(recipe.dateCreated)
                    recipeProObj.relativeDate = relDate

                    Constant.recipeDetailProjection = recipeProObj
                    Constant.isSearchScreen = true
                    navController.navigate("recipeDetail/${"Details"}")

                }
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = recipe.name,
                        modifier = Modifier.padding(bottom = 8.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold, // Make the text bold
                            fontSize = 18.sp // You can adjust the font size as needed
                        )
                    )
                    Text(
                        text = "By "+ recipe.ownerUsername,
                        style = TextStyle(
                            fontStyle = FontStyle.Italic, // Make the text italic
                            fontSize = 15.sp, // Set the font size to 15sp
                            color = Color.Gray
                        ),
                    )
                }


            }

        }
    }
    else{
        SearchRecipePlaceholder(recipe)
    }


}