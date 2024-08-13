package com.chattingapp.foodrecipeuidemo.composables.authorizeuser

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.credentials.PasswordUtil
import com.chattingapp.foodrecipeuidemo.emailvalidator.EmailValidator
import com.chattingapp.foodrecipeuidemo.entity.ChangePasswordRequest
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import com.chattingapp.foodrecipeuidemo.viewmodel.ForgotPasswordViewModel
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var serverCode = -761458


private fun displayToast(msg:String, context: Context){
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassword(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val isInputEnabled = remember { mutableStateOf(true) }
    val displayPasswordChangeScreen = remember { mutableStateOf(false) }
    val isSendButtonEnabled = remember { mutableStateOf(true) }
    val displayMailSender = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: ForgotPasswordViewModel = viewModel()
    val userExists by viewModel.userExists.collectAsState()
    val isFirstTime = remember { mutableStateOf(true) }


    Column(modifier = Modifier.padding(16.dp)) {

        TopAppBar(
            title = { Text(text = "Forgot my password") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack("login", false, true)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email Address") },
            placeholder = { Text("Email Address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = isInputEnabled.value // Disable or enable input based on state
        )


        if(isSendButtonEnabled.value) {
            ElevatedButton(
                onClick = {
                    if(email.value.trim() != "" && EmailValidator.isEmailValid(email.value.trim())){
                        viewModel.checkUserExistsByEmail(email.value.trim())
                    }
                    else{
                        displayToast("Please enter a valid email!", context)
                    }
                },
                modifier = Modifier.padding(top = 16.dp),
                enabled = isInputEnabled.value // Disable or enable the button based on state
            ) {
                Text("Send a code")
            }
        }

        LaunchedEffect(userExists) {
            if(!isFirstTime.value){
                userExists?.let {
                    if (it) {
                        //displayToast("User exists!", context)
                        isInputEnabled.value = false
                        displayMailSender.value = true
                        viewModel.sendEmail(email.value.trim())
                    } else {
                        displayToast("Email not found!", context)
                    }
                }
            }
            isFirstTime.value = false
        }

        if(displayMailSender.value){
            var userCode by remember { mutableStateOf(TextFieldValue("")) }

            var isButtonEnabled by remember { mutableStateOf(false) }
            var timerText by remember { mutableStateOf("Resend code in 60 seconds") }
            val countdownTime = 60 // seconds
            val serverCode by viewModel.serverCode.collectAsState()

            var resendAttempts by remember { mutableStateOf(0) }

            // Timer logic
            LaunchedEffect(resendAttempts) {
                isButtonEnabled = false
                for (i in countdownTime downTo 1) {
                    timerText = "Resend the code in $i seconds"
                    delay(1000L)
                }
                timerText = "Resend Code"
                isButtonEnabled = true
            }



                Text(
                    text = "We have sent you an email. Please enter your verification code!",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp)
                )

                OutlinedTextField(

                    value = userCode,
                    onValueChange = { newTextFieldValue ->
                        val filteredText = newTextFieldValue.text.filter { it.isDigit() }
                        userCode = newTextFieldValue.copy(text = filteredText)

                        //userCode = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    placeholder = { Text("Enter your verification code") }
                    ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                ElevatedButton(
                    onClick = {
                        if (userCode.text == serverCode.toString()) {

                            //displayToast("correct", context)
                            displayMailSender.value = false
                            displayPasswordChangeScreen.value = true

                        } else {
                            displayToast("Please check your code!", context)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify my account")
                }

                Button(
                    onClick = {
                        // Logic to resend the code
                        viewModel.sendEmail(email.value.trim())
                        resendAttempts++
                    },
                    enabled = isButtonEnabled,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(timerText)
                }
        }
        
        if(displayPasswordChangeScreen.value){
            val password = remember { mutableStateOf("") }
            val confirmPassword = remember { mutableStateOf("") }
            val isPasswordMatch = remember { mutableStateOf(true) }

            OutlinedTextField(
                value = password.value,
                onValueChange = { newPassword ->
                    // Remove any spaces from the input
                    password.value = newPassword.replace(" ", "")
                },
                label = { Text("New Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                visualTransformation = PasswordVisualTransformation() // Mask the password input
            )

            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { newPassword ->
                    // Remove any spaces from the input
                    confirmPassword.value = newPassword.replace(" ", "")
                },
                label = { Text("Confirm New Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                visualTransformation = PasswordVisualTransformation() // Mask the confirm password input
            )

            Button(
                onClick = {
                    if(password.value.trim().isNotBlank() && confirmPassword.value.trim().isNotBlank()){
                        isPasswordMatch.value = password.value == confirmPassword.value
                        if(isPasswordMatch.value){
                            //displayToast("pw matches", context)


                            val apiService = RetrofitHelper.apiService
                            val request = ChangePasswordRequest(email.value, PasswordUtil.hashPassword(confirmPassword.value))

                            apiService.changePassword(request).enqueue(object : Callback<Boolean> {
                                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                                    if (response.isSuccessful) {
                                        val success = response.body() ?: false
                                        if (success) {
                                            // Password change successful
                                            displayToast("Password changed successfully.", context)
                                            navController.popBackStack("login", false, true)
                                        } else {
                                            // Password change failed
                                            displayToast("Failed to change password.", context)
                                        }
                                    } else {
                                        displayToast("Something went wrong!", context)
                                    }
                                }

                                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                    displayToast("Failed to change password!", context)
                                }
                            })
                        }
                    }
                    else{
                        displayToast("Missing field(s)!", context)
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 10.dp)
            ) {
                Text(text = "Change Password")
            }

            if (!isPasswordMatch.value) {
                Text(
                    text = "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

    }
}


