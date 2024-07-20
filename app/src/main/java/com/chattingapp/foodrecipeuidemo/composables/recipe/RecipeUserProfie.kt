package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeUserProfile(bm: ImageBitmap, username:String){
    Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        //.padding(10.dp)
        .fillMaxWidth()
) {


    Image(
        bitmap = bm,
        contentDescription = null,
        modifier = Modifier
            .size(30.dp)
            .padding(end = 10.dp)
    )

    Text(
        text = username,
        fontSize = 16.sp,
        modifier = Modifier
            .fillMaxWidth()
    )
}
}