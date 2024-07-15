/*package com.chattingapp.foodrecipeuidemo.composables.navigationbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.chattingapp.foodrecipeuidemo.R
@Composable
fun CreateRecipeScreen(navController: NavHostController) {
    var recipeName by remember { mutableStateOf("Enter name") }
    var ingredients by remember { mutableStateOf("Enter ingredients") }
    var instructions by remember { mutableStateOf("Enter instructions") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Choose Recipe's Picture")
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.spaghetti_bolognese),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = recipeName,
            onValueChange = { recipeName = it },
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                if (recipeName.isEmpty()) {
                    Text("Recipe Name", style = TextStyle(color = Color.Gray, fontSize = 18.sp))
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                if (ingredients.isEmpty()) {
                    Text("Enter Ingredients", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = instructions,
            onValueChange = { instructions = it },
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                if (instructions.isEmpty()) {
                    Text("Enter Instructions", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /* Backend Part (Batu) */ }) {
                Text("Publish")
            }
            Button(onClick = { /* Backend Part (Batu) */ }) {
                Text("Save As Draft")
            }
        }
    }
}*/

package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.chattingapp.foodrecipeuidemo.R

@Composable
fun CreateRecipeScreen(navController: NavHostController) {
    var recipeName by remember { mutableStateOf("Enter name") }
    var ingredients by remember { mutableStateOf("Enter ingredients") }
    var instructions by remember { mutableStateOf("Enter instructions") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Choose Recipe's Picture")

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Gray)
                .clickable { launcher.launch("image/*") }
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.spaghetti_bolognese),  // Add a placeholder image resource
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = recipeName,
            onValueChange = { recipeName = it },
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                if (recipeName.isEmpty()) {
                    Text("Recipe Name", style = TextStyle(color = Color.Gray, fontSize = 18.sp))
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                if (ingredients.isEmpty()) {
                    Text("Enter Ingredients", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = instructions,
            onValueChange = { instructions = it },
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                if (instructions.isEmpty()) {
                    Text("Enter Instructions", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
                .background(Color.White, MaterialTheme.shapes.medium)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /* Backend Part (Batu) */ }) {
                Text("Publish")
            }

            Button(onClick = { /* Backend Part (Batu) */ }) {
                Text("Save As Draft")
            }
        }
    }
}