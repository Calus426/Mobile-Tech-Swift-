package com.tarumt.techswift

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label : String,
    val iconSelected : Int,
    val iconNonSelected : Int,
    val route : String
)
