package com.chattingapp.foodrecipeuidemo.composables.search

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.activitiy.ui.theme.Purple40
import com.chattingapp.foodrecipeuidemo.activitiy.ui.theme.PurpleGrey40
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.SearchCriteria
import com.chattingapp.foodrecipeuidemo.entity.SearchRecipeDTO
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SearchPageCall(navController: NavController) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isRecipeSelected by remember { mutableStateOf(true) }
    var isImageRendered by remember { mutableStateOf(false) }
    var isRecipeImageRendered by remember { mutableStateOf(false) }
    var userProfiles by remember { mutableStateOf(listOf<UserProfile>()) }
    var recipes by remember { mutableStateOf(listOf<Recipe>()) }

    // Coroutine scope for debounce
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SearchBar(
            searchText = searchText,
            isRecipeSelected = isRecipeSelected,
            onTextChange = { newText ->
                searchText = newText
                val trimmedText = newText.text.trim()

                searchJob?.cancel() // Cancel the previous job if any
                searchJob = scope.launch {
                    delay(500)
                    if (trimmedText.isNotEmpty()) {
                        // Log.d("SearchPageCall", "Search box value: $trimmedText")
                        if (isRecipeSelected) {
                            searchRecipes(trimmedText, { recipes = it }, { isRecipeImageRendered = it })
                        } else {
                            searchUsers(trimmedText, { userProfiles = it }, { isImageRendered = it })
                        }
                    } else {

                        recipes = listOf()
                        userProfiles = listOf()
                        isRecipeImageRendered = false
                        isImageRendered = false
                    }
                }
            },
            hint = if (isRecipeSelected) "Search for recipes..." else "Search for users..."
        )


        Spacer(modifier = Modifier.height(8.dp))

        ToggleView(isRecipeSelected) { isSelected ->
            isRecipeSelected = isSelected
            Log.d("SearchPageCall SELECTED", if (isSelected) "Recipes selected" else "Users selected")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isRecipeSelected) {
            RecipeList(recipes, isRecipeImageRendered, navController)
        } else {
            UserProfileList(userProfiles, isImageRendered, navController)
        }
    }
}

