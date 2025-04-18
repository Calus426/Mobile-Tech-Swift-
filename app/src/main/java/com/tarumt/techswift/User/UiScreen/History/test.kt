package com.tarumt.techswift.User.UiScreen.History

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.techswift.R

@Composable
fun Try (){
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
//                Text(
//                    modifier = Modifier.align(Alignment.Center),
//                    text =uiState.pendingList.toString() ,
//                    textAlign = TextAlign.Center,
//                    color = Color.Black
//                )

                PendingCard()

            }
        }
    }
}

@Composable
fun PendingCard(
    ){


    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2E2C)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .height(154.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
//            AsyncImage(
//                model = pending.pictureDescription,
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//                    .padding(8.dp)
//            )

            Card(
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth(0.3f),
                colors = CardDefaults.cardColors(containerColor = Color.White)

            ){
                Image(
                    painter = painterResource(id = R.drawable.airconditional),
                    contentDescription = stringResource(id = R.string.air_conditional),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)

                )
            }

            Row{
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(start = 5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.air_conditional) +" Service",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        ),
                        maxLines = 2,
                        textAlign = TextAlign.Start
                    )

                }

                Column(
                    modifier = Modifier
                ) {
                    Text(
                        text = "RM100",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    )

                }
            }





        }

    }

}

@Preview
@Composable
fun preview (){
    Try()
}