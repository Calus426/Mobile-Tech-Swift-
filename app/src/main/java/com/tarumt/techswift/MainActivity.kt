package com.tarumt.techswift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.tarumt.techswift.ui.theme.TechSwiftTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)
        enableEdgeToEdge()
        setContent {
                TechSwiftTheme {
                    MainScreen()
            }

        }
    }
}

