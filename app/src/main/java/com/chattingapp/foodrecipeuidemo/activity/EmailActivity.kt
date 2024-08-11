package com.chattingapp.foodrecipeuidemo.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chattingapp.foodrecipeuidemo.MainActivity
import com.chattingapp.foodrecipeuidemo.activity.ui.theme.FoodRecipeUiDemoTheme
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import com.chattingapp.foodrecipeuidemo.viewmodel.TokenViewModel
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var serverCode = -761458

class EmailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodRecipeUiDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    sendEmail()
                    VerificationCodeUI()
                }
            }
        }
    }


}
@Composable
fun VerificationCodeUI() {
    var userCode by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    var isButtonEnabled by remember { mutableStateOf(false) }
    var timerText by remember { mutableStateOf("Resend code in 60 seconds") }
    val countdownTime = 60 // seconds

    var resendAttempts by remember { mutableStateOf(0) }
    val tokenViewModel: TokenViewModel = viewModel()


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

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "We have sent you an email. Please enter your verification code!",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = userCode,
            onValueChange = {
                userCode = it
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 16.dp, top = 8.dp)
                .size(50.dp)
            ,
            placeholder = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Enter code here",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        )

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    if (userCode.text == serverCode.toString()) {

                        val apiService = RetrofitHelper.apiService

                        apiService.verifyUser(Constant.user.email).enqueue(object : Callback<Boolean> {
                            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                                if(response.body() == true){
                                    navigateToHomePageActivity(context)
                                }
                            }

                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                t.message?.let { Log.d("EMAIL VERIFY ERROR:", it) }
                            }

                        })

                    } else {
                        displayToast("Please check your code!", context)
                    }
                }
            ) {
                Text("Verify the account")
            }
        }

        Button(
            onClick = {
                // Logic to resend the code
                sendEmail()
                resendAttempts++
            },
            enabled = isButtonEnabled,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(timerText)
        }

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    val token =
                        retrieveToken(context)
                    if (token != null) {
                        deleteToken(context)

                        tokenViewModel.deleteToken(Constant.user.id, token)
                    }
                    navigateToMainActivity(
                        context
                    )
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Logout")
            }

        }

    }

}

private fun displayToast(msg:String, context: Context){
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

private fun sendEmail(){
    val apiService = RetrofitHelper.apiService

    apiService.sendVerificationEmail(Constant.user.email).enqueue(object : Callback<Int> {
        override fun onResponse(call: Call<Int>, response: Response<Int>) {
            serverCode = response.body()!!
        }

        override fun onFailure(call: Call<Int>, t: Throwable) {

        }


    })
}

//@Preview
//@Composable
//private fun displayEmailAct() {
//    VerificationCodeUI()
//}


private fun navigateToHomePageActivity(context: Context) {
    Toast.makeText(context, "Email is verified!", Toast.LENGTH_SHORT).show()
    val intent = Intent(context, HomePageActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}

private fun navigateToMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}
fun retrieveToken(context: Context): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("auth_token", null)
}
fun deleteToken(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("auth_token") // Remove the token
    editor.apply() // Commit changes
}