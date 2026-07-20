package com.skyorigin.threatshieldai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
import com.skyorigin.threatshieldai.ui.theme.PremiumColors
import com.skyorigin.threatshieldai.ui.theme.premiumShadow
import kotlinx.coroutines.delay

@Composable
fun ProtectionStatusCard(
    viewModel: ScamLensViewModel,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = false
    
    val history = viewModel.analysesHistory
    val status = ProtectionStatusHelper.calculateStatus(history)
    val score = if (history.isEmpty()) 0 else {
        val safeCount = history.count { it.status.lowercase() == "safe" }
        ((safeCount.toFloat() / history.size) * 100).toInt().coerceIn(0, 100)
    }

    val textPrimary = if (isDark) Color.White else Color(0xFF111827)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B7280)

    // Breathing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow_breath")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Animated Card Scale & Alpha on load
    var isLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        isLoaded = true
    }
    
    val scaleFactor by animateFloatAsState(
        targetValue = if (isLoaded) 1f else 0.95f,
        animationSpec = tween(700, easing = EaseOutBack),
        label = "scaleFactor"
    )
    val alphaFactor by animateFloatAsState(
        targetValue = if (isLoaded) 1f else 0f,
        animationSpec = tween(600, easing = EaseInOutQuad),
        label = "alphaFactor"
    )

    // Rotating Recommendations index
    val recommendations = ProtectionStatusHelper.getRecommendations(history, isHindi)
    var recommendationIndex by remember { mutableStateOf(0) }
    LaunchedEffect(recommendations) {
        while (true) {
            delay(5000)
            if (recommendations.isNotEmpty()) {
                recommendationIndex = (recommendationIndex + 1) % recommendations.size
            }
        }
    }

    // Gradient Background for the Premium Blue Card
    val blueCardGradient = Brush.linearGradient(
        colors = if (isDark) {
            listOf(
                Color(0xFF0F172A), // Slate 900
                Color(0xFF1E1B4B), // Indigo 950 (ambient glow)
                Color(0xFF0F172A)
            )
        } else {
            listOf(
                Color(0xFFEFF6FF), // Blue 50
                Color(0xFFDBEAFE), // Blue 100
                Color(0xFFEFF6FF)
            )
        }
    )

    val borderBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(Color(0xFF3B82F6).copy(alpha = 0.45f), Color(0xFF1E1B4B).copy(alpha = 0.05f))
        } else {
            listOf(Color(0xFF3B82F6).copy(alpha = 0.22f), Color(0xFFDBEAFE).copy(alpha = 0.05f))
        }
    )

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .scale(scaleFactor)
            .alpha(alphaFactor)
            .premiumShadow(
                isDark = isDark,
                borderRadius = 28.dp,
                elevation = if (isDark) 16.dp else 6.dp
            )
            .border(
                width = 1.2.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(blueCardGradient)
                .padding(24.dp)
        ) {
            // Header: Title and Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "सुरक्षा स्थिति" else "Protection Status",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.3).sp
                        )
                    )
                    Text(
                        text = if (isHindi) "आपके स्कैन इतिहास के आधार पर आपकी वर्तमान सुरक्षा स्थिति।" else "Your current security health based on your scan history.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Ambient glow breathing status badge
                Box(
                    modifier = Modifier
                        .background(
                            color = status.color.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = status.color.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(status.color, CircleShape)
                        )
                        Text(
                            text = if (isHindi) {
                                when (status) {
                                    ProtectionStatus.EXCELLENT -> "उत्कृष्ट"
                                    ProtectionStatus.GOOD -> "अच्छा"
                                    ProtectionStatus.NEEDS_ATTENTION -> "ध्यान दें"
                                    ProtectionStatus.HIGH_RISK -> "उच्च जोखिम"
                                    ProtectionStatus.UNKNOWN -> "अज्ञात"
                                }
                            } else {
                                when (status) {
                                    ProtectionStatus.EXCELLENT -> "Excellent"
                                    ProtectionStatus.GOOD -> "Good"
                                    ProtectionStatus.NEEDS_ATTENTION -> "Attention"
                                    ProtectionStatus.HIGH_RISK -> "High Risk"
                                    ProtectionStatus.UNKNOWN -> "Unknown"
                                }
                            },
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Body content: Big Status Icon and Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive scaling/pulsing status icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(
                            color = status.color.copy(alpha = glowIntensity * 0.12f),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.5.dp,
                            color = status.color.copy(alpha = glowIntensity * 0.45f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = status.icon,
                        contentDescription = "Protection Status Icon",
                        tint = status.color,
                        modifier = Modifier
                            .size(36.dp)
                            .scale(0.9f + (glowIntensity * 0.12f))
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) status.titleHi else status.titleEn,
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) status.descHi else status.descEn,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = textSecondary,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            // Divider and rotating recommendation block
            if (recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                    thickness = 0.8.dp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Fading recommendation text
                AnimatedContent(
                    targetState = recommendationIndex,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)) { it / 2 })
                            .togetherWith(fadeOut(animationSpec = tween(400)))
                    },
                    label = "recommendation_fade"
                ) { targetIndex ->
                    val recText = recommendations.getOrNull(targetIndex) ?: ""
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Shield,
                            contentDescription = null,
                            tint = PremiumColors.PrimaryAccent.copy(alpha = 0.7f),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = recText,
                            style = TextStyle(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PremiumColors.PrimaryAccent
                            )
                        )
                    }
                }
            }
        }
    }
}
