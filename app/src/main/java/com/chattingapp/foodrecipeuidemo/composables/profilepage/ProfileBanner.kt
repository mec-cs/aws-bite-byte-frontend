package com.chattingapp.foodrecipeuidemo.composables.profilepage

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.FollowCountsDTO
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProfileBanner(){
    var displayFollowCounts by remember { mutableStateOf<Boolean>(false) }
    var followerCount by remember { mutableStateOf<Long>(-100) }
    var followingCount by remember { mutableStateOf<Long>(-100) }
    var recipeCount by remember { mutableStateOf<Long>(-100) }


    val apiService = RetrofitHelper.apiService
    apiService.getFollowersCount(Constant.userProfile.id).enqueue(object :
        Callback<FollowCountsDTO> {
        override fun onResponse(call: Call<FollowCountsDTO>, response: Response<FollowCountsDTO>) {
            val counts = response.body()
            if (counts != null) {
                followerCount = counts.followersCount
                followingCount = counts.followingsCount
                recipeCount = counts.recipeCount
                Log.d("FOLLOWER_COUNT: ", counts.followersCount.toString())
                displayFollowCounts = true
            }
        }
        override fun onFailure(call: Call<FollowCountsDTO>, t: Throwable) {
        }
    })


    var displayProfileImage by remember { mutableStateOf<Boolean>(false) }

    if(Constant.userProfile.bm == null){
        apiService.getImage(Constant.userProfile.profilePicture).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val decodedBytes = Base64.decode(response.body(), Base64.DEFAULT)
                    val bitm = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    Constant.userProfile.bm = bitm

                    displayProfileImage = true
                } else {
                    // Handle error
                    // Log.e(TAG, "Failed to download image: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
            }
        })
    }
    else{
        displayProfileImage = true
    }

    // Your profile screen UI

    Column {
        Text(
            Constant.userProfile.username,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            painterResource(id = R.drawable.ic_launcher_background)

            if(displayProfileImage){
                Constant.userProfile.bm?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null, // Provide a content description if needed
                        modifier = Modifier
                            .size(100.dp)
                            .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    )
                }
            }

            if(displayFollowCounts) {
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = recipeCount.toString(), fontWeight = FontWeight.Bold)
                    Text(text = "recipes")
                }
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = followerCount.toString(), fontWeight = FontWeight.Bold)
                    Text(text = "followers")
                }
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = followingCount.toString(), fontWeight = FontWeight.Bold)
                    Text(text = "followings")
                }
            }
        }


    }
}