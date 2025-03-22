package com.tarumt.techswift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tarumt.techswift.userUIScreen.UserScreenUI
import com.tarumt.techswift.ui.theme.TechSwiftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechSwiftTheme {
                UserScreenUI()
            }
        }
    }
}

@Composable
@Preview
fun PreviewUserUI(){
    UserScreenUI()
}
