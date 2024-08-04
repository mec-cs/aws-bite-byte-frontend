package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import android.content.ContentResolver
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.Recipe
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import java.io.InputStream

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

    // Function to create recipe
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

            val apiService = // Initialize your API service here

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color.Transparent)
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
                        painter = painterResource(id = R.drawable.spaghetti_bolognese),
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
                value = description,
                onValueChange = { description = it },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                decorationBox = { innerTextField ->
                    if (description.isEmpty()) {
                        Text("Enter Description", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
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
                value = cuisine,
                onValueChange = { cuisine = it },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                decorationBox = { innerTextField ->
                    if (cuisine.isEmpty()) {
                        Text("Enter Cuisine", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
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
                value = course,
                onValueChange = { course = it },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                decorationBox = { innerTextField ->
                    if (course.isEmpty()) {
                        Text("Enter Course", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
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
                value = diet,
                onValueChange = { diet = it },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                decorationBox = { innerTextField ->
                    if (diet.isEmpty()) {
                        Text("Enter Diet", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
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
                value = prepTime,
                onValueChange = { prepTime = it },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                decorationBox = { innerTextField ->
                    if (prepTime.isEmpty()) {
                        Text("Enter Preparation Time", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
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
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {
                    if (imageUri == null || recipeName.isEmpty() || description.isEmpty() || cuisine.isEmpty() || course.isEmpty() ||
                        diet.isEmpty() || prepTime.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()
                    ) {
                        showDialog = true
                    } else if (nullSave) {
                        nullSave = true
                    } else {
                        createRecipe()
                        showSuccessMessage("Your recipe successfully created!")
                    }
                }) {
                    Text("Publish")
                }
            }

            Log.d("NULL TF", "SaveRecipe=$nullSave")

            if (showDialog) {
                ShowAlertDialog(
                    title = "Warning",
                    message = Constant.CREATE_ERROR_DIALOG,
                    onConfirm = { showDialog = false }
                )
            }

            if (nullSave) {
                ShowAlertDialog(
                    title = "Null Error",
                    message = Constant.NULL_EXCEPTION_ERROR,
                    onConfirm = {
                        nullSave = false
                    }
                )
            }
        }
    }
}

fun copyUriToFile(uri: Uri, contentResolver: ContentResolver, onFileCreated: (File) -> Unit) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    coroutineScope.launch {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg")
            val outputStream = FileOutputStream(tempFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            onFileCreated(tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun File.toUri(): Uri = Uri.fromFile(this)

@Composable
fun ShowAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String = "OK",
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onConfirm() },
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        }
    )
}