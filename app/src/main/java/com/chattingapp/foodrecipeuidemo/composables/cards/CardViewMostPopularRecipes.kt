package com.chattingapp.foodrecipeuidemo.composables.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R

@Composable
fun CardViewMostPopularRecipes(navController: NavController, cardId: String) {
    val text = "Popular Recipes"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                navController.navigate("recipeCategory/$cardId")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
            //.padding(8.dp)

        ) {

            Image(
                painter = painterResource(id = R.drawable.popularrecipes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )


            Text(
                text = buildAnnotatedString {
                    append(text)
                    addStyle(
                        style = SpanStyle(textDecoration = TextDecoration.Underline),
                        start = 0,
                        end = text.length
                    )
                },
                fontSize = 16.sp,
                textAlign = TextAlign.Right,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(16.dp)
            )
            // Add more content inside the card here if needed
        }
    }
}