package com.tarumt.techswift.Login_Signup.UiScreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.tarumt.techswift.Login_Signup.UiScreen.SignUp.SignUpUI
import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel

@Composable
fun FlipCard(authViewModel: AuthViewModel, onLoginButtonClick:()->Unit) {
    var isLogin by remember { mutableStateOf(true) }


    // Rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (isLogin) 0f else 180f,
        animationSpec = tween(
            durationMillis = 700,
            easing = FastOutSlowInEasing
        ),
        label = "cardRotation"
    )

    // Scale effect for zoom in/out
    val scale by animateFloatAsState(
        targetValue = if (rotation % 180 == 0f) 1f else 0.9f,
        animationSpec = tween(durationMillis = 300),
        label = "scaleAnim"
    )

    // Opacity fade during mid-flip
    val frontAlpha = if (rotation <= 90f) 1f else 0f
    val backAlpha = if (rotation > 90f) 1f else 0f

    val isFrontVisible = rotation <= 90f

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationY = rotation
                    scaleX = scale
                    scaleY = scale
                    cameraDistance = 12 * density
                }
        ) {
            if (isFrontVisible) {
                Box(modifier = Modifier.graphicsLayer {
                    alpha = frontAlpha
                }) {
                    LoginUI(
                        authViewModel = authViewModel,
                        onSignUpClick = { isLogin = false },
                        onLoginButtonClick = onLoginButtonClick
                    )
                }
            } else {
                Box(
                    modifier = Modifier.graphicsLayer {
                        rotationY = 180f
                        alpha = backAlpha
                    }
                ) {
                    SignUpUI(
                        authViewModel = authViewModel,
                        onLoginClick = { isLogin = true }
                    )
                }
            }
        }
    }
}