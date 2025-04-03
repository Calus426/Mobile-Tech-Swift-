package com.tarumt.techswift.User.Model

data class Request(
    val id : Int = 0,
    val serviceId : Int = 0,
    val textDescription : String = "",
    val userId : Int = 0,
    val pending : Boolean = true,
)
