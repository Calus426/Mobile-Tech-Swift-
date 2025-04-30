package com.tarumt.techswift.Technician

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tarumt.techswift.User.Datasource.ServiceDataSource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TechnicianHomeUI(
    viewModel: TechnicianViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val serviceList = ServiceDataSource().loadServices()

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.pendingList.isEmpty() -> {
                Text(
                    text = "No available tasks.",
                    color = Color.Gray,
                    fontSize = 18.sp
                )
            }
            else -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.8f)
                        .padding(1.dp),
                    shape = RoundedCornerShape(30.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Available Tasks",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Black,
                                fontSize = 24.sp
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(14.dp)
                        ) {
                            items(uiState.pendingList) { task ->
                                TaskCard(
                                    serviceName = stringResource(serviceList[task.serviceId].label),
                                    price = task.offeredPrice.toString(),
                                    onAccept = { viewModel.acceptTask(task, context) },
                                    onClick = { viewModel.onTaskSelected(task) }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }

        //  Custom Dialog
        if (uiState.showDialog && uiState.selectedTask != null) {
            Dialog(onDismissRequest = { viewModel.dismissDialog() }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .fillMaxHeight(0.6f)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val task = uiState.selectedTask!!
                        val painter = rememberAsyncImagePainter(model = task.pictureDescription)



                        Text(
                            text = "Task: R${task.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        fun format(ts: Timestamp?): String {
                            return ts?.toDate()?.let {
                                val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                "Date: ${df.format(it)}\nTime: ${tf.format(it)}"
                            } ?: "N/A"
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                TimelineStep("Request Posted", format(task.createdTime),isLast = true)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Service: ${stringResource(serviceList[task.serviceId].label)}",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Description: ${task.textDescription}",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Price: RM ${String.format("%.2f", task.offeredPrice ?: 0.00)}",
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Image Preview
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
                            onClick = { viewModel.dismissDialog() },
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
    }
}

@Composable
fun TaskCard(serviceName: String, price: String, onAccept: () -> Unit, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2E2C)),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = serviceName,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = price,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = { onAccept() },
                    modifier = Modifier
                        .size(width = 100.dp, height = 50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7B61FF)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Accept",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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

