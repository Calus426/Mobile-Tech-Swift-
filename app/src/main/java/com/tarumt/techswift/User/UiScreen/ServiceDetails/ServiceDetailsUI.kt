package com.tarumt.techswift.User.UiScreen.ServiceDetails

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tarumt.techswift.BuildConfig
import com.tarumt.techswift.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@SuppressLint("SuspiciousIndentation")
@Composable
fun ServiceDetailsUI(
    serviceDetailsViewModel: ServiceDetailsViewModel = viewModel(),
    onSubmitRequestClicked: (String) -> Unit
) {

    val serviceDetailsUiState by serviceDetailsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showProcessingDialog by remember { mutableStateOf(false) }
    var enable by remember { mutableStateOf(true) }
    var navigate by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.9f)
                .padding(1.dp),
            shape = RoundedCornerShape(30.dp), // Rounded corners
            elevation = CardDefaults.cardElevation(4.dp),

            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (showProcessingDialog) 0.3f else 1f)
            ) {
                TextDescription(
                    serviceDetailsViewModel.userDescription,
                    onDescriptionChange = { serviceDetailsViewModel.descriptionUpdate(it) },
                    enable = enable,
                    descriptionError = serviceDetailsUiState.descriptionError
                )

                Spacer(modifier = Modifier.height(5.dp))

                PriceOffer(
                    price = serviceDetailsViewModel.offeredPrice,
                    onPriceChange = {
                        if (it.all { char -> char.isDigit() }) {
                            serviceDetailsViewModel.priceUpdate(it)
                        }
                    },
                    enable = enable,
                    priceError = serviceDetailsUiState.priceError
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Capture Photo Button
                PictureDescription(
                    serviceDetailsViewModel = serviceDetailsViewModel,
                    serviceDetailsUiState,
                    enable
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(
                        modifier = Modifier.padding(end = 5.dp, bottom = 5.dp),
                        enabled = enable,
                        onClick = {
                            if (serviceDetailsViewModel.validateInputs()) {
                                showProcessingDialog = true
                                enable = false
                                serviceDetailsViewModel.savePictureDescription(context) {
                                    showProcessingDialog = false
                                    navigate = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF393D36))
                    ) {
                        Text(text = stringResource(R.string.submit_service_request))
                    }
                }
            }


        }

        if (showProcessingDialog) {
            BackHandler(enabled = true) {
                // Do nothing → this disables the back button
            }
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever, // <- LOOP FOREVER,
                speed = 1.3f
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(180.dp)
            )

        }
        if (navigate) {
            onSubmitRequestClicked("Service request added successfully!")
        }

    }
}


@Composable
fun TextDescription(
    descriptionText: String = "",
    onDescriptionChange: (String) -> Unit,
    enable: Boolean,
    descriptionError : Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 9.dp, top = 15.dp, bottom = 2.dp, end = 9.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Describe your problem",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            value = descriptionText,
            onValueChange = onDescriptionChange,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedBorderColor = Color.Black.copy(alpha = 0.2f),
                focusedBorderColor = Color.Black.copy(alpha = 0.2f)
            ),
            placeholder = {
                Text(text = "Description")
            },
            enabled = enable,
            isError = descriptionError
        )
        if (descriptionError) {
            Text(
                text = "Description cannot be empty!",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 9.dp, top = 4.dp)
            )
        }
    }

}

@Composable
fun PriceOffer(
    price: String,
    onPriceChange: (String) -> Unit = {},
    enable: Boolean,
    priceError: Boolean
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 9.dp, top = 15.dp, bottom = 2.dp, end = 9.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Enter Your Offer Price (RM)",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = price,
            onValueChange = onPriceChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide() // Hide the keyboard
                    focusManager.clearFocus() // Clear focus from TextField
                }
            ),
            readOnly = !enable,
            prefix = { Text("RM ") },
            placeholder = { Text("0.0") },
            label = { Text("Amount") },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Black.copy(alpha = 0.2f),
                focusedBorderColor = Color.Black.copy(alpha = 0.2f)
            ),
            isError = priceError
        )

        if (priceError) {
            Text(
                text = "Price offered cannot be 0!",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 9.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun PictureDescription(
    serviceDetailsViewModel: ServiceDetailsViewModel,
    uiState: ServiceDetailsUiState,
    enable: Boolean

) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    // Camera launcher for taking a picture
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            serviceDetailsViewModel.updatePictureDescription(uri)
        }

    // Permission launcher to request camera permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Take Picture of your problem",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 15.sp
        )

        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(1.dp)
                )
                .fillMaxWidth(0.7f),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.pictureDescription?.path?.isNotEmpty() == true) {
                Image(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .heightIn(max = 150.dp),
                    painter = rememberAsyncImagePainter(uiState.pictureDescription),
                    contentDescription = null
                )
            } else {
                Image(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .heightIn(max = 150.dp),
                    painter = painterResource(R.drawable.emptyimage),
                    contentDescription = null

                )
            }
        }


        Button(
            onClick = {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                } else {
                    // Request a permission
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            enabled = enable,
            colors = ButtonDefaults.buttonColors(Color(0xFF393D36))
        ) {
            Text(text = "Capture Image From Camera")
        }


    }

}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}


@Composable
@Preview
fun ServiceDetailsPreview() {
    ServiceDetailsUI {}
}
