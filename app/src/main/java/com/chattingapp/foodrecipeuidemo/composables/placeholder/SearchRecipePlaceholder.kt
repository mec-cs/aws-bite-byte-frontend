package com.chattingapp.foodrecipeuidemo.composables.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.entity.RecipeSearchResult

@Composable
fun SearchRecipePlaceholder(recipe: RecipeSearchResult){
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
            Image(
                painter = painterResource(id = R.drawable.yumbyte_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = recipe.name,
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold, // Make the text bold
                        fontSize = 18.sp // You can adjust the font size as needed
                    )
                )
                Text(
                    text = "By "+ recipe.ownerUsername,
                    style = TextStyle(
                        fontStyle = FontStyle.Italic, // Make the text italic
                        fontSize = 15.sp, // Set the font size to 15sp
                        color = Color.Gray
                    ),
                )
            }




    }
}