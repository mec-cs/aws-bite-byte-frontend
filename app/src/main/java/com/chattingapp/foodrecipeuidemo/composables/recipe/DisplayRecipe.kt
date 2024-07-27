package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.viewmodel.LikeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel

@Composable
fun DisplayRecipe(recipe: RecipeProjection, viewModel: RecipeViewModel, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var username = ""

    val likeViewModel: LikeViewModel = viewModel()

    val isLikeMap by likeViewModel.isLikedMap.collectAsState()
    val isLike = isLikeMap[recipe.id] ?: false

    val relativeDate = recipe.dateCreated?.let { CalculateDate.formatDateForUser(it) }
    LaunchedEffect(recipe.id) {
        viewModel.fetchImage(recipe) {
            bitmap = it
            isLoading = false
        }
    }

    username = if (recipe.ownerId == Constant.userProfile.id) {
        Constant.userProfile.username
    }
    else {
        if(Constant.targetUserProfile != null){
            Constant.targetUserProfile!!.username
        } else{
            // make a api call to get username by recipe owner id

            ""
        }
    }

    Column(modifier = Modifier.padding(bottom = 70.dp)) {
        // Conditional profile display
        var profileBitmap = when {
            Constant.isProfilePage && Constant.targetUserProfile != null -> Constant.targetUserProfile!!.bm
            Constant.userProfile.id == recipe.ownerId -> Constant.userProfile.bm
            else -> null
        }
        if(profileBitmap == null){
            // get profile picture by recipe owner
        }

        profileBitmap?.let {

            RecipeUserProfile(it.asImageBitmap(), username, recipe.id!!)
        }

        Text(
            text = recipe.name ?: "",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        } else {
            bitmap?.let {
                Column(modifier = Modifier.clickable {
                    Constant.recipeDetailProjection = recipe
                    Constant.recipeDetailProjection!!.bmRecipe = bitmap
                    Constant.recipeDetailProjection!!.relativeDate = relativeDate
                    navController.navigate("recipeDetail/${"Details"}")
                }) {

                    Image(
                        bitmap = it.asImageBitmap(),
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
                        LikeRecipe(recipeId = recipe.id!!, likeViewModel)
                        IconButton( modifier = Modifier
                            .size(30.dp) // Adjust the size of the button
                            .clip(RoundedCornerShape(8.dp)),
                            onClick = {
                                Constant.recipeDetailProjection = recipe
                                Constant.recipeDetailProjection!!.bmRecipe = bitmap
                                Constant.recipeDetailProjection!!.relativeDate = relativeDate
                                navController.navigate("recipeDetail/${"Comments"}")


                            }) {
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
    }
}


/*fun formatDateForUser(dateString: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val dateTime: Date
    try {
        dateTime = formatter.parse(dateString) ?: return "Invalid date"
    } catch (e: ParseException) {
        return "Parse error"
    }
    val now = Date()
    val durationMillis = now.time - dateTime.time
    val durationSeconds = durationMillis / 1000
    val durationMinutes = durationSeconds / 60
    val durationHours = durationMinutes / 60
    val durationDays = durationHours / 24
    return when {
        durationDays > 0 -> "${durationDays} day(s) ago"
        durationHours > 0 -> "${durationHours} hour(s) ago"
        durationMinutes > 0 -> "${durationMinutes} minute(s) ago"
        else -> "Just now"
    }
}

*/