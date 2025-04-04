package com.tarumt.techswift.User.UiScreen.History

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.items

@Composable
fun UserHistoryUI( viewModel : UserHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current




    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage.takeIf { it.isNotEmpty() }?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()

            // Clear the message after showing
            viewModel.clearToastMessage()
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

                LazyColumn {
                    items(uiState.pendingList){ url ->
                        AsyncImage(
                            model = url.pictureDescription,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(8.dp)
                        )
                    }

                }

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

