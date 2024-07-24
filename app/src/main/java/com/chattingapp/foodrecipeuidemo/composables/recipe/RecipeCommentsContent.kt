package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.viewmodel.CommentViewModel
import com.chattingapp.foodrecipeuidemo.viewmodel.ProfileImageViewModel

@Composable
fun RecipeCommentsContent(commentViewModel: CommentViewModel){

    val commentCount by commentViewModel.commentCount.observeAsState(null)

    val comments by commentViewModel.comments.observeAsState(emptyList())


    LaunchedEffect(Constant.recipeDetailProjection?.id) {
        Constant.recipeDetailProjection?.id?.let { id ->
            if (commentCount == null) { // Fetch only if recipe is not already loaded
                commentViewModel.fetchCommentCount(id)
            }
        }
    }



    // Fetch comments when the composable is first displayed
    if(comments.isEmpty()){
        LaunchedEffect(Constant.recipeDetailProjection!!.id!!) {
            commentViewModel.fetchComments(Constant.recipeDetailProjection!!.id!!)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Comments($commentCount)")
        LazyColumn {
            items(comments) { comment ->
                CommentItem(comment)
            }
        }
    }





}