@Composable
fun SearchBar(
    searchText: TextFieldValue,
    isRecipeSelected: Boolean,
    onTextChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "Search..."
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            BasicTextField(
                value = searchText,
                onValueChange = onTextChange,
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            if (searchText.text.isEmpty()) {
                                Text(
                                    text = hint,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                        if (searchText.text.isNotEmpty()) {
                            IconButton(onClick = { onTextChange(TextFieldValue("")) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Icon",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isRecipeSelected) {
            IconButton(
                onClick = { /* Handle filter click */ },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ToggleView(isRecipeSelected: Boolean, onSelectionChange: (Boolean) -> Unit) {
    val activeColor = Purple40
    val inactiveColor = PurpleGrey40

    Box(
        modifier = Modifier
            .width(180.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(65))
            .background(inactiveColor)
            .clickable {
                onSelectionChange(!isRecipeSelected)
            }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(65))
                .background(if (isRecipeSelected) activeColor else inactiveColor)
                .fillMaxWidth()
                .clickable {
                    onSelectionChange(!isRecipeSelected)
                }
                .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recipe",
                color = if (isRecipeSelected) Color.White else Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (!isRecipeSelected) onSelectionChange(true)
                    }
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "User",
                color = if (isRecipeSelected) Color.Black else Color.White,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (isRecipeSelected) onSelectionChange(false)
                    }
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RecipeList(recipes: List<Recipe>, isRecipeImageRendered: Boolean, navController: NavController) {
    LazyColumn {
        items(recipes) { recipe ->
            RecipeItem(
                recipe,
                isRecipeImageRendered,
                onClick = { id ->
                    Log.d("Recipe ID", "Clicked recipe id: $id")

                    // navigate to the Recipe Details Page
                    val recipeProObj = RecipeProjection(id = recipe.id, name = recipe.name, description = recipe.description, dateCreated = recipe.dateCreated, image = recipe.image, ownerId = recipe.ownerId, bmRecipe = recipe.bm)
                    val relDate = CalculateDate.formatDateForUser(recipe.dateCreated!!)

                    recipeProObj.relativeDate = relDate

                    Constant.recipeDetailProjection = recipeProObj
                    Constant.isSearchScreen = true
                    navController.navigate("recipeDetail/${"Details"}")
                }
            )
            Divider()
        }
    }
}

@Composable
fun UserProfileList(userProfiles: List<UserProfile>, isImgRendered: Boolean, navController: NavController) {
    LazyColumn {
        items(userProfiles) { userProfile ->
            UserProfileItem(
                userProfile = userProfile,
                isImgRendered = isImgRendered,
                onClick = { id ->
                    Log.d("UserProfile", "Clicked user id: $id")
                    // Set the targetUserProfile and navigate to profile
                    Constant.targetUserProfile = userProfile
                    Constant.isProfilePage = true
                    navController.navigate("profile")
                }
            )
            Divider()
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, isRecipeImgRendered: Boolean, onClick: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { onClick(recipe.id!!) },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRecipeImgRendered && recipe.bm != null) {
                Image(
                    bitmap = recipe.bm!!.asImageBitmap(),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .weight(1f)
            ) {
                Text(
                    text = recipe.name!!,
                    style = TextStyle(
                        color = Color(0xFF2b2b2b),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Recipe ID: ${recipe.id}",
                    style = TextStyle(
                        color = Color(0xFF666666),
                        fontSize = 14.sp
                    )
                )
            }
            IconButton(onClick = { /* Handle drop-down icon click */ }) {
                Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = "Drop Down")
            }
        }
    }
}


@Composable
fun UserProfileItem(userProfile: UserProfile, isImgRendered: Boolean, onClick: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { onClick(userProfile.id) },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isImgRendered && userProfile.bm != null) {
                Image(
                    bitmap = userProfile.bm!!.asImageBitmap(),
                    contentDescription = "User Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .weight(1f)
            ) {
                Text(
                    text = userProfile.username,
                    style = TextStyle(
                        color = Color(0xFF2b2b2b),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
//                Text(text = "Subtitle for ${userProfile.username}", style = MaterialTheme.typography.bodySmall) // Adjust subtitle as needed
            }
            IconButton(onClick = { /* Handle phone icon click */ }) {
                Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = "Drop Down")
            }
        }
    }
}

private fun searchRecipes(query: String, onResult: (List<Recipe>) -> Unit, onImageRendered: (Boolean) -> Unit) {
    val searchRecipeDTO = SearchRecipeDTO(query, 0)
    RetrofitHelper.apiService.getRecipesByNames(searchRecipeDTO)
        .enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    val recipes = response.body() ?: listOf()
                    onResult(recipes)
                    val recipeImages: List<String> = recipes.map { it.image!! }
                    Log.e("SearchPageCall Successfull", "Recipe List Successfully Fetched")

                    RetrofitHelper.apiService.getRecipeImagesList(recipeImages)
                        .enqueue(object: Callback<List<String>> {
                            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                if (response.isSuccessful) {
                                    val encodedRecipeStrings: List<String> = response.body() ?: listOf()
                                    recipes.forEachIndexed { index, recipe ->
                                        val encodedRecipeString = encodedRecipeStrings.getOrNull(index)
                                        if (encodedRecipeString != null) {
                                            val decodedBytes = Base64.decode(encodedRecipeString, Base64.DEFAULT)
                                            val bitm = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                            recipe.bm = bitm
                                        }
                                    }
                                    onImageRendered(true)
                                    Log.e("SearchPageCall Successfull", "Recipe Image List Successfully Fetched")
                                } else {
                                    Log.e("SearchPageCall Unsuccuessful", "Error: ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                Log.d("SearchPageCall FAILURE", "Failed to fetch recipe images\n$t")
                            }
                        })

                } else {
                    Log.e("SearchPageCall UNSUCCESSFULL", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Log.e("SearchPageCall FAILURE", "Network request failed\n$t")
            }
        })
}

private fun searchUsers(query: String, onResult: (List<UserProfile>) -> Unit, onImageRendered: (Boolean) -> Unit) {
    val searchCriteria = SearchCriteria(query, 0)
    RetrofitHelper.apiService.getUsersByUsername(searchCriteria)
        .enqueue(object : Callback<List<UserProfile>> {
            override fun onResponse(call: Call<List<UserProfile>>, response: Response<List<UserProfile>>) {
                if (response.isSuccessful) {
                    val userProfiles = response.body() ?: listOf()
                    onResult(userProfiles)
                    val profilePictures: List<String> = userProfiles.map { it.profilePicture }
                    Log.e("SearchPageCall Successfull", "User List Successfully Fetched")

                    RetrofitHelper.apiService.getProfilePicturesList(profilePictures)
                        .enqueue(object : Callback<List<String>> {
                            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                if (response.isSuccessful) {
                                    val encodedStrings: List<String> = response.body() ?: listOf()
                                    userProfiles.forEachIndexed { index, userProfile ->
                                        val encodedString = encodedStrings.getOrNull(index)
                                        if (encodedString != null) {
                                            val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
                                            val bitm = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                            userProfile.bm = bitm
                                        }
                                    }
                                    onImageRendered(true)
                                    Log.e("SearchPageCall Successfull", "User Image List Successfully Fetched")
                                } else {
                                    Log.e("ProfilePictureFetch", "Error: ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                Log.e("ProfilePictureFetch", "Cannot fetch profile pictures", t)
                            }
                        })
                } else {
                    Log.e("SearchPageCall FAILURE", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<UserProfile>>, t: Throwable) {
                Log.e("SearchPageCall", "Network request failed\n", t)
            }
        })
}


@Preview
@Composable
fun displaySearchPage() {
    SearchPageCall(navController = rememberNavController())
}

