package com.tarumt.techswift

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel
import com.tarumt.techswift.ui.theme.TechSwiftTheme
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)

        val apiKey = BuildConfig.PLACES_API_KEY
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No API key found")
            finish()
            return
        }

        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey, Locale("en"))
        }

        val authViewModel : AuthViewModel by viewModels()
        val navViewModel: NavViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
                TechSwiftTheme {
                    val windowInfo = rememberWindowInfo()
                    if (navViewModel.navController == null) {
                        navViewModel.navController = rememberNavController()
                    }
                    MainScreen(authViewModel=authViewModel, navController =  navViewModel.navController!!,windowInfo = windowInfo)
            }

        }
    }
//    override fun onDestroy() {
//        super.onDestroy()
//        FirebaseAuth.getInstance().signOut()
//        Log.d("MainActivity", "User signed out in onDestroy")
//    }

}

