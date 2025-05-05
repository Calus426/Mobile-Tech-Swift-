package com.tarumt.techswift.Technician.TechnicianHistory

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.User.Datasource.ServiceDataSource
import com.tarumt.techswift.WindowInfo
import java.util.Locale

@Composable
fun TechnicianHistoryUi(
    viewModel: TechnicianHistoryViewModel = viewModel(),
    windowInfo: WindowInfo
) {

    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("inProgress") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Request?>(null) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.fetchHistory()
    }

    val widthFraction =
        if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded) 0.8f else 0.9f
    val heightFraction =
        if (windowInfo.screenHeightInfo is WindowInfo.WindowType.Expanded) 0.9f else 0.85f

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .fillMaxHeight(heightFraction),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F1F1), RoundedCornerShape(50)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabButton("In Progress", selectedTab == "inProgress") {
                        selectedTab = "inProgress"
                    }
                    TabButton("Finished", selectedTab == "finished") {
                        selectedTab = "finished"
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF7B61FF))
                } else if (uiState.errorMessage != null) {
                    Text(text = "Error: ${uiState.errorMessage}", color = Color.Red)
                } else {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp) // This adds spacing between items
                    ) {
                        val tasks = if (selectedTab == "inProgress") {
                            uiState.inProgressTasks
                        } else {
                            uiState.finishedTasks
                        }
                        items(tasks) { task ->
                            HistoryServiceCard(
                                task = task,
                                isInProgress = selectedTab == "inProgress",
                                onDoneClick = {
                                    viewModel.markTaskAsFinished("R${task.id}")
                                },
                                onClick = {
                                    selectedTask = task
                                    showDialog = true
                                },
                                onNavigateClick = {
                                    viewModel.getUserAddress(task.userId!!, context)
                                },
                                onCallClick = {
                                    viewModel.getUserPhone(task.userId!!, onPhoneFetched = { phone ->
                                        PhoneCallIntent(phone, context)
                                    })
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedTask != null) {
        TaskTimelineDialog(
            task = selectedTask!!,
            onDismiss = { showDialog = false },
            windowInfo = windowInfo
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun HistoryServiceCard(
    task: Request,
    isInProgress: Boolean = false,
    onDoneClick: (() -> Unit)? = null,
    onClick: () -> Unit = {},
    onNavigateClick: () -> Unit = {},
    onCallClick: () -> Unit = {},
) {
    val serviceList = ServiceDataSource().loadServices()

    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2E2C)),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onClick() }) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .fillMaxWidth(0.3f)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(serviceList[task.serviceId].image),
                        contentDescription = stringResource(id = serviceList[task.serviceId].label),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)

                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(serviceList[task.serviceId].label),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "RM ${String.format("%.2f", task.offeredPrice ?: 0.00)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                )
            }

            if (isInProgress && onDoneClick != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(
                        onClick = { onCallClick()},
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call User",
                            tint = Color(0xFF008000),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Button(
                        onClick = { onNavigateClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f).padding(start = 4.dp, end = 4.dp)
                    ) {
                        Text(text = "Map", color = Color.White)
                    }

                    Button(
                        onClick = { onDoneClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Text(text = "Done", color = Color.White, fontSize = 12.sp)
                    }


                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color.White else Color.Transparent,
            contentColor = if (selected) Color.Black else Color.DarkGray
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun TaskTimelineDialog(task: Request, onDismiss: () -> Unit, windowInfo: WindowInfo) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded) 0.7f else 0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text("Order: R${task.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                fun format(ts: Timestamp?) = ts?.toDate()?.let {
                    val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    "Date: ${df.format(it)}\nTime: ${tf.format(it)}"
                } ?: "N/A"


                val timeLineList: MutableList<TimeLineInfo> = mutableListOf()
                timeLineList.add(TimeLineInfo("Request Posted", format(task.createdTime)))
                timeLineList.add(TimeLineInfo("Request Accepted", format(task.acceptedTime)))
                timeLineList.add(TimeLineInfo("Request Finished", format(task.finishedTime)))
                TimelineStep(timeLineList)



                Spacer(modifier = Modifier.height(16.dp))
                Text("Price: RM ${task.offeredPrice}")
                Text("Text Description: ${task.textDescription}")

                //  Image Preview
                val painter = rememberAsyncImagePainter(task.pictureDescription)
                Image(
                    painter = painter,
                    contentDescription = "Task Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(bottom = 12.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onDismiss() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7B61FF),
                        contentColor = Color.White
                    )
                ) {
                    Text("Close")
                }

            }
        }
    }
}


@Composable
fun TimelineStep(
    timeLine: List<TimeLineInfo>
) {


    val titleHeights = remember { mutableStateMapOf<Int, Float>() }

    Box(
        modifier = Modifier
            .background(Color.LightGray.copy(alpha = 0.4f), shape = RoundedCornerShape(23.dp))
            .padding(16.dp)
            .fillMaxWidth(0.9f)
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
                timeLine.forEachIndexed { index, event ->
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
                        Text(text = "Date: ${event.timeInfo}", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
fun PhoneCallIntent(phoneNo: String, context: Context) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNo")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

data class TimeLineInfo(
    val title: String,
    val timeInfo: String
)