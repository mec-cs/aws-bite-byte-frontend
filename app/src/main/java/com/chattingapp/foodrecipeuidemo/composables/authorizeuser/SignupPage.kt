package com.chattingapp.foodrecipeuidemo.composables.authorizeuser

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import com.chattingapp.foodrecipeuidemo.MainActivity
import com.chattingapp.foodrecipeuidemo.credentials.PasswordUtil
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import org.mindrot.jbcrypt.BCrypt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private fun displayToast(msg:String, context: Context){
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}
private fun isEmailValid(email:String): Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
@Composable
fun SignupPage(onSwitchToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
        if(!email.isBlank() && isEmailValid(email) && !username.isBlank() && !password.isBlank() &&
            !confirmPassword.isBlank() && password.equals(confirmPassword) && !password.contains("$")){

            val userProfileDTO = UserProfileDTO(email, PasswordUtil.hashPassword(password), username)

            val apiService = RetrofitHelper.apiService

            apiService.saveUser(userProfileDTO).enqueue(object : Callback<String> {

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            // Display or process the response body for successful cases
                            displayToast(responseBody, context)
                            navigateToMainActivity(context)
                        } else {
                            displayToast("Empty response body", context)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        displayToast("${response.message()} $errorBody", context)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("API_CALL_FAILURE", "Failed to create user", t)
                    displayToast("Failed to create user: Something went wrong!", context)
                }
            })
        }
        else{
            if(!isEmailValid(email)){
                displayToast("Invalid email!", context)
            }
            else if(email.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank()){
                displayToast("Missing information!", context)
            }
            else if(!password.equals(confirmPassword)){
                displayToast("Check your passwords!", context)
            }
            else if(password.contains("$")){
                displayToast("Password cannot contain $ sign!", context)
            }
        }



        }) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onSwitchToLogin) {
            Text("Already have an account? Login")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colors.error)
        }
    }
}

/*fun hashPassword(password: String): String {
    val salt = BCrypt.gensalt()
    Log.d("HASH: ",BCrypt.hashpw(password, salt))
    return BCrypt.hashpw(password, salt)
}*/

private fun navigateToMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}