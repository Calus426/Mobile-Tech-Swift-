package com.tarumt.techswift.User.Model

import android.net.Uri

data class Request(
    val id : Int = 0,
    val serviceId : Int = 0,
    val textDescription : String = "",
    val pictureDescription : String = "",
    val userId : String = "ugJPJBqAV1f9EdJk5MWOBpvrLxY2",
    val pending : Boolean = true,
)
