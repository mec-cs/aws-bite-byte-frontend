package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.viewmodel.FavoriteViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.LikeViewModel

@Composable
fun DisplayRecipe(
    recipe: RecipeProjection,

    navController: NavController

) {

    var expanded by remember { mutableStateOf(false) }

    val likeViewModel: LikeViewModel = viewModel()

    val favoriteViewModel: FavoriteViewModel = viewModel()

    val relativeDate = recipe.dateCreated?.let { CalculateDate.formatDateForUser(it) }


    Column(modifier = Modifier.padding(bottom = 70.dp)) {
            if(!Constant.isProfilePage){
                RecipeUserProfile(recipe.ownerImage!!, recipe.username!!, recipe.id!!, favoriteViewModel)
            }

            RecipeNameBar(recipe.name!!, recipe.id!!, favoriteViewModel)


            //Log.d("DisplayRecipe", "Recipe image loaded for recipe: ${recipe.id}")
                Column(modifier = Modifier.clickable {
                    Constant.recipeDetailProjection = recipe
                    Constant.recipeDetailProjection!!.relativeDate = relativeDate
                    navController.navigate("recipeDetail/${"Details"}")
                }) {
                    AsyncImage(
                        model = "${Constant.RECIPE_IMAGE_URL}${recipe.image}",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LikeRecipe(recipeId = recipe.id, likeViewModel)
                        IconButton(
                            modifier = Modifier
                                .size(30.dp) // Adjust the size of the button
                                .clip(RoundedCornerShape(8.dp)),
                            onClick = {
                                Constant.recipeDetailProjection = recipe
                                Constant.recipeDetailProjection!!.relativeDate = relativeDate
                                navController.navigate("recipeDetail/${"Comments"}")
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.comment),
                                contentDescription = "Comment",
                                modifier = Modifier.size(24.dp), // Adjust the size of the icon
                                tint = Color.Unspecified
                            )
                        }
                    }

                    val description = recipe.description ?: ""
                    val truncatedDescription = if (description.length > Constant.MAX_DESCRIPTION_SIZE) {
                        description.take(Constant.MAX_DESCRIPTION_SIZE) + "..."
                    } else {
                        description
                    }

                    Text(
                        text = if (expanded) description else truncatedDescription,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (description.length > Constant.MAX_DESCRIPTION_SIZE) {
                        Text(
                            text = if (expanded) "See less" else "See more",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    }

                    relativeDate?.let {
                        Text(text = it, fontSize = 12.sp)
                    }
                }


    }
}
