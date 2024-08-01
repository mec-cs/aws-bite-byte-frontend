package com.chattingapp.foodrecipeuidemo.composables.profilepage

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.composables.recipe.DisplayRecipe
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.viewmodel.FollowCountsViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileImageViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.UserProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfileBanner(  navController: NavController) {
    val recipeViewModel: RecipeViewModel = viewModel()
    val viewModel: FollowCountsViewModel = viewModel()
    val profileImageViewModel: ProfileImageViewModel = viewModel()
    val recipeList by recipeViewModel.recipeList.collectAsState(emptyList())
    val followCounts by viewModel.followCounts.collectAsState(null)

    val userProfile = remember {
        Constant.targetUserProfile ?: Constant.userProfile
    }

    var isFirstTime by remember { mutableStateOf(true) }

    /*if(Constant.targetUserProfile != null){
        userProfile = Constant.targetUserProfile!!
        Log.d("PROFILE IMAGE: ", userProfile.profilePicture)

    }
    else{
        userProfile = Constant.userProfile
    }*/

    if(isFirstTime){
        LaunchedEffect(userProfile.id) {
            viewModel.fetchFollowersCount(userProfile.id)
            recipeViewModel.fetchRecipes(userProfile.id)
            isFirstTime = false
        }
    }


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


                    Column {
                        Row {
                            Column(
                                modifier = Modifier.padding(start = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "${counts.recipeCount}", fontWeight = FontWeight.Bold)
                                Text(text = "recipes")
                            }
                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                                    .clickable {
                                        val followType = "Followers"
                                        val followerCount = followCounts!!.followersCount.toString()
                                        val followingCount = followCounts!!.followingsCount.toString()
                                        navController.navigate("profileFollows/$followType/$followerCount/$followingCount")
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,

                            ) {
                                Text(text = "${counts.followersCount}", fontWeight = FontWeight.Bold)
                                Text(text = "Followers")
                            }
                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                                    .clickable {
                                        val followType = "Followings"
                                        val followerCount = followCounts!!.followersCount.toString()
                                        val followingCount = followCounts!!.followingsCount.toString()
                                        navController.navigate("profileFollows/$followType/$followerCount/$followingCount")
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "${counts.followingsCount}", fontWeight = FontWeight.Bold)
                                Text(text = "Followings")
                            }

                        }
                        if(Constant.targetUserProfile != null){
                            val isFollowing by viewModel.isFollowing.collectAsState()
                            val isChecking by viewModel.isChecking.collectAsState()
                            val isActionInProgress by viewModel.isActionInProgressFlow.collectAsState()
                            LaunchedEffect(Unit) {
                                viewModel.checkIfUserFollows(Constant.userProfile.id, Constant.targetUserProfile!!.id)
                            }
                            if(!isChecking){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp) // Add horizontal padding
                                        .wrapContentWidth(Alignment.CenterHorizontally) // Center the box horizontally
                                ) {
                                    Button(
                                        onClick = {
                                            // Toggle follow/unfollow action based on the current state
                                            if (isFollowing == true) {
                                                // Call API to unfollow
                                                viewModel.unfollowUser(Constant.userProfile.id, Constant.targetUserProfile!!.id)
                                            } else {
                                                // Call API to follow
                                                viewModel.followUser(Constant.userProfile.id, Constant.targetUserProfile!!.id)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isFollowing == true) Color.Black else Color.White, // Background color
                                            contentColor = if (isFollowing == true) Color.White else Color.Black // Text color
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        border = if (isFollowing == true) null else BorderStroke(1.dp, Color.Black), // Add border if not followed
                                        modifier = Modifier
                                            .fillMaxWidth(), // Ensure the button fills the Box
                                        enabled = !isActionInProgress
                                    ) {
                                        Text(
                                            text = if (isFollowing == true) "Unfollow" else "Follow",
                                            style = TextStyle(
                                                fontSize = 14.sp, // Font size for the button text
                                                fontWeight = FontWeight.Bold // Font weight
                                            )
                                        )
                                    }
                                }
                            }

                        }

                    }

                }
                // Observe and fetch more recipes when the user scrolls to the bottom



        }
        if(displayProfileImage){
            val userProfileViewModel = UserProfileViewModel()
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp, 0.dp, 16.dp, 32.dp)
            ) {

                items(recipeList) { recipe ->
                    Log.d("SIZE:  ", recipeList.size.toString())
                    recipeViewModel.listSize = recipeList.size
                    Log.d("SIZE:  VIEW MODEL:  ", recipeViewModel.recipeListDetail.size.toString())

                    recipeViewModel.recipeListDetail = recipeList
                    Log.d("SIZE:  VIEW MODEL:  ", recipeViewModel.recipeListDetail.size.toString())
                    Constant.isProfilePage = true
                    DisplayRecipe(recipe, /*recipeViewModel,*/ navController/*, userProfileViewModel*/)
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
