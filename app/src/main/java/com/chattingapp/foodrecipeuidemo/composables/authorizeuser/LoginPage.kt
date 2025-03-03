package com.chattingapp.foodrecipeuidemo.composables.authorizeuser

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.activity.EmailActivity
import com.chattingapp.foodrecipeuidemo.activity.HomePageActivity
import com.chattingapp.foodrecipeuidemo.composables.placeholder.LoginLogo
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.emailvalidator.EmailValidator
import com.chattingapp.foodrecipeuidemo.entity.AuthenticationDTO
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun LoginPage(
    onSwitchToSignup: () -> Unit,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LoginLogo()

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { newPassword ->
                // Remove any spaces from the input
                password = newPassword.replace(" ", "")
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )

        TextButton(onClick = {
            navController.navigate("forgot my password")
        }) {
            Text(text = "Forgot my password")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Remember Me")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if(email.isNotBlank() && EmailValidator.isEmailValid(email) && password.isNotBlank()){
                coroutineScope.launch {
                    loginUser(email, password, rememberMe, context)
                }
            }
            else{
                displayToast("Please check your email and password!", context)
            }

        }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onSwitchToSignup) {
            Text("Don't have an account? Sign up")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = androidx.compose.material.MaterialTheme.colors.error)
        }
    }
}

private suspend fun loginUser(
    email: String,
    password: String,
    rememberMe: Boolean,
    context: Context
) {
    val authenticationDTO = AuthenticationDTO()
    authenticationDTO.email = email
    authenticationDTO.password=password
    if (rememberMe) {
        authenticationDTO.token = generateToken().also {
            storeToken(it, context)
        }
    }

    val apiService = RetrofitHelper.apiService
    try {
        val response = apiService.checkLoginCredentials(authenticationDTO)
        Constant.user = response
        if (response.verified) {
            navigateToHomePageActivity(context)
        } else {
            navigateToEmailActivity(context)
        }
    } catch (e: Exception) {
        deleteToken(context)
        displayToast("Please check your email and password!", context)
        Log.e("API_CALL_FAILURE", "Failed to login", e)
    }
}

private fun generateToken(): String {
    return UUID.randomUUID().toString()
}

private fun deleteToken(context: Context) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("auth_token").apply()
}

private fun storeToken(token: String, context: Context) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("auth_token", token).apply()
}

private fun navigateToHomePageActivity(context: Context) {
    val intent = Intent(context, HomePageActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}

private fun navigateToEmailActivity(context: Context) {
    val intent = Intent(context, EmailActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}

private fun displayToast(msg: String, context: Context) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}
