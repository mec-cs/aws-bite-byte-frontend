package com.chattingapp.foodrecipeuidemo.composables.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.chattingapp.foodrecipeuidemo.R

@Composable
fun PublicHealthAnnouncement(){
    Image(
        painter = painterResource(id = R.drawable.yumbyte_logo_public_announcement),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()

    )
}