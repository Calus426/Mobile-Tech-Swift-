package com.tarumt.techswift.Model

data class User(
    val name : String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val address1: String = "",
    val postcode: String = "",
    val state: String = "",
    val fullAddress: String = "",
    val profileAvatar : String = "https://firebasestorage.googleapis.com/v0/b/techswift-15f63.firebasestorage.app/o/images%2Fgem.jpg?alt=media&token=3aed87f0-6736-407a-a067-a48a9a3b3544",
    val role : String = ""
)
