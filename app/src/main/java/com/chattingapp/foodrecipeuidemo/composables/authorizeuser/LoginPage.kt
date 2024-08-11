package com.chattingapp.foodrecipeuidemo.composables.authorizeuser

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.activity.EmailActivity
import com.chattingapp.foodrecipeuidemo.activity.HomePageActivity
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.entity.AuthenticationDTO
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
fun LoginPage(onSwitchToSignup: () -> Unit, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.yumbyte_logo),
                contentDescription = "Yumbyte Logo",
                modifier = Modifier
                    .height(250.dp) // Adjust the size as needed
            )
        }


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )


        TextButton(onClick = {
            // Handle the click event here
            //navigateToForgotPasswordActivity(context)
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
            val authenticationDTO = AuthenticationDTO()

            authenticationDTO.email = email
            authenticationDTO.password = password

            var token = ""


            if (rememberMe) {
                token = generateToken()
                Log.d("TOKEN", token)

                authenticationDTO.token = token


            }
            val apiService = RetrofitHelper.apiService

            apiService.checkLoginCredentials(authenticationDTO).enqueue(object : Callback<User> {

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Constant.user = response.body()!!
                            if(rememberMe){
                                storeToken(token, context)
                            }
                            // Display or process the response body for successful cases
                            if(response.body()!!.verified)
                                navigateToHomePageActivity(context)
                            else
                                navigateToEmailActivity(context)

                        } else {
                            deleteToken(context)
                            displayToast("Please check your email or password!", context)
                        }
                    } else {
                        deleteToken(context)
                        val errorBody = response.errorBody()?.string()
                        displayToast("${response.message()} $errorBody", context)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("API_CALL_FAILURE", "Failed to create user", t)
                    displayToast("Failed to login: Check your email and password", context)
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


@Preview
@Composable
private fun displayLoginPage() {
    LoginPage(onSwitchToSignup = { /*TODO*/ }, navController = rememberNavController())
}


fun generateToken(): String {
    return UUID.randomUUID().toString()
}
fun deleteToken(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("auth_token") // Remove the token
    editor.apply() // Commit changes
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
