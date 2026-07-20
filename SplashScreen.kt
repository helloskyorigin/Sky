package com.example

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.ui.theme.LocalIsDark

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onLaunchApp: () -> Unit
) {
    val logoAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val isDark = LocalIsDark.current

    LaunchedEffect(Unit) {
        // Logo fades in over 400ms immediately
        logoAlpha.animateTo(1f, tween(400, easing = EaseInOutCubic))
        
        // Display logo for approximately 1000ms total (400ms fade in + 600ms static)
        delay(600)
        
        // Fade smoothly into Home Screen over 300ms
        exitAlpha.animateTo(0f, tween(300, easing = EaseInOutQuad))
        
        onLaunchApp()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(exitAlpha.value)
            .background(if (isDark) Color(0xFF090B12) else Color(0xFFFFFFFF)), // Adaptive background
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_official_logo),
            contentDescription = "ThreatShield AI Logo",
            modifier = Modifier
                .width(132.dp)
                .wrapContentHeight()
                .alpha(logoAlpha.value),
            contentScale = ContentScale.Fit
        )
    }
}
