package com.tarumt.techswift.User.UiScreen.Order

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.techswift.R

@Composable
fun Try() {
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

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                PendingCard()

            }
        }
    }
}

@Composable
fun PendingCard(
) {


    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2E2C)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 10.dp)
            .height(154.dp)
    ) {


        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.7f)
                        .fillMaxWidth(0.3f),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)

                    ) {
                        Image(
                            painter = painterResource(R.drawable.electricity),
                            contentDescription = "Electricity",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)

                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(start = 15.dp, top = 6.dp)
                ) {

                    Text(
                        text =" Service",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        ),
                        textAlign = TextAlign.Start
                    )

                }
            }
        }
    }
}

@Preview
@Composable
fun preview() {
    Try()
}