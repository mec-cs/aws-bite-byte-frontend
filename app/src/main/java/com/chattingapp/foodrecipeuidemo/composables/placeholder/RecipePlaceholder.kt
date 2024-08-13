package com.chattingapp.foodrecipeuidemo.composables.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection

@Composable
fun RecipePlaceholder(recipe:RecipeProjection){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 10.dp, top = 0.dp, start = 0.dp)
            .fillMaxWidth()
    ){
        Image(
            painter = painterResource(id = R.drawable.placeholder_profile),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .padding(end = 10.dp)
        )
        Text(
            text = "Loading...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = "Add to favorites",
            tint = Color.Gray,
            modifier = Modifier
                .padding(16.dp)

        )
    }
    recipe.name?.let {
        Text(
        text = it,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    }

    Image(
        painter = painterResource(id = R.drawable.placeholder_recipe), // Replace with your drawable resource name
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        IconButton(modifier = Modifier
            .size(30.dp) // Adjust the size of the button
            .clip(RoundedCornerShape(8.dp)) // Clip the icon to have rounded corners
            , onClick = {
            }, enabled = false) {
            val icon = R.drawable.like
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Like",
                modifier = Modifier.size(24.dp), // Adjust the size of the icon
                tint = Color.Unspecified
            )
        }
        Text(
            text = "0",
            fontSize = 15.sp
        )

        IconButton(
            modifier = Modifier
                .size(30.dp) // Adjust the size of the button
                .clip(RoundedCornerShape(8.dp)),
            onClick = {
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.comment),
                contentDescription = "Comment",
                modifier = Modifier.size(24.dp), // Adjust the size of the icon
                tint = Color.Unspecified
            )
        }
    }

    val description = recipe.description ?: ""
    val truncatedDescription = if (description.length > Constant.MAX_DESCRIPTION_SIZE) {
        description.take(Constant.MAX_DESCRIPTION_SIZE) + "..."
    } else {
        description
    }

    Text(
        text = truncatedDescription,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    if (description.length > Constant.MAX_DESCRIPTION_SIZE) {
        Text(
            text = "See more",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {  }
        )
    }
    val relativeDate = recipe.dateCreated?.let { CalculateDate.formatDateForUser(it) }

    if (relativeDate != null) {
        Text(text = relativeDate, fontSize = 12.sp)
    }
}