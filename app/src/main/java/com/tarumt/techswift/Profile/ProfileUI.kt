package com.tarumt.techswift.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tarumt.techswift.R
import com.tarumt.techswift.User.Datasource.genderList
import com.tarumt.techswift.ui.theme.GreenBackground
import com.tarumt.techswift.ui.theme.provider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun ProfileUI(profileViewModel: ProfileViewModel = viewModel()) {
    val uiState = profileViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }


    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(uiState.value.oriProfile.profileAvatar)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.default_avatar2),
        error = painterResource(R.drawable.default_avatar2)
    )


    LaunchedEffect(Unit) {
        snapshotFlow { profileViewModel.address }
            .debounce(300)
            .collect { query ->
                profileViewModel.loadAddressSuggestion(context)
            }
    }

   LaunchedEffect(painter.state){
       when(painter.state){
           is AsyncImagePainter.State.Success -> isLoading = false
           is AsyncImagePainter.State.Error -> isLoading = false
           else -> Unit
       }
   }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        //Check if the image loaded , if not the show loading

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            if (!isLoading || uiState.value.oriProfile.profileAvatar.isEmpty()) {
                Box {
                    Box(
                        modifier = Modifier
                            .height(40.dp) // Use fixed height instead of percentage
                            .fillMaxWidth()
                            .background(GreenBackground)
                    )

                    Image(
                        painter = painter,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.TopCenter)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                }


                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.8f),
                    ) {

                        HorizontalDivider(
                            modifier = Modifier.padding(
                                bottom = 3.dp,
                                start = 10.dp,
                                end = 10.dp,
                                top = 5.dp
                            )
                        )

                        ProfileTextField(
                            "Name",
                            profileViewModel.name,
                            onValueChange = { profileViewModel.nameUpdate(it) }
                        )


                        ProfileTextField(
                            "Email",
                            profileViewModel.email,
                            onValueChange = { profileViewModel.emailUpdate(it) }
                        )

                        ProfileTextField(
                            "Phone",
                            profileViewModel.phone,
                            onValueChange = { profileViewModel.phoneUpdate(it) }
                        )

                        DropdownSelection(
                            "Gender",
                            onValueChange = { profileViewModel.genderUpdate(it) },
                            gender = profileViewModel.gender

                        )

                        AddressTextField(
                            "Address",
                            profileViewModel.address,
                            profileViewModel.postcode,
                            profileViewModel.state,
                            onValueChange = {
                                profileViewModel.addressUpdate(it)
                            },
                            uiState.value.addressSuggestion,
                            onDropdownClick = {
                                profileViewModel.updateFullAdress(it)
                                profileViewModel.loadAndFillAddress(
                                    address = it,
                                    context = context
                                )
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(
                                bottom = 8.dp,
                                start = 10.dp,
                                end = 10.dp,
                                top = 6.dp
                            )
                        )

                        Button(
                            onClick = {
                                profileViewModel.updateProfileDetails(context)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF393D36))
                        ) {
                            Text(
                                text = "Save",
                                fontFamily = FontFamily(
                                    Font(
                                        googleFont = GoogleFont("Poppins"),
                                        fontProvider = provider,
                                        weight = FontWeight.Bold
                                    )
                                ),
                                fontSize = 20.sp
                            )
                        }


                        // Add more profile content here
                    }

                }

            } else {

                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
                val progress by animateLottieCompositionAsState(
                    composition=composition,
                    iterations = LottieConstants.IterateForever, // <- LOOP FOREVER,
                    speed = 1.3f
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .alpha(0f),
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(180.dp)
                    )
                }
            }
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressTextField(
    fieldName: String,
    address: String,
    postcode: String,
    state: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String> = emptyList(),
    onDropdownClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(bottom = 3.dp)
            .fillMaxWidth()
    ) {
        Text(
            fieldName,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(horizontal = 4.dp),
            fontFamily = FontFamily(
                Font(
                    googleFont = GoogleFont("Poppins"),
                    fontProvider = provider,
                    weight = FontWeight.Medium

                )
            )
        )

        Text(
            text = "Address 1",
            fontSize = 12.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(horizontal = 4.dp),
            fontFamily = FontFamily(
                Font(
                    googleFont = GoogleFont("Poppins"),
                    fontProvider = provider,
                    weight = FontWeight.Normal

                )
            )
        )
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFe0d4d4),
                    unfocusedContainerColor = Color(0xFFe0d4d4)
                ),
                modifier = Modifier
                    .height(50.dp)
                    .menuAnchor()
                    .fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 13.sp),
            )

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


        Row(
            modifier = Modifier
                .padding(top = 3.dp)
                .fillMaxWidth()
        ) {
            Column(
                Modifier
                    .weight(0.3f)
            ) {
                Text(
                    text = "Postcode",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    fontFamily = FontFamily(
                        Font(
                            googleFont = GoogleFont("Poppins"),
                            fontProvider = provider,
                            weight = FontWeight.Normal

                        )
                    )
                )
                OutlinedTextField(
                    value = postcode,
                    onValueChange = {},
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFc2aaab),
                        unfocusedContainerColor = Color(0xFFc2aaab)
                    ),
                    modifier = Modifier
                        .height(50.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    readOnly = true
                )
            }

            Column(
                Modifier
                    .weight(0.8f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = "State",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    fontFamily = FontFamily(
                        Font(
                            googleFont = GoogleFont("Poppins"),
                            fontProvider = provider,
                            weight = FontWeight.Normal

                        )
                    )
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = {},
//            placeholder = { Text(placeholder,fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFc2aaab),
                        unfocusedContainerColor = Color(0xFFc2aaab)
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    readOnly = true
                )
            }
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelection(fieldName: String, onValueChange: (String) -> Unit,gender:String="") {

    var selectedText by remember { mutableStateOf(genderList.find { it == gender } ?: genderList.first())  }
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(bottom = 3.dp)
    ) {
        Text(
            fieldName,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(4.dp),
            fontFamily = FontFamily(
                Font(
                    googleFont = GoogleFont("Poppins"),
                    fontProvider = provider,
                    weight = FontWeight.Medium

                )
            )
        )

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .height(45.dp)
                    .fillMaxWidth(),
                value = selectedText,
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFe0d4d4),
                    unfocusedContainerColor = Color(0xFFe0d4d4)
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 13.sp)
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.background(Color(0xFFe0d4d4).copy(alpha = 0.2f))
            ) {
                genderList.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        text = { Text(text = text) },
                        onClick = {
                            selectedText = genderList[index]
                            isExpanded = false
                            onValueChange(selectedText)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }

}

@Composable
private fun ProfileTextField(fieldName: String, value: String, onValueChange: (String) -> Unit) {
    val font = GoogleFont("Poppins")
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(bottom = 3.dp)
            .fillMaxWidth()
    ) {
        Text(
            fieldName,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(4.dp),
            fontFamily = FontFamily(
                Font(
                    googleFont = font,
                    fontProvider = provider,
                    weight = FontWeight.Medium

                )
            )
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFe0d4d4),
                unfocusedContainerColor = Color(0xFFe0d4d4)
            ),
            modifier = Modifier
                .height(47.dp)
                .fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(fontSize = 12.sp)
        )
    }
}

@Preview
@Composable
fun Preview() {
    ProfileUI()
}