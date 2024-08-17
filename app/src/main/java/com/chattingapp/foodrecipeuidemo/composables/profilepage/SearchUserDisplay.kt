package com.chattingapp.foodrecipeuidemo.composables.profilepage

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.chattingapp.foodrecipeuidemo.composables.placeholder.SearchUserPlaceholder
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.SearchUserViewModel

@Composable
fun SearchUserDisplay(user: UserProfile, navController: NavController, viewModel: SearchUserViewModel){
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(user.id) {
        //Log.d("DisplayRecipe", "Fetching image for recipe: ${recipe.id}")
        viewModel.fetchImage(user.profilePicture) {
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
                    if(Constant.userProfile.id != user.id){
                        Constant.targetUserProfile = user
                    }

                    Constant.isProfilePage = true

                    navController.navigate("profile")

                },verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
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

                // Use alignBy to center the text vertically with respect to the Image

                    Text(
                        text = user.username,
                        modifier = Modifier.padding(start = 8.dp).weight(1f),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    /*Text(
                        text = user.username,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 8.dp)
                    )*/

            }
        }

    }
    else{
        SearchUserPlaceholder(user)
    }
}