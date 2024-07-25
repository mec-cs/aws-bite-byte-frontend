package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection
import com.chattingapp.foodrecipeuidemo.entity.UserProfile

@Composable
fun CommentItem(comment: CommentProjection, userProfile: UserProfile?, profileImage: Bitmap?) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userProfile != null) {
            profileImage?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } ?: CircularProgressIndicator()
            Text(text = userProfile.username, fontWeight = FontWeight.Bold)
        } else {
            CircularProgressIndicator()
        }
        Text(text = comment.comment ?: "No comment")
    }
}
