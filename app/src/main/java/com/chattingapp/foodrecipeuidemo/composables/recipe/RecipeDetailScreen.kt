package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(navController: NavController, recipeId: String?, recipeViewModel: RecipeViewModel) {
    /*if(recipeViewModel.page > 0 && recipeViewModel.listSize>10){
        Log.d("IF STATEMENT: ", "HERE")
        recipeViewModel.page = recipeViewModel.listSize/10 -1
    }*/

    Box(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = "Recipe Details") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack("profile", false, true) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )


    }
}