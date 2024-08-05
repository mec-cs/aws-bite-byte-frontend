package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ResetPasswordScreen(navController: androidx.navigation.NavController) {
    var password by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Enter your new password",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                resetPassword(password.text, context)
                navController.navigate("login") // Navigate back to login after resetting password
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Password")
        }
    }
}

private fun resetPassword(newPassword: String, context: Context) {
    val apiService = RetrofitHelper.apiService

    apiService.resetPassword(ResetPasswordRequest(newPassword)).enqueue(object : Callback<Boolean> {
        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
            if (response.isSuccessful) {
                Toast.makeText(context, "Password reset successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error resetting password", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Boolean>, t: Throwable) {
            Toast.makeText(context, "Failed to reset password", Toast.LENGTH_SHORT).show()
        }
    })
}

data class ResetPasswordRequest(
    val newPassword: String
)
