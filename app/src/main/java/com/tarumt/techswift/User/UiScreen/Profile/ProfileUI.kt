package com.tarumt.techswift.User.UiScreen.Profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.tarumt.techswift.R
import com.tarumt.techswift.User.Datasource.genderList
import com.tarumt.techswift.ui.theme.GreenBackground
import com.tarumt.techswift.ui.theme.provider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUI(profileViewModel: ProfileViewModel = viewModel()) {
    val uiState = profileViewModel.uiState.collectAsState()

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Profile Image (overlapping top bar from MainScreen)

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.1f)
                        .fillMaxWidth()
                        .background(GreenBackground)
                )

                Image(
                    painter = painterResource(id = R.drawable.gem),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.TopCenter)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                )

            }

            Box(
                Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    HorizontalDivider(
                        modifier = Modifier.padding(
                            bottom = 3.dp,
                            start = 10.dp,
                            end = 10.dp
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
                        onValueChange = { profileViewModel.genderUpdate(it) }
                    )
                    AddressTextField(
                        "Address",
                        profileViewModel.address,
                        profileViewModel.postcode,
                        profileViewModel.state,
                        onValueChange = {
                            profileViewModel.addressUpdate(it)
                            profileViewModel.loadAddressSuggestion(context)
                        },
                        uiState.value.addressSuggestion,
                        onDropdownClick = {profileViewModel.loadAndFillAddress(address = it,context=context)}
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(
                            bottom = 3.dp,
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
                            .fillMaxWidth(0.8f)
                            .size(50.dp),
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

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(bottom = 3.dp)
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
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = address,
                onValueChange ={
                    onValueChange(it)
                    expanded = true
                } ,
//            placeholder = { Text(placeholder,fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFe0d4d4),
                    unfocusedContainerColor = Color(0xFFe0d4d4)
                ),
                modifier = Modifier
                    .height(45.dp)
                    .menuAnchor(),
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
                        }
                    )
                }
            }

        }


        Row(
            modifier = Modifier.padding(top = 3.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(0.2f)
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
//            placeholder = { Text(placeholder,fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFc2aaab),
                        unfocusedContainerColor = Color(0xFFc2aaab)
                    ),
                    modifier = Modifier
                        .height(45.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    readOnly = true
                )
            }
            // Spacer(modifier = Modifier.fillMaxWidth(0.1f))

            Column(
                Modifier.fillMaxWidth(0.1f)
            ) {

            }
            Column(
                Modifier.fillMaxWidth(0.7f)
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
                        .height(45.dp),
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
fun DropdownSelection(fieldName: String, onValueChange: (String) -> Unit) {

    var selectedText by remember { mutableStateOf(genderList[0]) }
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
                    .height(45.dp),
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
//            placeholder = { Text(placeholder,fontSize = 13.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFe0d4d4),
                unfocusedContainerColor = Color(0xFFe0d4d4)
            ),
            modifier = Modifier
                .height(45.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(fontSize = 13.sp),
        )
    }
}

@Preview
@Composable
fun Preview() {
    ProfileUI()
}