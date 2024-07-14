package com.chattingapp.foodrecipeuidemo.composables.authorizeuser

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.chattingapp.foodrecipeuidemo.activitiy.EmailActivity
import com.chattingapp.foodrecipeuidemo.activitiy.HomePageActivity
import com.chattingapp.foodrecipeuidemo.entity.User
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


private fun displayToast(msg:String, context: Context){
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

@Composable
fun LoginPage(onSwitchToSignup: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )
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
            lateinit var user:User
            user = User(email, password, null)
            if (rememberMe) {
                val token = generateToken()
                Log.d("TOKEN", token)
                storeToken(token, context)
                user = User(email, password, token)

            }

            val apiService = RetrofitHelper.apiService

            apiService.checkLoginCredentials(user).enqueue(object : Callback<User> {

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            // Display or process the response body for successful cases
                            if(response.body()!!.verified)
                                navigateToHomePageActivity(context)
                            else
                                navigateToEmailActivity(context)

                        } else {
                            displayToast("Please check your email or password!", context)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        displayToast("${response.message()} $errorBody", context)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("API_CALL_FAILURE", "Failed to create user", t)
                    displayToast("Failed to create user: Something went wrong!", context)
                }
            })







        }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))



        TextButton(onClick = onSwitchToSignup) {
            Text("Don't have an account? Sign up")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colors.error)
        }
    }
}

fun generateToken(): String {
    return UUID.randomUUID().toString()
}

fun storeToken(token: String, context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("auth_token", token)
    editor.apply()
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