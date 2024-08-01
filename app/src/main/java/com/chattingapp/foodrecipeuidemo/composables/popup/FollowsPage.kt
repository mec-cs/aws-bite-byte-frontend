package com.chattingapp.foodrecipeuidemo.composables.popup

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.chattingapp.foodrecipeuidemo.composables.profilepage.ProfileFollowPeople
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.viewmodel.FollowCountsViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileButtonStatusViewModel
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
                }
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

            /*LazyRow(
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
            }*/

            when (selectedTab.value) {
                "Followers" -> {
                    if (isFirstTimeFollower) {
                        LaunchedEffect(userProfile.id) {
                            profileFollowerViewModel.fetchUsers(userProfile.id)
                        }
                        isFirstTimeFollower = false
                    }

                    if (isLoadingFollower) {
                        CircularProgressIndicator(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp))
                    } else {
                        Column {

                            LazyColumn(
                                state = listStateFollower,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp, 0.dp, 16.dp, 32.dp)
                            ) {
                                items(userListFollower) { user ->
                                    profileFollowerViewModel.userListDetail = userListFollower
                                    ProfileFollowPeople(user, profileFollowerViewModel, profileFollowingViewModel, selectedTab.value, null)
                                }
                            }

                            LaunchedEffect(listStateFollower) {
                                snapshotFlow { listStateFollower.layoutInfo.visibleItemsInfo.lastOrNull() }
                                    .collect { lastVisibleItem ->
                                        if (lastVisibleItem != null && lastVisibleItem.index >= profileFollowerViewModel.userListDetail.size - 1 && userListFollower.size != followerCount!!.toInt()) {
                                            profileFollowerViewModel.loadMoreUser(userProfile.id)
                                            delay(1000)
                                        }
                                    }
                            }
                        }
                    }
                }
                "Followings" -> {
                    // Handle "Followings" tab content here
                    if (isFirstTimeFollowing) {
                        LaunchedEffect(userProfile.id) {
                            profileFollowingViewModel.fetchUsers(userProfile.id)
                        }
                        isFirstTimeFollowing = false
                    }

                    if (isLoadingFollowing) {
                        CircularProgressIndicator(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp))
                    } else {
                        Column {
                            val profileCountsViewModel = FollowCountsViewModel()
                            LazyColumn(
                                state = listStateFollowing,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp, 0.dp, 16.dp, 32.dp)
                            ) {
                                items(userListFollowing) { user ->
                                    profileFollowingViewModel.userListDetail = userListFollowing

                                    ProfileFollowPeople(user, profileFollowerViewModel, profileFollowingViewModel, selectedTab.value, profileCountsViewModel)
                                }
                            }

                            LaunchedEffect(listStateFollowing) {
                                snapshotFlow { listStateFollowing.layoutInfo.visibleItemsInfo.lastOrNull() }
                                    .collect { lastVisibleItem ->
                                        if (lastVisibleItem != null && lastVisibleItem.index >= profileFollowingViewModel.userListDetail.size - 1 && userListFollowing.size != followingCount!!.toInt()) {
                                            profileFollowingViewModel.loadMoreUser(userProfile.id)
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