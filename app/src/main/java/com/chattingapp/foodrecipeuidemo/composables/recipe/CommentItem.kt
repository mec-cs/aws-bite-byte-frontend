package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chattingapp.foodrecipeuidemo.entity.CommentProjection

@Composable
fun CommentItem(comment: CommentProjection){


    Text(text = comment.comment!!)
}