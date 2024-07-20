package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
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

        LaunchedEffect(recipe.image) {
            viewModel.fetchImage(recipe) {
                bitmap = it
                isLoading = false // Set loading to false once image is loaded
            }
        }

        if (Constant.isProfilePage && Constant.targetUserProfile == null) {
            Constant.userProfile.bm?.let {
                RecipeUserProfile(it.asImageBitmap(), Constant.userProfile.username)
            }
        } else if (Constant.isProfilePage && Constant.targetUserProfile != null) {
            Constant.targetUserProfile!!.bm?.let {
                RecipeUserProfile(it.asImageBitmap(), Constant.targetUserProfile!!.username)
            }
        } else if (!Constant.isProfilePage && recipe.ownerId == Constant.userProfile.id) {
            Constant.userProfile.bm?.let {
                RecipeUserProfile(it.asImageBitmap(), Constant.userProfile.username)
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
