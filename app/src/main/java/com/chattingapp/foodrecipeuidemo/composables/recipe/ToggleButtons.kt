package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToggleButtons(
    selectedTab: MutableState<String>,
    onSelectTab: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Text for Details
        Text(
            text = "Details",
            fontSize = 16.sp,
            color = if (selectedTab.value == "Details") Color.Black else Color.Gray,
            fontWeight = if (selectedTab.value == "Details") FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable { onSelectTab("Details") }
        )

        // Text for Comments
        Text(
            text = "Comments",
            fontSize = 16.sp,
            color = if (selectedTab.value == "Comments") Color.Black else Color.Gray,
            fontWeight = if (selectedTab.value == "Comments") FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable { onSelectTab("Comments") }
        )
    }
}