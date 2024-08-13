package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper.apiService
import com.chattingapp.foodrecipeuidemo.viewmodel.CreateRecipeViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateRecipeScreen(navController: NavHostController) {
    var recipeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var cuisine by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val createRecipeViewModel: CreateRecipeViewModel = viewModel()
    val isLoadingCreateRecipe by createRecipeViewModel.isLoading.collectAsState()
    val isSuccessCreateRecipe by createRecipeViewModel.isSuccess.collectAsState()

    fun showSuccessMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(isSuccessCreateRecipe) {
        if (isSuccessCreateRecipe) {
            recipeName = ""
            description = ""
            cuisine = ""
            course = ""
            diet = ""
            prepTime = ""
            ingredients = ""
            instructions = ""
            imageUri = null
            selectedImageFile = null
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            copyUriToFile(it, navController.context.contentResolver) { file ->
                selectedImageFile = file
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp , bottom =  0.dp, end = 0.dp, start = 0.dp)
                //.imePadding()
        ) {

            item {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        imageUri = uri
                        uri?.let {
                            copyUriToFile(it, navController.context.contentResolver) { file ->
                                selectedImageFile = file
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { launcher.launch("image/*") }
                            .background(Color.White), // This is applied directly to Box
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Add photo",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }
                    Text("Add photo")
                }
            }

            item {
                OutlinedTextField(
                    value = recipeName,
                    onValueChange = { recipeName = it },
                    label = { Text("Recipe Name") },
                    placeholder = { Text("Enter the masterpiece's name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Recipe Description") },
                    placeholder = { Text("Enter a description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = cuisine,
                    onValueChange = { cuisine = it },
                    label = { Text("Recipe Cuisine") },
                    placeholder = { Text("Enter cuisine of the recipe") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = { Text("Recipe Course") },
                    placeholder = { Text("Enter course") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = diet,
                    onValueChange = { diet = it },
                    label = { Text("Diet Type") },
                    placeholder = { Text("Enter diet types if any") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = prepTime,
                    onValueChange = {
                            newValue ->
                        // Allow digits, dot, and comma
                        val filteredValue = newValue.filter { char ->
                            char.isDigit() || char == '.'
                        }

                        // Remove leading dots or commas
                        val cleanedValue = if (filteredValue.isNotEmpty() && (filteredValue.first() == '.')) {
                            filteredValue.drop(1)
                        } else {
                            filteredValue
                        }

                        // Remove consecutive dots and commas
                        val finalValue = buildString {
                            var lastChar: Char? = null
                            for (char in cleanedValue) {
                                if (char == '.') {
                                    if (lastChar != char) {
                                        append(char)
                                    }
                                } else {
                                    append(char)
                                }
                                lastChar = char
                            }
                        }

                        // Ensure there's only one dot or comma
                        val formattedValue = buildString {
                            var seenDotOrComma = false
                            for (char in finalValue) {
                                if (char == '.') {
                                    if (!seenDotOrComma) {
                                        append(char)
                                        seenDotOrComma = true
                                    }
                                } else {
                                    append(char)
                                    seenDotOrComma = false
                                }
                            }
                        }

                        prepTime = formattedValue
                                    },
                    label = { Text("Preparation Time") },
                    placeholder = { Text("Enter time") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        Text(
                            text = "Please add numbers in minutes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    label = { Text("Recipe Ingredients") },
                    placeholder = { Text("Enter ingredients and inventory to be used") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            item {
                val keyboardController = LocalSoftwareKeyboardController.current

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Recipe Instructions") },
                    placeholder = { Text("Enter instructions in detail") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .imePadding(), // Adjust padding for keyboard
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide() // Hide the keyboard when done
                        }
                    )
                )
            }

            item {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            if (recipeName.isNotEmpty() && description.isNotEmpty() &&
                                cuisine.isNotEmpty() && course.isNotEmpty() &&
                                diet.isNotEmpty() && prepTime.isNotEmpty() &&
                                ingredients.isNotEmpty() && instructions.isNotEmpty() &&
                                selectedImageFile != null
                            ) {
                                // Call the createRecipe function from the ViewModel
                                createRecipeViewModel.createRecipe(
                                    recipeName = recipeName,
                                    description = description,
                                    cuisine = cuisine,
                                    course = course,
                                    diet = diet,
                                    prepTime = prepTime,
                                    ingredients = ingredients,
                                    instructions = instructions,
                                    selectedImageFile = selectedImageFile
                                )

                            } else {
                                showSuccessMessage("Please fill out all fields.")
                            }
                        },
                        enabled = !isLoadingCreateRecipe,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(bottom = 400.dp)
                    ) {
                        Text("Create Recipe")
                    }

                }

            }
        }

        if (isLoadingCreateRecipe) {
            // Show a loading indicator or dialog
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}



fun copyUriToFile(uri: Uri, contentResolver: ContentResolver, onFileCopied: (File) -> Unit) {
    // Open an input stream from the given Uri
    val inputStream = contentResolver.openInputStream(uri)

    // Decode the input stream into a Bitmap
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    // Resize the bitmap to 350x175 pixels
    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 350, 175, true)

    // Create a temporary file to store the resized image
    val file = File.createTempFile("image", ".jpg")

    // Compress the resized bitmap into the file
    FileOutputStream(file).use { outputStream ->
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    }

    // Call the callback with the resized file
    onFileCopied(file)
}