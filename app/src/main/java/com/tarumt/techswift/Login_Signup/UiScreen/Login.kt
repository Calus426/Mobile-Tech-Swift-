package com.tarumt.techswift.Login_Signup.UiScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.techswift.Login_Signup.ViewModel.AuthState
import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel
import com.tarumt.techswift.R

@Composable
fun LoginUI(authViewModel: AuthViewModel, onSignUpClick: () -> Unit, onLoginButtonClick:()->Unit){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    val isLoading = authState.value == AuthState.Loading
    val focusManager = LocalFocusManager.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> onLoginButtonClick()
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message,Toast.LENGTH_SHORT
                ).show()
            else -> Unit
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .padding(1.dp),
            shape = RoundedCornerShape(30.dp), // Rounded corners
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){

                Text(
                    text = "Login Page",
                    fontSize = 32.sp
                )

                //Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = {
                        Text("Email")
                    }

                )

                Spacer(modifier = Modifier.height(8.dp))

                //Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                       password = it
                    },
                    label = {
                        Text("Password")
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            R.drawable.baseline_visibility_24
                        else R.drawable.baseline_visibility_off_24

                        Image(
                            painter = painterResource(id = image),
                            contentDescription = "Toggle password visibility",
                            modifier = Modifier
                                .clickable { passwordVisible = !passwordVisible }
                                .padding(8.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        authViewModel.login(email,password)
                        focusManager.clearFocus()
                              },
                    enabled = !isLoading
                ){
                    Text(stringResource(R.string.login))
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onSignUpClick
                ) {
                    Text(text = "Dont have an account, Signup")
                }

                if (isLoading) {
                    CircularProgressIndicator(color = Color.Black,
                        strokeWidth = 4.dp,
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center)
                            .size(50.dp)
                    )
                }
            }

        }
    }


}