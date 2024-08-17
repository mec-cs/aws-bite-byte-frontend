package com.chattingapp.foodrecipeuidemo.composables.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chattingapp.foodrecipeuidemo.R

@Composable
fun LoginLogo() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.yumbyte_logo),
            contentDescription = "Yumbyte Logo",
            modifier = Modifier.height(250.dp)
        )
    }
}