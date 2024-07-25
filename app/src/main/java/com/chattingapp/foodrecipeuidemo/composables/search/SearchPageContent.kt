package com.chattingapp.foodrecipeuidemo.composables.search

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.date.CalculateDate
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.entity.RecipeProjection
import com.chattingapp.foodrecipeuidemo.entity.SearchCriteria
import com.chattingapp.foodrecipeuidemo.entity.SearchRecipeDTO
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        SearchBar(searchText, isRecipeSelected) { newText ->
            searchText = newText
            val trimmedText = newText.text.trim()
            if (trimmedText.isNotEmpty()) {
                Log.d("com.chattingapp.foodrecipeuidemo.composables.search.SearchPageCall", "Search box value: $trimmedText")

                if (isRecipeSelected) {
                    val searchRecipeDTO = SearchRecipeDTO(trimmedText, 0)
                    RetrofitHelper.apiService.getRecipesByNames(searchRecipeDTO)
                        .enqueue(object : Callback<List<Recipe>> {
                            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                                if (response.isSuccessful) {
                                    recipes = response.body() ?: listOf()
                                    Log.d("SearchPageCall SUCCESSFULL", "Recipes fetched: $recipes")

                                    val recipeImages: List<String> = recipes.map { it.image!! }

                                    RetrofitHelper.apiService.getRecipeImagesList(recipeImages)
                                        .enqueue(object: Callback<List<String>> {
                                            override fun onResponse(
                                                call: Call<List<String>>,
                                                response: Response<List<String>>
                                            ) {
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
                                                    isRecipeImageRendered = true
                                                } else {
                                                    Log.e("SearchPageCall Unsuccuessful", "Error: ${response.errorBody()?.string()}")
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<List<String>>,
                                                t: Throwable
                                            ) {
                                                Log.d("SearchPageCall FAILURE", "Failed to fetch recipe images\n${t}")
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
                } else {
                    val searchCriteria = SearchCriteria(trimmedText, 0)
                    isImageRendered = false
                    RetrofitHelper.apiService.getUsersByUsername(searchCriteria)
                        .enqueue(object : Callback<List<UserProfile>> {
                            override fun onResponse(call: Call<List<UserProfile>>, response: Response<List<UserProfile>>) {
                                if (response.isSuccessful) {
                                    userProfiles = response.body() ?: listOf()
                                    val profilePictures: List<String> = userProfiles.map { it.profilePicture }

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
                                                    isImageRendered = true
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
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ToggleView(isRecipeSelected) { isSelected ->
            isRecipeSelected = isSelected
            Log.d("SearchPageCall SELECTED", if (isSelected) "Recipes selected" else "Users selected")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isRecipeSelected) {
            RecipeList(recipes, isRecipeImageRendered, navController)
        } else {
            UserProfileList(userProfiles, isImageRendered)
        }
    }
}

@Composable
fun SearchBar(searchText: TextFieldValue, isRecipeSelected: Boolean, onTextChange: (TextFieldValue) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .padding(8.dp)
                .background(Color.Gray.copy(alpha = 0.1f))
        )
        if (isRecipeSelected) {
            IconButton(onClick = { /* Handle filter click */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_filter), contentDescription = "Filter")
            }
        }
    }
}

@Composable
fun ToggleView(isRecipeSelected: Boolean, onSelectionChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = { onSelectionChange(true) }, colors = ButtonDefaults.buttonColors(backgroundColor = if (isRecipeSelected) Color.Gray else Color.LightGray)) {
            Text("Recipes")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSelectionChange(false) }, colors = ButtonDefaults.buttonColors(backgroundColor = if (isRecipeSelected) Color.LightGray else Color.Gray)) {
            Text("Users")
        }
    }
}

@Composable
fun RecipeList(recipes: List<Recipe>, isRecipeImageRendered: Boolean, navController: NavController) {
    Column {
        recipes.forEach { recipe ->
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
fun UserProfileList(userProfiles: List<UserProfile>, isImgRendered: Boolean) {
    Column {
        userProfiles.forEach { userProfile ->
            UserProfileItem(
                userProfile = userProfile,
                isImgRendered = isImgRendered,
                onClick = { id ->
                    Log.d("UserProfile", "Clicked user id: $id")

                    // navigate to the profile page

                }
            )
            Divider()
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, isRecipeImgRendered: Boolean, onClick: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        .clickable { onClick(recipe.id!!) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(recipe.id.toString(), fontSize = 20.sp, modifier = Modifier.width(32.dp))
        Text(recipe.name!!, fontSize = 20.sp, modifier = Modifier.weight(1f))
        if(isRecipeImgRendered) {
            recipe.bm?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }
    }
}

@Composable
fun UserProfileItem(userProfile: UserProfile, isImgRendered: Boolean, onClick: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(userProfile.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(userProfile.id.toString(), fontSize = 20.sp, modifier = Modifier.width(32.dp))
        Text(userProfile.username, fontSize = 20.sp, modifier = Modifier.weight(1f))
        if(isImgRendered) {
            userProfile.bm?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null, // Provide a content description if needed
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }
    }
}
