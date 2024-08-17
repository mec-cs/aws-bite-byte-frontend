package com.chattingapp.foodrecipeuidemo.composables.authorizeuser

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chattingapp.foodrecipeuidemo.MainActivity
import com.chattingapp.foodrecipeuidemo.composables.placeholder.LoginLogo
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.credentials.PasswordUtil
import com.chattingapp.foodrecipeuidemo.emailvalidator.EmailValidator
import com.chattingapp.foodrecipeuidemo.entity.UserProfileDTO
import com.chattingapp.foodrecipeuidemo.viewmodel.UserProfileViewModel

private fun displayToast(msg:String, context: Context){
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

@Composable
fun SignupPage(onSwitchToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val viewModel: UserProfileViewModel = viewModel()

    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                LoginLogo()


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
                    onValueChange = { newPassword ->
                        // Remove any spaces from the input
                        password = newPassword.replace(" ", "")
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { newPassword ->
                        // Remove any spaces from the input
                        confirmPassword = newPassword.replace(" ", "")
                    },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    if(email.trim().isNotBlank() && EmailValidator.isEmailValid(email) && username.trim().isNotBlank() && password.trim().isNotBlank() &&
                        confirmPassword.trim().isNotBlank() && password == confirmPassword && !password.contains("$") && password.length >= Constant.MINIMUM_PASSWORD_SIZE){


                        val userProfileDTO = UserProfileDTO(email, PasswordUtil.hashPassword(password), username)

                        viewModel.saveUser(userProfileDTO,
                            onSuccess = { responseBody ->
                                displayToast(responseBody, context)
                                navigateToMainActivity(context)
                            },
                            onError = { error ->
                                displayToast("Something went wrong!", context)
                            }
                        )
                    }
                    else{
                        if(!EmailValidator.isEmailValid(email)){
                            displayToast("Invalid email!", context)
                        }
                        else if(email.trim().isBlank() || username.trim().isBlank() || password.trim().isBlank() || confirmPassword.trim().isBlank()){
                            displayToast("Missing information!", context)
                        }
                        else if(!password.equals(confirmPassword)){
                            displayToast("Check your passwords!", context)
                        }
                        else if(password.contains("$")){
                            displayToast("Password cannot contain $ sign!", context)
                        }
                        else if(password.length < Constant.MINIMUM_PASSWORD_SIZE){
                            displayToast("Password should contain minimum ${Constant.MINIMUM_PASSWORD_SIZE} characters", context)
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
    }

}

private fun navigateToMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}

