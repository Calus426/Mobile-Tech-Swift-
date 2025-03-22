package com.tarumt.techswift.userUIScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.tarumt.techswift.ui.theme.GreenBackground

@Composable
fun UserHistoryUI(
    onNextButtonClicked:() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenBackground), // Dark background color
        contentAlignment = Alignment.Center
    ){
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
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "History",
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Button(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClick = onNextButtonClicked

                ) {
                    Text(text = "Back")
                }
            }
        }
    }
}

@Preview
@Composable
fun HistoryPreview(){
    val navController  = rememberNavController()
    UserHistoryUI {  }
}

