package com.chattingapp.foodrecipeuidemo.composables.profilepage

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.UserFollowsResponse
import com.chattingapp.foodrecipeuidemo.viewmodel.FollowCountsViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileFollowerViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileFollowingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileFollowPeople(
    user: UserFollowsResponse,
    profileFollowerViewModel: ProfileFollowerViewModel,
    profileFollowingViewModel: ProfileFollowingViewModel,
    selectedTab:String?,
    viewModel: FollowCountsViewModel?
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var username by remember { mutableStateOf<String?>("") }
    var isLoading by remember { mutableStateOf(true) }



    if(selectedTab == "Followers"){
        LaunchedEffect(user.follower.profilePicture) {
            Log.d("DisplayRecipe", "Fetching image for user: ${user.id}")
            profileFollowerViewModel.fetchImage(user) {
                bitmap = it
                username = user.follower.username
                isLoading = false
                Log.d("DisplayRecipe", "Image fetched for user: ${user.id}")
            }
        }
    }
    else{
        LaunchedEffect(user.followed.profilePicture) {
            Log.d("DisplayRecipe", "Fetching image for user: ${user.id}")
            profileFollowingViewModel.fetchImage(user) {
                bitmap = it
                username = user.followed.username
                isLoading = false
                Log.d("DisplayRecipe", "Image fetched for user: ${user.id}")
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Text(
                    text = username!!,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp)
                )
            }
        }
    }
}
