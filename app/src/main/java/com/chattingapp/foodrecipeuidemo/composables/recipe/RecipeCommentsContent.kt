package com.chattingapp.foodrecipeuidemo.composables.recipe

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.CommentViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileImageViewModel
import kotlinx.coroutines.delay

@Composable
fun RecipeCommentsContent(commentViewModel: CommentViewModel) {

    val commentCount by commentViewModel.commentCount.observeAsState(-1)

    val comments by commentViewModel.comments.observeAsState(emptyList())


    val userProfiles by commentViewModel.userProfilesComment.observeAsState(emptyList())
    val profileImageCache by commentViewModel.profileImageCacheComment.observeAsState(emptyMap())

    LaunchedEffect(comments) {
        val ownerIds = comments.map { it.ownerId!! }.distinct()
        commentViewModel.fetchUserProfiles(ownerIds)
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
        if (comments.isEmpty()) {
            LaunchedEffect(Constant.recipeDetailProjection!!.id!!) {
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
                items(comments) { comment ->
                    val userProfile = userProfiles.find { it.id == comment.ownerId }
                    val profileImage = profileImageCache[comment.ownerId]
                    CommentItem(
                        comment = comment,
                        userProfile = userProfile,
                        profileImage = profileImage
                    )
                }
            }

            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
                    .collect { lastVisibleItem ->
                        if (lastVisibleItem != null && lastVisibleItem.index >= comments.size - 1) {
                            commentViewModel.fetchMoreComments(Constant.recipeDetailProjection?.id!!)
                            Log.d("FETCH COMMENT", "RECIPE COMMENT CONTENT")
                            delay(1000)
                        }
                    }
            }
        }
    }
}
