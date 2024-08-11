package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.FavoriteViewModel

@Composable
fun RecipeUserProfile(
    bm: ImageBitmap,
    username: String,
    recipeId: Long,
    favoriteViewModel: FavoriteViewModel = viewModel() // Use default viewModel if not provided
) {
    // Observe the favorite state from the view model
    val isFavoriteMap by favoriteViewModel.isFavoriteMap.collectAsState()
    val isFavorite = isFavoriteMap[recipeId] ?: false

    // Observe the loading state from the view model
    val loadingState by favoriteViewModel.loadingState.collectAsState()
    val isLoading = loadingState[recipeId] ?: false

    LaunchedEffect(recipeId) {
        // Fetch the favorite state when the recipeId changes
        favoriteViewModel.checkFavorite(Constant.userProfile.id, recipeId)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 10.dp, end = 10.dp, top = 0.dp , start = 0.dp)
            .fillMaxWidth()
    ) {
        Image(
            bitmap = bm,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .padding(end = 10.dp)
        )

        Text(
            text = username,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        if (isLoading) {
            // Show a progress indicator if loading
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .padding(16.dp)
            )
        } else {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorite) Color.Red else Color.Gray,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        if (isFavorite) {
                            // Call method to delete favorite
                            //favoriteViewModel.deleteFavorite(Constant.userProfile.id, recipeId)
                            favoriteViewModel.toggleFavorite(Constant.userProfile.id, recipeId)
                        } else {
                            // Call method to add favorite
                            //favoriteViewModel.addFavorite(Constant.userProfile.id, recipeId)
                            favoriteViewModel.toggleFavorite(Constant.userProfile.id, recipeId)
                        }
                    }
            )
        }
    }
}
