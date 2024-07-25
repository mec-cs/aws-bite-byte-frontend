package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.ClickHistoryViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.CommentViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(navController: NavController, toggleStatus:String) {
    val recipeViewModel = remember { RecipeViewModel() }
    val commentViewModel = remember { CommentViewModel() }

    val selectedTab = remember { mutableStateOf(toggleStatus) }

    val clickHistoryViewModel = ClickHistoryViewModel()



    LaunchedEffect(Constant.userProfile.id, Constant.recipeDetailProjection!!.id!!) {
        clickHistoryViewModel.addClick(Constant.userProfile.id, Constant.recipeDetailProjection!!.id!!)
    }





    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            TopAppBar(
                title = { Text(text = selectedTab.value) },
                navigationIcon = {
                    IconButton(onClick = {
                        Constant.recipeSpecificDTO = null
                        if (Constant.isSearchScreen) {
                            navController.popBackStack("search", false, true)
                        } else if (Constant.isProfilePage) {
                            navController.popBackStack("profile", false, true)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

                    Row(modifier = Modifier.padding(16.dp)) {
                        // Toggle Buttons
                        ToggleButtons(
                            selectedTab = selectedTab,
                            onSelectTab = { selectedTab.value = it })

                        // Content based on selected tab

                    }



            when (selectedTab.value) {
                "Details" -> RecipeDetailsToggle(recipeViewModel)
                "Comments" -> RecipeCommentsContent(commentViewModel)
            }



        }

    }
}