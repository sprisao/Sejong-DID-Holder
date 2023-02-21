package com.example.did_holder_app.ui.viewmodel

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.did_holder_app.data.model.VC.SignUpRequest
import timber.log.Timber

@Composable
fun SignUpScreen(navController: NavController, viewModel: DIDViewModel) {
    val context = LocalContext.current
    Column {
        Text(text = "SignUpScreen")
        UserSignupScreen(onSignup = { user ->
            viewModel.signUpUser(user) {
                if (it.isSuccessful) {
                    if (it.body()?.code == 0) {
                        Toast.makeText(context, "회원가입 완료", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "실패 : ${it.body()?.msg}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "실패 : ${it.body()?.msg}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}

@Composable
fun UserSignupScreen(onSignup: (SignUpRequest) -> Unit) {
    var userid: String by remember { mutableStateOf("androidTest") }
    var userpass: String by remember { mutableStateOf("androidTest1") }
    var username: String by remember { mutableStateOf("android") }
    var userphoneno: String by remember { mutableStateOf("01000000000") }
    var jobposition: String by remember { mutableStateOf("android") }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = userid,
            onValueChange = { userid = it },
            label = { Text("User ID") },
            singleLine = true
        )

        OutlinedTextField(
            value = userpass,
            onValueChange = { userpass = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("User Name") },
            singleLine = true
        )

        OutlinedTextField(
            value = userphoneno,
            onValueChange = { userphoneno = it },
            label = { Text("Phone Number") },
            singleLine = true
        )

        OutlinedTextField(
            value = jobposition,
            onValueChange = { jobposition = it },
            label = { Text("Job Position") },
            singleLine = true
        )


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Timber.d("Sign Up")
                val user = SignUpRequest(userid, userpass, username, userphoneno, jobposition)
                Timber.d(user.toString())
                onSignup(user)
            }
        ) {
            Text("Sign Up")
        }
    }
}
