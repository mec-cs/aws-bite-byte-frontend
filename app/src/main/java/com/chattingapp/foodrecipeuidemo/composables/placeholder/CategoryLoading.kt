package com.chattingapp.foodrecipeuidemo.composables.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.chattingapp.foodrecipeuidemo.R

@Composable
fun PageLoadingPlaceholder(){
    Image(
        painter = painterResource(id = R.drawable.yumbyte_logo),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()

    )
}