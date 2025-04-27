package com.tarumt.techswift.User.UiScreen.History

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.Model.Service
import com.tarumt.techswift.User.Datasource.ServiceDataSource
import com.tarumt.techswift.ui.theme.provider

@Composable
fun UserHistoryUI(
    viewModel: UserHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    val serviceList = remember { ServiceDataSource().loadServices() }

    var statusScreen by remember { mutableStateOf("inProgress") }




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

        //White Card
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
                modifier = Modifier.padding(16.dp)
            ) {
                StatusSelection(
                    onStatusSelected = { statusScreen = it },
                    currentStatus = statusScreen
                )

                LazyColumn {

                    if (statusScreen.equals("inProgress")) {
                        items(uiState.inProgressList) { inProgress ->
                            val service = serviceList[inProgress.serviceId]
                            ServiceCard(inProgress, service, viewModel, uiState, context)
                        }
                    } else {
                        items(uiState.pendingList) { pending ->
                            val service = serviceList[pending.serviceId]
                            ServiceCard(pending, service, viewModel, uiState, context)
                        }
                    }


                }
            }


        }

    }

}


@SuppressLint("DefaultLocale")
@Composable
fun ServiceCard(
    request: Request,
    service: Service,
    viewModel: UserHistoryViewModel,
    uiState: UserHistoryUiState,
    context: Context
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 7.dp, end = 2.dp),
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
                            painter = painterResource(service.image),
                            contentDescription = stringResource(id = service.label),
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
                        text = stringResource(id = service.label) + " Service",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        ),
                        textAlign = TextAlign.Start
                    )


                    Text(
                        text = "RM " + String.format("%.2f", request.offeredPrice ?: 0.00),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    )


                    if (request.pending == false) {
                        viewModel.getTechnicianDetails(request.technicianId)


                        Text(
                            text = "Order accepted by Technician ${uiState.technicianName}",
                            color = Color(0xFFC6C6C6),
                            fontFamily = FontFamily(
                                Font(
                                    googleFont = GoogleFont("Inter"),
                                    fontProvider = provider,
                                    weight = FontWeight.SemiBold
                                )
                            ),
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp, top = 15.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD9D9D9)) // Green background
                                    .clickable {
                                        PhoneCallIntent(uiState.technicianPhone, context)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call Technician",
                                    tint = Color(0xFF008000),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }


                }
            }
        }


    }

}


private fun PhoneCallIntent(
    phoneNo: String,
    context: Context
) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:${phoneNo}")
    }
    context.startActivity(intent)
}

@Composable
fun StatusSelection(
    onStatusSelected: (String) -> Unit = {},
    currentStatus: String
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .border(
                width = 2.dp,
                color = Color.Black.copy(alpha = 0.06f),
                shape = RoundedCornerShape(15.dp)
            )

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = { onStatusSelected("inProgress") },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),

                ) {
                Text(
                    text = "In progress",
                    color =
                    if (currentStatus == "inProgress") Color.Black
                    else Color.Black.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),

                    )
            }

            Text(
                text = "|",
                color = Color.Black.copy(alpha = 0.2f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = { onStatusSelected("pending") },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Pending",
                    color =
                    if (currentStatus == "pending") Color.Black
                    else Color.Black.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),

                    )
            }
        }


    }
}

@Preview
@Composable
fun HistoryPreview() {
    val userHistoryViewModel: UserHistoryViewModel = viewModel()
    UserHistoryUI(userHistoryViewModel)
}

