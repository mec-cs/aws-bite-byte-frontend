package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.composables.placeholder.NoRecipeUserCommentPlaceholder
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.CommentViewModel
import kotlinx.coroutines.delay

@Composable
fun RecipeCommentsContent(commentViewModel: CommentViewModel) {

    val commentCount by commentViewModel.commentCount.observeAsState(-1)

    val comments by commentViewModel.comments.observeAsState(emptyList())

    Constant.deletedCommentCount = 0
    val userProfiles by commentViewModel.userProfilesComment.observeAsState(emptyList())
    val profileImageCache by commentViewModel.profileImageCacheComment.observeAsState(emptyMap())

    var textState by remember { mutableStateOf(TextFieldValue()) }

    val isTextNotEmpty = textState.text.trim().isNotEmpty()

    var isNoCommentContent by remember { mutableStateOf(false) }


    // Determine which icon to display based on text input
    val iconResId = if (isTextNotEmpty) {
        R.drawable.sendenabled // Icon for non-empty text
    } else {
        R.drawable.senddisabled // Icon for empty text
    }


    LaunchedEffect(Constant.recipeDetailProjection?.id) {
        Constant.recipeDetailProjection?.id?.let { id ->
            if (commentCount == -1L) { // Fetch only if recipe is not already loaded
                Log.d("RecipeCommentsContent", "Fetching comment count for recipeId: $id")
                commentViewModel.fetchCommentCount(id)
            }
        }
    }
    if(commentCount != -1L)
    // Fetch comments when the composable is first displayed
    {
        if (comments.isEmpty() && commentCount.toInt() != 0) {
            LaunchedEffect(Constant.recipeDetailProjection!!.id!!) {
                Log.d("HERE", "HERE")
                commentViewModel.fetchComments(Constant.recipeDetailProjection!!.id!!)
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            val listState = rememberLazyListState()

            LazyColumn(state = listState) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Comments($commentCount)")
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Image(
                            bitmap = Constant.userProfile.bm!!.asImageBitmap(),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                        Text(text = Constant.userProfile.username, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // TextArea (TextField)
                            TextField(
                                value = textState,
                                onValueChange = { newText -> textState = newText },
                                modifier = Modifier
                                    .weight(1f) // Allows the TextField to take up remaining space
                                    .height(50.dp), // Adjust height as needed
                                placeholder = { Text("Enter your comment...") }
                            )

                            // Icon in front of TextField
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp) // Spacing between TextField and Icon
                                    .clickable(enabled = isTextNotEmpty) {
                                        if (isTextNotEmpty) {

                                            commentViewModel.addComment(
                                                recipeId = Constant.recipeDetailProjection!!.id!!,
                                                commentText = textState.text.trim()
                                            )

                                            textState = TextFieldValue() // Clear text field

                                        }
                                    }
                            ) {
                                // Icon based on text input
                                Icon(
                                    painter = painterResource(id = iconResId),
                                    contentDescription = "Input Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                }
                if(!isNoCommentContent){
                    items(comments) { comment ->

                        CommentItem(
                            comment = comment,
                            commentViewModel
                        )
                    }
                }
                if(isNoCommentContent){
                    item {
                        NoRecipeUserCommentPlaceholder()
                    }
                }

            }

            LaunchedEffect(commentCount) {
                if(commentCount == 0L){
                    isNoCommentContent = true
                }
                else{
                    isNoCommentContent = false
                }
            }

            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
                    .collect { lastVisibleItem ->
                        Log.d("commentCount", commentCount.toString())
                        Log.d("comments", comments.size.toString())
                        if (lastVisibleItem != null && lastVisibleItem.index >= comments.size - 1 && commentCount > comments.size) {
                            if(Constant.deletedCommentCount > 0){
                                commentViewModel.currentPage -= (Constant.deletedCommentCount / Constant.PAGE_SIZE_CLICK_LIKE + 1)
                                Constant.deletedCommentCount = 0
                            }
                            if(commentCount.toInt() != 0) {
                                commentViewModel.fetchMoreComments(Constant.recipeDetailProjection?.id!!)
                            }
                            Log.d("FETCH COMMENT", "RECIPE COMMENT CONTENT")
                            delay(1000)
                        }
                    }
            }
        }
    }
}
