package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection
import com.chattingapp.foodrecipeuidemo.viewmodel.CommentViewModel

@Composable
fun CommentItem(comment: CommentProjection, viewModel: CommentViewModel) {
    var expanded by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "${Constant.USER_IMAGE_URL}${comment.profilePicture}",
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

            var expandedMenu by remember { mutableStateOf(false) }


            Text(text = comment.username!!, fontWeight = FontWeight.Bold)
            if(comment.ownerId == Constant.userProfile.id){
                Row {
                    IconButton(onClick = { expandedMenu = !expandedMenu }) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp) // Space between dots
                        ) {
                            repeat(3) {
                                Surface(
                                    modifier = Modifier.size(4.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.onSurface
                                ) {}
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete Comment") },
                            onClick = {
                                // Handle the delete action here
                                Constant.deletedCommentCount +=1
                                viewModel.deleteComment(comment.id!!)

                                expandedMenu = false
                            }
                        )
                    }
                }
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
