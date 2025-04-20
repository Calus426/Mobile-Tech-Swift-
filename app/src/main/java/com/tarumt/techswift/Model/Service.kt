package com.tarumt.techswift.Model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Service(
    val id : Int,
    @StringRes val label : Int,
    @DrawableRes val image : Int
)
