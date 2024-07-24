package com.chattingapp.foodrecipeuidemo.composables.recipe

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToggleButtons(
    selectedTab: MutableState<String>,
    onSelectTab: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        // Button for Details
        OutlinedButton(
            onClick = { onSelectTab("Details") },
            colors = ButtonDefaults.buttonColors(
                //containerColor = if (selectedTab.value == "Details") Color.Gray else Color.Transparent
            ),
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(text = "Details", fontSize = 16.sp)
        }

        // Button for Comments
        OutlinedButton(
            onClick = { onSelectTab("Comments") },
            colors = ButtonDefaults.outlinedButtonColors(
                //containerColor = if (selectedTab.value == "Details") Color.Gray else Color.Transparent
            ),
            modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)

        ) {
            Text(text = "Comments", fontSize = 16.sp)
        }
    }
}