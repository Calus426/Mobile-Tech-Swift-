package com.tarumt.techswift.User.UiScreen.History

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.delay

@Composable
fun UserHistoryUI( viewModel : UserHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    

    Box(
        modifier = Modifier
            .fillMaxSize(),
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
                    text =uiState.pendingList.toString() ,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )


            }
        }
    }
}

@Preview
@Composable
fun HistoryPreview(){
    val navController  = rememberNavController()
    UserHistoryUI()
}

