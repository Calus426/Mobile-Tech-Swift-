package com.tarumt.techswift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tarumt.techswift.ui.theme.TechSwiftTheme
import com.tarumt.techswift.userUIScreen.UserHomeUI


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechSwiftTheme {
              Navigate()
            }
        }
    }
}

