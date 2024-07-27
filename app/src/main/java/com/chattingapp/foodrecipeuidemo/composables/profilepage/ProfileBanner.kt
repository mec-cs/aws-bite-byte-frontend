package com.chattingapp.foodrecipeuidemo.composables.profilepage

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.composables.recipe.DisplayRecipe
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.viewmodel.FollowCountsViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileImageViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfileBanner(viewModel: FollowCountsViewModel, profileImageViewModel: ProfileImageViewModel, recipeViewModel: RecipeViewModel, navController: NavController) {
    val recipeList by recipeViewModel.recipeList.observeAsState(emptyList())

    var userProfile: UserProfile

    var isFirstTime by remember { mutableStateOf(true) }

    if(Constant.targetUserProfile != null){
        userProfile = Constant.targetUserProfile!!
        Log.d("PROFILE IMAGE: ", userProfile.profilePicture)

    }
    else{
        userProfile = Constant.userProfile
    }

    if(isFirstTime){
        LaunchedEffect(userProfile.id) {
            viewModel.fetchFollowersCount(userProfile.id)
            recipeViewModel.fetchRecipes(userProfile.id)
        }
        isFirstTime = false
    }


    val followCounts by viewModel.followCounts.observeAsState()
    var displayProfileImage by remember { mutableStateOf(false) }



    Column {
        Text(
            userProfile.username,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            painterResource(id = R.drawable.ic_launcher_background)
            if (userProfile.bm == null) {
                LaunchedEffect(userProfile.profilePicture) {
                    profileImageViewModel.fetchProfileImage(userProfile.profilePicture)
                }

                val profileImage by profileImageViewModel.profileImage.observeAsState()
                profileImage?.let {
                    displayProfileImage = true
                } ?: run {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            } else {
                displayProfileImage = true
            }
            if (displayProfileImage) {
                userProfile.bm?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    )
                }
            }
                followCounts?.let { counts ->
                    Column(
                        modifier = Modifier.padding(start = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "${counts.recipeCount}", fontWeight = FontWeight.Bold)
                        Text(text = "recipes")
                    }
                    Column(
                        modifier = Modifier.padding(start = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "${counts.followersCount}", fontWeight = FontWeight.Bold)
                        Text(text = "Followers")
                    }
                    Column(
                        modifier = Modifier.padding(start = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "${counts.followingsCount}", fontWeight = FontWeight.Bold)
                        Text(text = "Followings")
                    }
                }
                // Observe and fetch more recipes when the user scrolls to the bottom




        }
        if(displayProfileImage){
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp, 0.dp, 16.dp, 32.dp)
            ) {
                if(recipeList.size != recipeViewModel.recipeListDetail.size){

                }
                items(recipeList) { recipe ->
                    Log.d("SIZE:  ", recipeList.size.toString())
                    recipeViewModel.listSize = recipeList.size
                    Log.d("SIZE:  VIEW MODEL:  ", recipeViewModel.recipeListDetail.size.toString())

                    recipeViewModel.recipeListDetail = recipeList
                    Log.d("SIZE:  VIEW MODEL:  ", recipeViewModel.recipeListDetail.size.toString())

                    DisplayRecipe(recipe, recipeViewModel, navController)
                }
            }

            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
                    .collect { lastVisibleItem ->

                        if (lastVisibleItem != null && lastVisibleItem.index >= recipeViewModel.recipeListDetail.size - 1 && recipeViewModel.recipeListDetail.size.toLong() != followCounts?.recipeCount) {
                            Log.d("LOAD MORE RECIPES", "ProfileBanner: ")
                            recipeViewModel.loadMoreRecipes(userProfile.id)
                            delay(1000)
                        }
                    }
            }
        }

    }
}
