package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.CommentViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel

@Composable
fun RecipeDetailsToggle(recipeViewModel: RecipeViewModel){


    val recipe by recipeViewModel.recipe
    val isLoading by recipeViewModel.isLoadingRecipe

    LaunchedEffect(Constant.recipeDetailProjection?.id) {
        Constant.recipeDetailProjection?.id?.let { id ->
            if (recipe == null) { // Fetch only if recipe is not already loaded
                recipeViewModel.fetchRecipeById(id)
            }
        }
    }



    if (isLoading) {
        Image(
            painter = painterResource(id = R.drawable.yumbyte_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()

        )
    } else {
        recipe?.let {

            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ){

                item {
                    Text(text = Constant.recipeDetailProjection!!.name!!,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                    )

                    Text(text = Constant.recipeDetailProjection!!.description!!,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                    )
                    AsyncImage(
                        model = "${Constant.RECIPE_IMAGE_URL}${Constant.recipeDetailProjection!!.image}",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Text(text = Constant.recipeDetailProjection!!.relativeDate!!,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp))
                }


                item {
                    RecipeDetail(fieldName = "Cuisine", detail = recipe!!.cuisine)
                }
                item {
                    RecipeDetail(fieldName = "Course", detail = recipe!!.course)
                }
                item {
                    RecipeDetail(fieldName = "Diet", detail = recipe!!.diet)
                }
                item {
                    RecipeDetail(fieldName = "Prep Time in Minutes", detail = recipe!!.prepTime)
                }
                item {
                    RecipeDetail(fieldName = "Ingredients", detail = recipe!!.ingredients)
                }
                item {
                    RecipeDetail(fieldName = "Instructions", detail = recipe!!.instructions)
                }

            }


            // Display the recipe details

        } ?: run {
            // Display error message
            Text("Error loading recipe")
        }
    }
}