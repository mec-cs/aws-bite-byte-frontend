package com.chattingapp.foodrecipeuidemo.composables.follow

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.composables.placeholder.NoRecipeUserCommentPlaceholder
import com.chattingapp.foodrecipeuidemo.composables.placeholder.PageLoadingPlaceholder
import com.chattingapp.foodrecipeuidemo.composables.profilepage.ProfileFollowPeople
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.viewmodel.FollowCountsViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileFollowerViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileFollowingViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowsPage(
    navController: NavController,
    followType: String?,
    followerCount: String?,
    followingCount: String?
) {
    val selectedTab = rememberSaveable { mutableStateOf(followType) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${selectedTab.value}") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack("profile", false, true)
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Make the background transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val profileFollowerViewModel: ProfileFollowerViewModel = viewModel()
            val userListFollower by profileFollowerViewModel.userList.collectAsState()
            val isLoadingFollower by profileFollowerViewModel.isLoading.collectAsState()
            var isFirstTimeFollower by remember { mutableStateOf(true) }
            val listStateFollower = rememberLazyListState()

            val profileFollowingViewModel: ProfileFollowingViewModel = viewModel()
            val userListFollowing by profileFollowingViewModel.userList.collectAsState()
            val isLoadingFollowing by profileFollowingViewModel.isLoading.collectAsState()
            var isFirstTimeFollowing by remember { mutableStateOf(true) }
            val listStateFollowing = rememberLazyListState()


            val userProfile: UserProfile = Constant.targetUserProfile ?: Constant.userProfile

            LazyRow(
                horizontalArrangement = Arrangement.Center, // Center items horizontally
                contentPadding = PaddingValues(horizontal = 16.dp), // Padding around the row
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Followers",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { selectedTab.value = "Followers" },
                        style = if (selectedTab.value == "Followers") {
                            TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
                        } else {
                            TextStyle(fontWeight = FontWeight.Normal, color = Color.Gray)
                        }
                    )
                }
                item {
                    Text(
                        text = "Followings",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { selectedTab.value = "Followings" },
                        style = if (selectedTab.value == "Followings") {
                            TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
                        } else {
                            TextStyle(fontWeight = FontWeight.Normal, color = Color.Gray)
                        }
                    )
                }
            }

            when (selectedTab.value) {
                "Followers" -> {
                    if (isFirstTimeFollower) {
                        LaunchedEffect(userProfile.id) {
                            if(followerCount!!.toInt() != 0){
                                profileFollowerViewModel.fetchUsers(userProfile.id)
                            }
                        }
                        isFirstTimeFollower = false
                    }

                    if (isLoadingFollower) {
                        PageLoadingPlaceholder()
                    } else {
                        Column {
                            if(userListFollower.isEmpty()){
                                NoRecipeUserCommentPlaceholder()
                            }
                            else{
                                LazyColumn(
                                    state = listStateFollower,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp, 0.dp, 16.dp, 32.dp)
                                ) {
                                    items(userListFollower) { user ->
                                        profileFollowerViewModel.userListDetail = userListFollower
                                        ProfileFollowPeople(user, selectedTab.value)
                                    }
                                }

                                LaunchedEffect(listStateFollower) {
                                    snapshotFlow { listStateFollower.layoutInfo.visibleItemsInfo.lastOrNull() }
                                        .collect { lastVisibleItem ->
                                            if (lastVisibleItem != null && lastVisibleItem.index >= profileFollowerViewModel.userListDetail.size - 1
                                                && userListFollower.size != followerCount!!.toInt() && followerCount.toInt() > Constant.PAGE_SIZE_PROFILE) {
                                                profileFollowerViewModel.loadMoreUsers(userProfile.id)
                                                delay(1000)
                                            }
                                        }
                                }
                            }

                        }
                    }
                }
                "Followings" -> {
                    // Handle "Followings" tab content here
                    if (isFirstTimeFollowing) {
                        Log.d("inside", "isFirstTimeFollowing")
                        LaunchedEffect(userProfile.id) {
                            if(followingCount!!.toInt() != 0){
                                profileFollowingViewModel.fetchUsers(userProfile.id)
                            }
                        }
                        isFirstTimeFollowing = false
                    }

                    if (isLoadingFollowing) {
                        PageLoadingPlaceholder()
                    } else {
                        Column {
                            if(userListFollowing.isEmpty()){
                                NoRecipeUserCommentPlaceholder()
                            }
                            else{
                                val profileCountsViewModel = FollowCountsViewModel()
                                LazyColumn(
                                    state = listStateFollowing,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp, 0.dp, 16.dp, 32.dp)
                                ) {
                                    items(userListFollowing) { user ->


                                        ProfileFollowPeople(user, selectedTab.value)
                                    }
                                }

                                LaunchedEffect(listStateFollowing) {
                                    snapshotFlow { listStateFollowing.layoutInfo.visibleItemsInfo.lastOrNull() }
                                        .collect { lastVisibleItem ->
                                            if (lastVisibleItem != null && lastVisibleItem.index == userListFollowing.size - 1
                                                && followingCount!!.toInt() > userListFollowing.size && followingCount.toInt() > Constant.PAGE_SIZE_PROFILE) {
                                                profileFollowingViewModel.loadMoreUsers(userProfile.id)
                                                delay(1000)
                                            }
                                        }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}