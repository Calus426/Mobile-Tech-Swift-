package com.tarumt.techswift.User.ServiceDetails

import android.net.Uri
import com.tarumt.techswift.Model.Service

data class ServiceDetailsUiState(
    val serviceSelected : Service = Service(0,0,0),
    val textDescription : String = "",
    val pictureDescription : Uri? = null,
    val picturePath : String = "",
    val offeredPrice : String = "0.0",
    val descriptionError: Boolean = false,
    val priceError: Boolean = false

)
