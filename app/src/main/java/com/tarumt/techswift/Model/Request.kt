package com.tarumt.techswift.Model

import com.google.firebase.Timestamp

data class Request(
    val id : Int = 0,
    val serviceId : Int = 0,
    val textDescription : String = "",
    val pictureDescription : String = "",
    val userId : String? = "",
    val pending : Boolean = true,
    val createdTime : Timestamp? = null,
    val offeredPrice : Double? = 0.00,
    val technicianId : String = "",
    val status : String = "pending" , //pending,inProgress,finished
    val acceptedTime : Timestamp? = null,
    val finishedTime : Timestamp? = null
)
