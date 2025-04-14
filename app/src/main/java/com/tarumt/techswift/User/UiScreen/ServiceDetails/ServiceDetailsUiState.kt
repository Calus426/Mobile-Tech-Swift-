package com.tarumt.techswift.User.UiScreen.ServiceDetails

import android.net.Uri
import com.tarumt.techswift.User.Model.Service

data class ServiceDetailsUiState(
    val serviceSelected : Service = Service(0,0,0),
    val textDescription : String = "",
    val pictureDescription : Uri? = null,
    val picturePath : String = "",
    val offeredPrice : String = ""

)
