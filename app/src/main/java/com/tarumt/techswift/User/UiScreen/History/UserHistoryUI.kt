package com.tarumt.techswift.User.UiScreen.History

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.Model.Service
import com.tarumt.techswift.User.Datasource.ServiceDataSource
import com.tarumt.techswift.User.UiScreen.Order.OrderEvent
import com.tarumt.techswift.User.UiScreen.Order.parseTimestamp
import com.tarumt.techswift.ui.theme.provider

@Composable
fun UserHistoryUI(viewModel: UserHistoryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val serviceList = remember { ServiceDataSource().loadServices() }
    val context = LocalContext.current
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
                LazyColumn {
                    items(uiState.finishedList) { finished ->
                        val service = serviceList[finished.serviceId]
                        ServiceCard(finished, service, viewModel, uiState)
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
) {
    val orderEvents: MutableList<OrderEvent> = mutableListOf()

    if (request.createdTime != null) {
        val (date, time) = parseTimestamp(request.createdTime)
        orderEvents.add(OrderEvent("Request Posted",date,time))
    }
    if (request.acceptedTime != null) {
        val (date, time) = parseTimestamp(request.acceptedTime)
        orderEvents.add(OrderEvent("Request Accepted",date,time))
    }
    if (request.finishedTime != null) {
        val (date, time) = parseTimestamp(request.finishedTime)
        orderEvents.add(OrderEvent("Request Finished",date,time))
    }





    var showInfoBox by remember { mutableStateOf(false) }
    if (showInfoBox) {
        Dialog(onDismissRequest = { showInfoBox = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.54f)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Order: R" + request.id,
                        fontFamily = FontFamily(
                            Font(
                                googleFont = GoogleFont("Inter"),
                                fontProvider = provider,
                                weight = FontWeight.Bold
                            )
                        ),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))  // Adds space at the bottom

                    TimeLine(orderEvents)

                    Spacer(modifier = Modifier.height(10.dp))  // Adds space at the bottom

                    Text(
                        text = "Price: RM ${
                            String.format(
                                "%.2f",
                                request.offeredPrice ?: 0.00
                            )
                        }\n" +
                                "Text Description: ${request.textDescription}",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))  // Adds space at the bottom

                    request.pictureDescription?.let { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2E2C)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 10.dp)
            .height(154.dp)
            .clickable { showInfoBox = true }
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
                        .fillMaxHeight(0.6f)
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
                            text = "Order finished by Technician ${uiState.technicianName}",
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

                    }


                }
            }
        }


    }

}

@Composable
private fun TimeLine(orderEvents: List<OrderEvent>) {

    val titleHeights = remember { mutableStateMapOf<Int, Float>() }

    Box(
        modifier = Modifier
            .background(Color.LightGray.copy(alpha = 0.4f), shape = RoundedCornerShape(23.dp))
            .padding(16.dp)
            .fillMaxWidth(0.8f)
    ) {
        Row {
            // Vertical Line with Dots
            Canvas(modifier = Modifier
                .width(20.dp)
            ) {
                val dotRadius = 4.dp.toPx()
                val lineX = size.width / 2

                // Draw the vertical line
                if (titleHeights.isNotEmpty()) {
                    val firstY = titleHeights[0] ?: 0f
                    val lastY = titleHeights.values.last()
                    drawLine(
                        color = Color.Black,
                        start = Offset(lineX, firstY),
                        end = Offset(lineX, lastY),
                        strokeWidth = 2f
                    )
                }

                // Draw dots aligned with each title
                titleHeights.forEach { (index, yPosition) ->
                    drawCircle(
                        color = Color.Black,
                        radius = dotRadius,
                        center = Offset(lineX, yPosition)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Event Descriptions
            Column {
                orderEvents.forEachIndexed { index, event ->
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .onGloballyPositioned { layoutCoordinates ->
                                // Calculate the center position of this title
                                val centerY = layoutCoordinates.positionInParent().y +
                                        (layoutCoordinates.size.height / 2)
                                titleHeights[index] = centerY
                            }
                    ) {
                        Text(
                            text = event.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(text = "Date: ${event.date}",fontSize = 12.sp)
                        Text(text = "Time: ${event.time}",fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
