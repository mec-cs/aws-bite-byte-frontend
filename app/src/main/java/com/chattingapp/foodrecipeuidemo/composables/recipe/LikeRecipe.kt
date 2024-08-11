package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.Like
import com.chattingapp.foodrecipeuidemo.viewmodel.LikeViewModel

@Composable
fun LikeRecipe(recipeId: Long, likeViewModel: LikeViewModel){



    val isLikeMap by likeViewModel.isLikedMap.collectAsState()
    val isLike = isLikeMap[recipeId] ?: false

    val likeCounts by likeViewModel.likeCounts.collectAsState()
    val likeCount = likeCounts[recipeId]

    val loadingState by likeViewModel.loadingState.collectAsState()
    val isLoading = loadingState[recipeId] ?: false

    /*LaunchedEffect(recipeId) {
        likeViewModel.fetchLikeCounts(recipeId)
    }

    LaunchedEffect(Constant.userProfile.id, recipeId) {
        likeViewModel.checkLike(Constant.userProfile.id, recipeId)
    }*/

    LaunchedEffect(recipeId) {
        likeViewModel.fetchLikeCounts(recipeId)
        likeViewModel.checkLike(Constant.userProfile.id, recipeId)
    }
    // Like Button
    if(isLoading){
        CircularProgressIndicator()
    }
    else{
        val isActionInProgress by likeViewModel.isActionInProgressFlow.collectAsState()

        IconButton(modifier = Modifier
            .size(30.dp) // Adjust the size of the button
            .clip(RoundedCornerShape(8.dp)) // Clip the icon to have rounded corners

            , onClick = {
                val likeClicked = Like(-1, Constant.userProfile.id, recipeId)
                likeViewModel.toggleLike(likeClicked)
            }, enabled = !isActionInProgress) {
            val icon = if (isLike) R.drawable.like_filled else R.drawable.like
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Like",
                modifier = Modifier.size(24.dp), // Adjust the size of the icon
                tint = Color.Unspecified
            )
        }
        if (likeCount != null) {
            Text(
                text = likeCount.likes.toString(),
                fontSize = 15.sp
            )
        }
// placeholder, font, limitations 50-200, recommends


    }

    // Like Count




}