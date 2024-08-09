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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper.apiService
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
    var showDialog by remember { mutableStateOf(false) }
    var nullSave by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    fun showSuccessMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
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

    fun createRecipe() {
        coroutineScope.launch {
            val namePart = recipeName.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val cuisinePart = cuisine.toRequestBody("text/plain".toMediaTypeOrNull())
            val coursePart = course.toRequestBody("text/plain".toMediaTypeOrNull())
            val dietPart = diet.toRequestBody("text/plain".toMediaTypeOrNull())
            val prepTimePart = prepTime.toRequestBody("text/plain".toMediaTypeOrNull())
            val ingredientsPart = ingredients.toRequestBody("text/plain".toMediaTypeOrNull())
            val instructionsPart = instructions.toRequestBody("text/plain".toMediaTypeOrNull())
            val imageUriPart = selectedImageFile?.name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val ownerIdPart = Constant.userProfile.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = "true".toRequestBody("text/plain".toMediaTypeOrNull())


            val imageFile = selectedImageFile?.let {
                MultipartBody.Part.createFormData("file", it.name, it.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            }

            apiService.createTheRecipe(
                file = imageFile!!,
                name = namePart,
                description = descriptionPart,
                cuisine = cuisinePart,
                course = coursePart,
                diet = dietPart,
                prepTime = prepTimePart,
                ingredients = ingredientsPart,
                instructions = instructionsPart,
                image = imageUriPart!!,
                ownerId = ownerIdPart,
                type = typePart
            ).enqueue(object : Callback<Recipe> {
                override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                    if (response.body() == null) {
                        Log.d("Save NULL Response", "Response is null, please check conditions")
                        nullSave = true
                    } else if (response.isSuccessful) {
                        Log.d("Recipe Created, HTTP: " + response.code(), response.body().toString())
                        nullSave = false
                        showSuccessMessage("Your recipe successfully created!")

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

                    } else {
                        Log.d("onResponse Fail", "Response Unsuccessful!")
                        nullSave = false
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    Log.d("onFailure", "Fail to call the API function")
                    nullSave = false
                }
            })
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
                .imePadding()
        ) {

            item {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val launcherCreate = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        imageUri = uri
                        uri?.let {
                            copyUriToFile(it, navController.context.contentResolver) { file ->
                                selectedImageFile = file
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White)
                            .clickable { launcher.launch("image/*") },
                        shape = CircleShape
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
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
                                    modifier = Modifier.size(30.dp)
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
                    onValueChange = { prepTime = it },
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
                                createRecipe()
                            } else {
                                showSuccessMessage("Please fill out all fields.")
                            }
                        },
                        modifier = Modifier.width(150.dp).padding(bottom = 400.dp)
                    ) {
                        Text("Create Recipe")
                    }
                }
            }
        }
    }
}

/*fun copyUriToFile(uri: Uri, contentResolver: ContentResolver, onFileCopied: (File) -> Unit) {
    val inputStream = contentResolver.openInputStream(uri)
    val file = File.createTempFile("image", ".jpg")

    inputStream?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }

    onFileCopied(file)
}*/

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