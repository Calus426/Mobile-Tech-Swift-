    package com.tarumt.techswift.Login_Signup.UiScreen.SignUp

    import androidx.compose.foundation.Image
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxHeight
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.material3.Button
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.RadioButton
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextButton
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.text.input.ImeAction
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.text.input.PasswordVisualTransformation
    import androidx.compose.ui.text.input.VisualTransformation
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel
    import com.tarumt.techswift.R

    @Composable
    fun SignUpUI(
        authViewModel: AuthViewModel,
        onLoginClick : ()->Unit,
        signUpViewModel: SignUpViewModel = viewModel()
    ) {

        val uiState = signUpViewModel.uiState.collectAsState()
        val context = LocalContext.current
        var passwordVisible by remember { mutableStateOf(false) }

        Box(
        modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.9f)
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
                        text = "Sign Up Page",
                        fontSize = 22.sp
                    )

                    //Email Field
                    OutlinedTextField(
                        value = signUpViewModel.email,
                        onValueChange = {
                            signUpViewModel.emailUpdate(it)
                        },
                        label = {
                            Text("Email")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)

                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    //Password Field
                    OutlinedTextField(
                        value = signUpViewModel.password,
                        onValueChange = {
                            signUpViewModel.passwordUpdate(it)
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

                    UserDetailsField(label = "Name", value =signUpViewModel.name, onValueChange = {signUpViewModel.nameUpdate(it)}, keyboardType = KeyboardType.Text)

                    UserDetailsField(label = "Phone", value =signUpViewModel.phone, onValueChange = {signUpViewModel.phoneUpdate(it)},keyboardType = KeyboardType.Phone)

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Row(verticalAlignment = Alignment.CenterVertically){
                            RadioButton(
                                selected = true,
                                onClick = {}
                            )
                            Text("Male")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically){
                            RadioButton(
                                selected = true,
                                onClick = {}
                            )
                            Text("Female")
                        }

                    }

                    Button(
                        onClick = {authViewModel.signup(
                            signUpViewModel.email,
                            signUpViewModel.password,
                            uiState.value.currentProfile,
                            context
                        )}
                    ){
                        Text(stringResource(R.string.signup))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onLoginClick
                    ) {
                        Text(text = "Already have a account, Login")
                    }

                }

            }
        }
    }

    @Composable
    private fun UserDetailsField(
        label : String ,
        value : String,
        onValueChange : (String) -> Unit,
        keyboardType: KeyboardType,
        imeAction : ImeAction = ImeAction.Next
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(label)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            )

        )
        Spacer(modifier = Modifier.height(8.dp))
    }