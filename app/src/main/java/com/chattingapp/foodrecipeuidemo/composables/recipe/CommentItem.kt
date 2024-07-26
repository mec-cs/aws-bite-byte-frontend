package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection
import com.chattingapp.foodrecipeuidemo.entity.UserProfile

@Composable
fun CommentItem(comment: CommentProjection, userProfile: UserProfile?, profileImage: Bitmap?) {
    var expanded by remember { mutableStateOf(false) }

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

        val commentText = comment.comment ?: ""
        val truncatedDescription = if (commentText.length > Constant.MAX_COMMENT_SIZE) {
            commentText.take(Constant.MAX_COMMENT_SIZE) + "..."
        } else {
            commentText
        }

        Text(
            text = if (expanded) commentText else truncatedDescription,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (commentText.length > Constant.MAX_COMMENT_SIZE) {
            Text(
                text = if (expanded) "See less" else "See more",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
        
        Text(text = CalculateDate.formatDateForUser(comment.dateCreated!!))

    }
}
