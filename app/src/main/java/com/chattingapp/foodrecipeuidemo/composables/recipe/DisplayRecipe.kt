package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.Like
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.viewmodel.LikeViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.RecipeViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DisplayRecipe(recipe: RecipeProjection, viewModel: RecipeViewModel) {
    Column(modifier = Modifier.padding(bottom = 70.dp)) {
        var expanded by remember { mutableStateOf(false) } // State to toggle description
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        var isLoading by remember { mutableStateOf(true) } // State to track loading
        val likeViewModel = LikeViewModel()

        val likeCount by likeViewModel.likeCount.observeAsState()
        likeViewModel.fetchLikeCounts(recipe.id!!)

        LaunchedEffect(recipe.image) {
            viewModel.fetchImage(recipe) {
                bitmap = it
                isLoading = false // Set loading to false once image is loaded
            }
        }

        if (Constant.isProfilePage && Constant.targetUserProfile == null) {
            Constant.userProfile.bm?.let {
                RecipeUserProfile(it.asImageBitmap(), Constant.userProfile.username, recipe.id!!)
            }
        } else if (Constant.isProfilePage && Constant.targetUserProfile != null) {
            Constant.targetUserProfile!!.bm?.let {
                RecipeUserProfile(it.asImageBitmap(), Constant.targetUserProfile!!.username, recipe.id!!)
            }
        } else if (!Constant.isProfilePage && recipe.ownerId == Constant.userProfile.id) {
            Constant.userProfile.bm?.let {
                RecipeUserProfile(it.asImageBitmap(), Constant.userProfile.username, recipe.id!!)
            }
        }

        Text(
            text = recipe.name!!,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp) // Add vertical padding if needed
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            bitmap?.let {
                Column(modifier = Modifier.clickable { Log.d("CLICKED RECIPE:", "HI") }) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    val isLiked by likeViewModel.isLiked.observeAsState(false)
                    val isDisliked by likeViewModel.isDisliked.observeAsState(false)

                    LaunchedEffect(recipe.id) {
                        likeViewModel.checkLike(Constant.userProfile.id, recipe.id)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(), // Ensure the Row takes up full width
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between items
                    ) {
                        likeCount?.let {
                            // Like Button
                            IconButton(modifier = Modifier
                                .size(30.dp) // Adjust the size of the button
                                .clip(RoundedCornerShape(8.dp)) // Clip the icon to have rounded corners

                                , onClick = {
                                    val likeClicked = Like(-1, Constant.userProfile.id, recipe.id, true)
                                    likeViewModel.toggleLike(likeClicked)
                                }) {
                                val icon = if (isLiked) R.drawable.like_filled else R.drawable.like
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = "Like",
                                    modifier = Modifier.size(24.dp), // Adjust the size of the icon
                                    tint = Color.Unspecified
                                )
                            }
                            // Like Count
                            Text(
                                text = it.likes.toString(),
                                fontSize = 15.sp
                            )

                            // Dislike Button
                            IconButton( modifier = Modifier
                                .size(30.dp) // Adjust the size of the button
                                .clip(RoundedCornerShape(8.dp)),onClick = {
                                    val likeClicked = Like(-1, Constant.userProfile.id, recipe.id, false)
                                    likeViewModel.toggleDislike(likeClicked)
                                }) {
                                val icon = if (isDisliked) R.drawable.dislike_filled else R.drawable.dislike
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = "Dislike",
                                    modifier = Modifier.size(24.dp), // Adjust the size of the icon
                                    tint = Color.Unspecified
                                )
                            }
                            // Dislike Count
                            Text(
                                text = it.dislikes.toString(),
                                fontSize = 15.sp
                            )

                            IconButton( modifier = Modifier
                                .size(30.dp) // Adjust the size of the button
                                .clip(RoundedCornerShape(8.dp)),onClick = {

                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.comment),
                                    contentDescription = "Comment",
                                    modifier = Modifier.size(24.dp), // Adjust the size of the icon
                                    tint = Color.Unspecified
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f) // This will make sure the Column takes the remaining width
                                    .padding(start = 16.dp), // Optional: Add padding to the start
                                horizontalArrangement = Arrangement.End // Align items to the start (left)

                            )  {
                                val percentage = calculateLikePercentage(it.likes, it.dislikes)
                                val percentageColor = when (percentage) {
                                    in 81..100 -> Color.Green
                                    in 61..80 -> Color.Cyan
                                    in 41..60 -> Color.Gray
                                    else -> Color.Red // Default color for percentages below 40
                                }
                                if(percentage != -1) {
                                    Text(
                                        text = "$percentage%",
                                        fontSize = 15.sp,
                                        color = percentageColor, modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                                val emoji = when (percentage) {
                                    in 0..40 -> "😔" // Adjust emojis as needed
                                    in 41..60 -> "😐"
                                    in 61..80 -> "🙂"
                                    in 81..100 -> "😋"
                                    else -> "❓" // Fallback emoji
                                }

                                Text(
                                    text = emoji,
                                    fontSize = 20.sp, // Adjust the size of the emoji
                                )
                            }

                        }


                    }



                    val description = recipe.description
                    val truncatedDescription = if (description!!.length > Constant.MAX_TEXT_SIZE) {
                        description.take(Constant.MAX_TEXT_SIZE) + "..."
                    } else {
                        description
                    }

                    Text(
                        text = if (expanded) recipe.description else truncatedDescription,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (description.length > Constant.MAX_TEXT_SIZE) {
                        Text(
                            text = if (expanded) "See less" else "See more",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    }

                    val relativeDate = recipe.dateCreated?.let { formatDateForUser(it) }
                    if (relativeDate != null) {
                        Text(text = relativeDate, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

fun formatDateForUser(dateString: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val dateTime: Date
    try {
        dateTime = formatter.parse(dateString) ?: return "Invalid date"
    } catch (e: ParseException) {
        return "Parse error"
    }
    val now = Date()
    val durationMillis = now.time - dateTime.time
    val durationSeconds = durationMillis / 1000
    val durationMinutes = durationSeconds / 60
    val durationHours = durationMinutes / 60
    val durationDays = durationHours / 24
    return when {
        durationDays > 0 -> "${durationDays} day(s) ago"
        durationHours > 0 -> "${durationHours} hour(s) ago"
        durationMinutes > 0 -> "${durationMinutes} minute(s) ago"
        else -> "Just now"
    }
}

fun calculateLikePercentage(likes: Long, dislikes: Long): Int {
    val total = likes + dislikes
    return if (total == 0L) -1 else ((likes.toDouble() / total) * 100).toInt()
}