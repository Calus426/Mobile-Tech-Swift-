package com.tarumt.techswift.User.Order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.Model.Service
import com.tarumt.techswift.User.Datasource.ServiceDataSource
import com.tarumt.techswift.WindowInfo
import com.tarumt.techswift.ui.theme.provider
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun UserOrderUI(
    viewModel: UserOrderViewModel = viewModel(),
    windowInfo: WindowInfo
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    val serviceList = remember { ServiceDataSource().loadServices() }

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
                    onStatusSelected = { viewModel.statusScreenUpdate(it) },
                    currentStatus = uiState.statusScreen
                )

                LazyColumn {

                    if (uiState.statusScreen.equals("inProgress")) {
                        items(uiState.inProgressList) { inProgress ->
                            val service = serviceList[inProgress.request.serviceId]
                            OnGoingServiceCard(
                                request = inProgress.request,
                                service = service,
                                context = context,
                                technicianName = inProgress.technicianName,
                                technicianPhone = inProgress.technicianPhone,
                                windowInfo = windowInfo
                            )
                        }
                    } else {
                        items(uiState.pendingList) { pending ->
                            val service = serviceList[pending.serviceId]
                            PendingServiceCard(
                                request = pending,
                                service = service,
                                windowInfo = windowInfo,
                                onDeleteClicked = {viewModel.cancelRequest(it)}
                            )
                        }
                    }


                }
            }


        }

    }

}


@OptIn(ExperimentalWearMaterialApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun PendingServiceCard(
    request: Request,
    service: Service,
    windowInfo: WindowInfo,
    onDeleteClicked : (Int) -> Unit
) {
    val orderEvents: MutableList<OrderEvent> = mutableListOf()

    if (request.createdTime != null) {
        val (date, time) = parseTimestamp(request.createdTime)
        orderEvents.add(OrderEvent("Request Posted", date, time))
    }
    if (request.acceptedTime != null) {
        val (date, time) = parseTimestamp(request.acceptedTime)
        orderEvents.add(OrderEvent("Request Accepted", date, time))
    }
    if (request.finishedTime != null) {
        val (date, time) = parseTimestamp(request.finishedTime)
        orderEvents.add(OrderEvent("Request Finished", date, time))
    }

    val squareSize = 150.dp
    val swipeAbleState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1)


    var showConfirmation by rememberSaveable  { mutableStateOf(false) }
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Confirm Cancel") },
            text = { Text("Are you sure you want to cancel this request?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClicked(request.id)
                    showConfirmation = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("No")
                }
            }
        )
    }
    //Info box when user click
    var showInfoBox by rememberSaveable  { mutableStateOf(false) }
    if (showInfoBox) {
        Dialog(onDismissRequest = { showInfoBox = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) 0.54f else 0.85f)
                    .background(Color.White, shape = RoundedCornerShape(30.dp))
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

                    Column(
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = "Price: RM ${
                                String.format(
                                    "%.2f",
                                    request.offeredPrice ?: 0.00
                                )
                            }\n" +
                                    "Text Description: ${request.textDescription}",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(
                                Font(
                                    fontProvider = provider,
                                    googleFont = GoogleFont("Inter"),
                                    weight = FontWeight.Medium
                                )
                            )
                        )
                    }

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFF2D2E2C))
                .swipeable(
                    state = swipeAbleState,
                    anchors = anchors,
                    thresholds = { _, _ ->
                        FractionalThreshold(0.3f)
                    },
                    orientation = Orientation.Horizontal
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {

                IconButton(
                    onClick = {
                        showConfirmation = true
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            swipeAbleState.offset.value.roundToInt(), 0
                        )
                    }
                    .clip(RoundedCornerShape(15.dp))
                    .fillMaxWidth()
                    .height(150.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,

                    )
                {
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

                        }
                    }
                }
            }
        }

    }


}

@OptIn(ExperimentalWearMaterialApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun OnGoingServiceCard(
    request: Request,
    service: Service,
    context: Context,
    technicianName: String = "",
    technicianPhone: String = "",
    windowInfo: WindowInfo
) {
    val orderEvents: MutableList<OrderEvent> = mutableListOf()

    if (request.createdTime != null) {
        val (date, time) = parseTimestamp(request.createdTime)
        orderEvents.add(OrderEvent("Request Posted", date, time))
    }
    if (request.acceptedTime != null) {
        val (date, time) = parseTimestamp(request.acceptedTime)
        orderEvents.add(OrderEvent("Request Accepted", date, time))
    }
    if (request.finishedTime != null) {
        val (date, time) = parseTimestamp(request.finishedTime)
        orderEvents.add(OrderEvent("Request Finished", date, time))
    }

    //Info box when user click
    var showInfoBox by rememberSaveable { mutableStateOf(false) }
    if (showInfoBox) {
        Dialog(onDismissRequest = { showInfoBox = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) 0.54f else 0.85f)
                    .background(Color.White, shape = RoundedCornerShape(30.dp))
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

                    Column(
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = "Price: RM ${
                                String.format(
                                    "%.2f",
                                    request.offeredPrice ?: 0.00
                                )
                            }\n" +
                                    "Text Description: ${request.textDescription}",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(
                                Font(
                                    fontProvider = provider,
                                    googleFont = GoogleFont("Inter"),
                                    weight = FontWeight.Medium
                                )
                            )
                        )
                    }

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

            )
        {
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

                    Text(
                        text = "Order accepted by Technician $technicianName",
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
                                    PhoneCallIntent(technicianPhone, context)
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
            Canvas(
                modifier = Modifier
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
                        Text(text = "Date: ${event.date}", fontSize = 12.sp)
                        Text(text = "Time: ${event.time}", fontSize = 12.sp)
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
                    text = "On Going",
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

fun parseTimestamp(timestamp: Timestamp): Pair<String, String> {
    // Normalize time zone format from "UTC+8" to "UTC+08:00"
    val date = timestamp.toDate()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(date) to timeFormat.format(date)
}


