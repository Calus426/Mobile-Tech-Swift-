package com.tarumt.techswift.User.UiScreen.ServiceDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.places.api.model.kotlin.place

@Composable
fun ServiceDetailsUI(serviceDetailsViewModel : ServiceDetailsViewModel = viewModel()){

    val serviceDetailsUiState by serviceDetailsViewModel.uiState.collectAsState()
    Box(
        Modifier
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

            Column(
                modifier = Modifier.fillMaxWidth(),
            ){
                Text(
                    text = "Service id: ${serviceDetailsUiState.serviceId}"
                )
                Description(
                    serviceDetailsViewModel.userDescription,
                    onDescriptionChange = {serviceDetailsViewModel.descriptionUpdate(it)}
                    )
            }
        }
    }
}

@Composable
fun Description(
    descriptionText:String = "",
    onDescriptionChange: (String) -> Unit
    ){
    Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 9.dp , top = 15.dp, bottom = 2.dp)
            ){
        Text(
            text = "Describe your problem",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 9.dp,end = 9.dp)
    ){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            value = descriptionText,
            onValueChange = onDescriptionChange,
            colors = TextFieldDefaults.colors(
               unfocusedContainerColor = Color.Transparent,
               focusedContainerColor = Color.Transparent
            ),
            placeholder = {
                Text(text = "Description")
            }

        )
    }


}

@Composable
@Preview
fun ServiceDetailsPreview()
{
    ServiceDetailsUI()
}