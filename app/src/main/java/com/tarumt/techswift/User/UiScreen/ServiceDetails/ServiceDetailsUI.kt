package com.tarumt.techswift.User.UiScreen.ServiceDetails

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.tarumt.techswift.BuildConfig
import com.tarumt.techswift.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@Composable
fun ServiceDetailsUI(
    serviceDetailsViewModel : ServiceDetailsViewModel = viewModel(),
    onSubmitRequestClicked : (String) -> Unit
){

    val serviceDetailsUiState by serviceDetailsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showProcessingDialog by remember { mutableStateOf(false)}
    var enable by remember{ mutableStateOf(true)}
    var navigate by remember{ mutableStateOf(false)}

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
                modifier = Modifier.fillMaxWidth()
            ){
                TextDescription(
                    serviceDetailsViewModel.userDescription,
                    onDescriptionChange = {serviceDetailsViewModel.descriptionUpdate(it)},
                    enable = enable
                    )

                Spacer(modifier = Modifier.height(5.dp))

                PriceOffer(
                    price = serviceDetailsViewModel.offeredPrice,
                    onPriceChange = {serviceDetailsViewModel.priceUpdate(it)},
                    enable = enable
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
                ){
                    Button(
                        modifier = Modifier.padding(end = 5.dp, bottom = 5.dp),
                        enabled = enable,
                        onClick = {
                            showProcessingDialog = true
                            enable = false
                            serviceDetailsViewModel.savePictureDescription(context) {
                                showProcessingDialog = false
                                navigate = true
                            }

                        }
                    ) {
                        Text(text = stringResource(R.string.submit_service_request))
                    }
                }
            }
        }
        if(showProcessingDialog){
            CircularProgressIndicator(color = Color.White,
                strokeWidth = 4.dp,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .size(50.dp)
            )
        }
       if(navigate){
           onSubmitRequestClicked("Service request added successfully!")
       }

    }
}


@Composable
fun TextDescription(
    descriptionText:String = "",
    onDescriptionChange: (String) -> Unit,
    enable : Boolean
    ){
    Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 9.dp, top = 15.dp, bottom = 2.dp, end = 9.dp)
            ){
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Describe your problem",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            value = descriptionText,
            onValueChange = onDescriptionChange,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            placeholder = {
                Text(text = "Description")
            },
            enabled = enable


        )
    }

}

@Composable
fun PriceOffer(
    price: String,
    onPriceChange: (String) -> Unit = {},
    enable: Boolean
){

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
            fontSize = 20.sp,
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
            placeholder = {Text("0.0")},
            label = {Text("Amount")},
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun PictureDescription(
    serviceDetailsViewModel: ServiceDetailsViewModel,
    uiState: ServiceDetailsUiState,
    button: Boolean

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
    ){

        Text(
            text = "Take Picture of your problem",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp
        )

        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(1.dp)
                )
                .fillMaxWidth(0.7f),
            contentAlignment = Alignment.Center
        ){
            if (uiState.pictureDescription?.path?.isNotEmpty() == true) {
                Image(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .heightIn(max = 150.dp),
                    painter = rememberAsyncImagePainter(uiState.pictureDescription),
                    contentDescription = null
                )
            }
            else{
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
            enabled = button
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
