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
import androidx.compose.ui.input.pointer.pointerInput
import com.tarumt.techswift.Login_Signup.UiScreen.SignUp.SignUpUI
import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel
import com.tarumt.techswift.WindowInfo

@Composable
fun FlipCard(authViewModel: AuthViewModel, onLoginButtonClick: () -> Unit, windowInfo: WindowInfo) {
    var isLogin by remember { mutableStateOf(true) }


    // Rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (isLogin) 0f else 180f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "cardRotation"
    )

    // Scale effect for zoom in/out
    val scale by animateFloatAsState(
        targetValue = if (rotation % 180 == 0f) 1f else 0.9f,
        animationSpec = tween(durationMillis = 400),
        label = "scaleAnim"
    )

    // Opacity fade during mid-flip
    val frontAlpha = if (rotation <= 90f) 1f else 0f
    val backAlpha = if (rotation > 90f) 1f else 0f

    val isFrontVisible = rotation <= 90f

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // SignUp UI (Back side)
        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationY = rotation + 180f
                    scaleX = scale
                    scaleY = scale
                    alpha = backAlpha
                    cameraDistance = 12 * density
                }
                .then(if (isFrontVisible) Modifier else Modifier) // keep modifier chain clean
                .pointerInput(Unit) {} // consume events when visible only
                .let { if (isFrontVisible) Modifier else it } // only SignUp gets touchable when on back
        ) {
            if (!isFrontVisible) {
                SignUpUI(
                    authViewModel = authViewModel,
                    onLoginClick = { isLogin = true },
                    windowInfo = windowInfo
                )
            }
        }

        // Login UI (Front side)
        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationY = rotation
                    scaleX = scale
                    scaleY = scale
                    alpha = frontAlpha
                    cameraDistance = 12 * density
                }
                .let { if (isFrontVisible) it else Modifier.pointerInput(Unit) {} }
        ) {
            if (isFrontVisible) {
                LoginUI(
                    authViewModel = authViewModel,
                    onSignUpClick = { isLogin = false },
                    onLoginButtonClick = onLoginButtonClick,
                    windowInfo = windowInfo
                )
            }
        }
    }
}