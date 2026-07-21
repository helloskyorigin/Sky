package com.skyorigin.threatshieldai

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onLaunchApp: () -> Unit
) {
    val logoAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }

    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF050816) else Color.White
    val textPrimary = if (isDark) Color.White else Color(0xFF111111)

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        AnalyticsManager.getInstance(context).logScreenView("splash")
        
        // Elements fade in beautifully
        logoAlpha.animateTo(1f, tween(300, easing = EaseInOutCubic))
        contentAlpha.animateTo(1f, tween(300, easing = EaseInOutCubic))
        
        // Under 700ms total
        delay(100)
        
        // Smooth fade-out transition
        exitAlpha.animateTo(0f, tween(200, easing = EaseInOutQuad))
        
        AnalyticsManager.getInstance(context).logSplashCompleted()
        onLaunchApp()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(exitAlpha.value)
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ThreatShield AI Transparent Logo
            Image(
                painter = painterResource(id = R.drawable.ic_official_logo),
                contentDescription = "ThreatShield AI Logo",
                modifier = Modifier
                    .size(136.dp)
                    .alpha(logoAlpha.value),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // App Name with Material 3 Typography
            Text(
                text = "ThreatShield AI",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.alpha(contentAlpha.value)
            )
        }
    }
}
