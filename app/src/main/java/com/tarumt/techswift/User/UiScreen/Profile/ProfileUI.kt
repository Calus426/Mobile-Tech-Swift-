package com.tarumt.techswift.User.UiScreen.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.techswift.R
import com.tarumt.techswift.ui.theme.GreenBackground


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUI() {
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

                    HorizontalDivider(modifier =  Modifier.padding(bottom = 3.dp, start = 10.dp, end = 10.dp))

                    Column(
                        horizontalAlignment = Alignment.Start
                    ){
                        Text(
                            "Name",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start
                        )
                        OutlinedTextField(
                            value = "Gem",
                            onValueChange = {},
                            placeholder = {Text("Gem")},
                            colors = OutlinedTextFieldDefaults.colors(
                              focusedContainerColor = Color(0xFFE5E1E1),
                                unfocusedContainerColor = Color(0xFFE5E1E1)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Email: gem@example.com", fontSize = 18.sp)

                    // Add more profile content here
                }

            }
        }


    }


}