package com.tarumt.techswift.Login_Signup.UiScreen.SignUp

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tarumt.techswift.Login_Signup.ViewModel.AuthState
import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel
import com.tarumt.techswift.R
import com.tarumt.techswift.User.Datasource.genderList
import com.tarumt.techswift.User.Datasource.roleList
import kotlinx.coroutines.flow.debounce

@Composable
fun SignUpUI(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel()
) {

    val uiState = signUpViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val scrollState = rememberScrollState()

    val authState = authViewModel.authState.observeAsState()

    var showProcessingDialog by remember { mutableStateOf(false) }
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }

    }

    LaunchedEffect(Unit) {
        snapshotFlow { signUpViewModel.address }
            .debounce(300)
            .collect { query ->
                signUpViewModel.loadAddressSuggestion(context)
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
                .fillMaxHeight(0.9f)
                .padding(1.dp),
            shape = RoundedCornerShape(30.dp), // Rounded corners
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .alpha(if (showProcessingDialog) 0.3f else 1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Sign Up Page",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(top = 15.dp)
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = uiState.value.textFieldError[0]

                )
                if (uiState.value.textFieldError[0]) {
                    Text(
                        text = "The email format is not correct!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 9.dp, top = 4.dp)
                    )
                }


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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),

                    isError = uiState.value.textFieldError[1]

                )

                if (uiState.value.textFieldError[1]) {
                    Text(
                        text = "The password cannot be empty!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 9.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                //Name
                UserDetailsField(
                    label = "Name",
                    value = signUpViewModel.name,
                    onValueChange = { signUpViewModel.nameUpdate(it) },
                    keyboardType = KeyboardType.Text,
                    error = uiState.value.textFieldError[2],
                    errorText = "Name cannot be empty!"

                )

                //Phone
                UserDetailsField(
                    label = "Phone",
                    value = signUpViewModel.phone,
                    onValueChange = { signUpViewModel.phoneUpdate(it) },
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Phone,
                    error = uiState.value.textFieldError[3],
                    errorText = "Phone is not in Malaysia Phone format!",
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                //Gender
                RadioButtonSelection(
                    list = genderList,
                    selected = signUpViewModel.gender,
                    onClick = { signUpViewModel.genderUpdate(it) }
                )


                //Address
                AddressTextField(
                    signUpViewModel.address,
                    signUpViewModel.postcode,
                    signUpViewModel.state,
                    onValueChange = {
                        signUpViewModel.addressUpdate(it)
                    },
                    uiState.value.addressSuggestion,
                    onDropdownClick = {
                        signUpViewModel.updateFullAdress(it)

                        signUpViewModel.loadAndFillAddress(
                            address = it,
                            context = context
                        )
                    },
                    error = uiState.value.textFieldError[4],
                    errorText = "Address cannot be null!"
                )

                //Role Radio Button
                RadioButtonSelection(
                    list = roleList,
                    selected = signUpViewModel.role,
                    onClick = { signUpViewModel.roleUpdate(it) }
                )


                Button(
                    onClick = {
                        if (signUpViewModel.validateInputs()) {
                            showProcessingDialog = true
                            val profile = signUpViewModel.updateProfileDetails()
                            val email = signUpViewModel.email
                            val password = signUpViewModel.password

                            authViewModel.signup(
                                email,
                                password,
                                profile,
                                context,
                                onSuccess = {
                                    signUpViewModel.resetValue()
                                    showProcessingDialog = false
                                },
                            )
                        }
                    }

                ) {
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
        if (showProcessingDialog) {
            BackHandler(enabled = true) {
                // Do nothing → this disables the back button
            }
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever, // <- LOOP FOREVER,
                speed = 1.3f
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(180.dp)
            )

        }


        // Prevent scrolling and interactions
        if(showProcessingDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // This will block all user interactions while processing
                        detectTapGestures { }
                    }
            )
        }

    }
}

@Composable
private fun RadioButtonSelection(list: List<String>, selected: String, onClick: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
//
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            list.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected == item,
                        onClick = { onClick(item) }
                    )
                    Text(item)
                }
            }
        }
    }
}

@Composable
private fun UserDetailsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    imeAction: ImeAction = ImeAction.Next,
    error: Boolean,
    errorText: String,
    keyboardActions: KeyboardActions = KeyboardActions()
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
        ),
        keyboardActions = keyboardActions,
        isError = error
    )
    if (error) {
        Text(
            text = errorText,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 9.dp, top = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressTextField(
    address: String,
    postcode: String,
    state: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String> = emptyList(),
    onDropdownClick: (String) -> Unit,
    error: Boolean,
    errorText: String
) {
    var expanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current



    Column(
        modifier = Modifier.fillMaxSize(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded && suggestions.isNotEmpty(),
            onExpandedChange = {
                expanded = it
            }
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = {
                    onValueChange(it)
                    expanded = true
                },
                label = {
                    Text("Address 1")
                },
                modifier = Modifier
                    .menuAnchor(),
                isError = error

            )
            if (error) {
                Text(
                    text = errorText,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 9.dp, top = 4.dp)
                )
            }

            ExposedDropdownMenu(
                expanded = expanded && suggestions.isNotEmpty(),
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = {
                            expanded = false
                            onDropdownClick(suggestion)
                            focusManager.clearFocus()
                        }
                    )
                }
            }

        }


        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = postcode,
            onValueChange = {},
            label = {
                Text("Postcode")
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Gray,

                ),
            readOnly = true,
            enabled = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state,
            onValueChange = {},
            label = {
                Text("State")
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Gray
            ),
            readOnly = true,
            enabled = false
        )
    }

}
