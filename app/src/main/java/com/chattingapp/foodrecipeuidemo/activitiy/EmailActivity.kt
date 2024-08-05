package com.chattingapp.foodrecipeuidemo.activitiy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.activitiy.ui.theme.FoodRecipeUiDemoTheme
import com.chattingapp.foodrecipeuidemo.constant.Constant
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
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

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "We have sent you an email. Please enter your verification code!",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
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
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify my account")
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


private fun navigateToHomePageActivity(context: Context) {
    Toast.makeText(context, "Email is verified!", Toast.LENGTH_SHORT).show()
    val intent = Intent(context, HomePageActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}
