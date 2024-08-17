package com.chattingapp.foodrecipeuidemo.composables.authorizeuser

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.chattingapp.foodrecipeuidemo.activity.ui.theme.MyAppTheme
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.emailvalidator.EmailValidator
import com.chattingapp.foodrecipeuidemo.viewmodel.ForgotPasswordViewModel
import kotlinx.coroutines.delay

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

    MyAppTheme {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Make the background transparent
                )

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
                Button(
                    onClick = {
                        if(email.value.trim() != "" && EmailValidator.isEmailValid(email.value.trim())){
                            viewModel.checkUserExistsByEmail(email.value.trim())
                        }
                        else{
                            displayToast("Please enter a valid email!", context)
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    enabled = isInputEnabled.value, // Disable or enable the button based on state
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isInputEnabled.value) ButtonDefaults.buttonColors().backgroundColor(enabled = true).value
                        else Color.Blue.copy(alpha = 0.5f)
                    ),
                ) {
                    Text("Send a code", color = if (isInputEnabled.value) Color.White else Color.Black)
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

                Button(
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
                    Text("Verify my account", color = Color.White)
                }

                Button(
                    onClick = {
                        // Logic to resend the code
                        viewModel.sendEmail(email.value.trim())
                        resendAttempts++
                    },
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isButtonEnabled) ButtonDefaults.buttonColors().backgroundColor(enabled = true).value
                        else Color.Blue.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                ) {
                    Text(timerText, color = if (isButtonEnabled) Color.White else Color.Black)
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
                        isPasswordMatch.value = password.value == confirmPassword.value
                        if(password.value.trim().isNotBlank() && confirmPassword.value.trim().isNotBlank() && isPasswordMatch.value &&
                            password.value.length >= Constant.MINIMUM_PASSWORD_SIZE){

                                //displayToast("pw matches", context)

                                viewModel.changePassword(
                                    email = email.value.trim(),
                                    newPassword = confirmPassword.value.trim(),
                                    onSuccess = {
                                        displayToast("Password changed successfully.", context)
                                        navController.popBackStack("login", false, true)
                                    },
                                    onError = { _ ->
                                        displayToast("Something went wrong!", context)
                                    }
                                )


                        }
                        else{
                            if(password.value.trim().isBlank() || confirmPassword.value.trim().isBlank()){
                                displayToast("Missing field(s)!", context)
                            }
                            else if(!isPasswordMatch.value){
                                displayToast("Passwords should match!", context)
                            }
                            else if(password.value.length < Constant.MINIMUM_PASSWORD_SIZE){
                                displayToast("Password should contain minimum ${Constant.MINIMUM_PASSWORD_SIZE} characters!", context)
                            }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 10.dp)
                ) {
                    Text(text = "Change Password", color = Color.White)
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

}


