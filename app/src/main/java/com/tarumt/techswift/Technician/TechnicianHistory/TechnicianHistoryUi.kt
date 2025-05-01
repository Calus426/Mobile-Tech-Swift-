package com.tarumt.techswift.Technician.TechnicianHistory

import android.icu.text.SimpleDateFormat
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.User.Datasource.ServiceDataSource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.tarumt.techswift.WindowInfo

@Composable
fun TechnicianHistoryUi(viewModel: TechnicianHistoryViewModel = viewModel(),windowInfo: WindowInfo) {

    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("inProgress") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Request?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchHistory()
    }

    val widthFraction = if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded) 0.8f else 0.9f
    val heightFraction = if (windowInfo.screenHeightInfo is WindowInfo.WindowType.Expanded) 0.9f else 0.85f

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .fillMaxHeight(heightFraction),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
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
                    LazyColumn {
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
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedTask != null) {
        TaskTimelineDialog(task = selectedTask!!, onDismiss = { showDialog = false },windowInfo = windowInfo)
    }
}

@Composable
fun HistoryServiceCard(
    task: Request,
    isInProgress: Boolean = false,
    onDoneClick: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    val serviceList = ServiceDataSource().loadServices()
    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2E2C)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(if (isInProgress) 160.dp else 120.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF2D2E2C), modifier = Modifier.size(24.dp))
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
                    text = "RM ${task.offeredPrice}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                )
            }

            if (isInProgress && onDoneClick != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDoneClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Done", color = Color.White)
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
fun TaskTimelineDialog(task: Request, onDismiss: () -> Unit,windowInfo: WindowInfo) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Expanded) 0.7f else 0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {



                Text("Order: R${task.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                fun format(ts: Timestamp?) = ts?.toDate()?.let {
                    val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    "Date: ${df.format(it)}\nTime: ${tf.format(it)}"
                } ?: "N/A"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        TimelineStep("Request Posted", format(task.createdTime))
                        TimelineStep("Request Accepted", format(task.acceptedTime))
                        TimelineStep("Request Finished", format(task.finishedTime), isLast = true)
                    }
                }

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
fun TimelineStep(title: String, timeInfo: String, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Dot and line column
        Column(
            modifier = Modifier
                .padding(end = 12.dp)
                .width(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Vertical line (drawn first, behind the dot)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(Color.Black)
                )
            } else {
                // Add empty space for last item to maintain alignment
                Spacer(modifier = Modifier.height(40.dp))
            }

            // Dot (drawn on top of the line)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Black, shape = CircleShape)
                    .offset(y = (-20).dp) // This moves the dot up to center on the line
            )
        }

        // Event text
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(timeInfo, fontSize = 13.sp, color = Color.DarkGray)
        }
    }
}