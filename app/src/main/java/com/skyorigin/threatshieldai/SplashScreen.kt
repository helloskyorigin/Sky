package com.skyorigin.threatshieldai

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.blueGlow
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onLaunchApp: () -> Unit
) {
    val logoAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }

    val bgColor = Color.White
    val textPrimary = Color(0xFF111111)
    val textSecondary = Color(0xFF6B7280)
    val primaryBlue = Color(0xFF2563EB)

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        AnalyticsManager.getInstance(context).logScreenView("splash")
        // Elements fade in beautifully
        logoAlpha.animateTo(1f, tween(400, easing = EaseInOutCubic))
        contentAlpha.animateTo(1f, tween(400, easing = EaseInOutCubic))
        
        // Exact 2-second total duration (including fade-ins and fade-out)
        delay(1300)
        
        // Smooth fade-out transition
        exitAlpha.animateTo(0f, tween(300, easing = EaseInOutQuad))
        
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
            // ThreatShield AI Logo
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .blueGlow(borderRadius = 28.dp, isDark = false, glowColor = primaryBlue.copy(alpha = 0.3f))
                    .alpha(logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_official_logo),
                    contentDescription = "ThreatShield AI Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name below logo
            Text(
                text = "ThreatShield AI",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Small premium loading spinner
            CircularProgressIndicator(
                color = primaryBlue,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(contentAlpha.value)
            )
        }
    }
}
