package com.chattingapp.foodrecipeuidemo.composables.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chattingapp.foodrecipeuidemo.R

@Composable
fun ProfilePhotoPlaceholder() {
    Image(
        painter = painterResource(id = R.drawable.yumbyte_logo),
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .padding(10.dp, 0.dp, 0.dp, 0.dp)

    )
}
