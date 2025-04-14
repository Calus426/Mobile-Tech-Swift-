package com.tarumt.techswift.User.UiScreen.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.techswift.R


@Composable
fun ProfileUI(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Profile Image (overlapping top bar from MainScreen)
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
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

                // Main Content (push below the image)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp, start = 24.dp, end = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(80.dp)) // space below image

                    Text("Name: Gem", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Email: gem@example.com", fontSize = 18.sp)

                    // Add more profile content here
                }

            }
        }


}