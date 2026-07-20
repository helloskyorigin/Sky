package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.PremiumColors
import com.example.ui.theme.PremiumSpacing
import com.example.ui.theme.blueGlow
import com.example.ui.theme.premiumShadow

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Start with null to force user to make an explicit, intentional choice
    var selectedLanguage by remember { mutableStateOf<String?>(null) }
    val isDark = LocalIsDark.current
    
    val bgColor = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF8FAFC) // Beautiful soft slate-50 background
    val textPrimary = if (isDark) MaterialTheme.colorScheme.onBackground else Color(0xFF0F172A) // Near-black primary text
    val textSecondary = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF475569) // Slate-600 secondary text
    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF) // Pure white cards
    val unselectedBorderColor = if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFE2E8F0) // Clean thin border
    val primaryBlue = Color(0xFF2563EB)

    // Smooth screen entrance animations
    val animAlpha = remember { Animatable(0f) }
    val animScale = remember { Animatable(0.97f) }
    
    LaunchedEffect(Unit) {
        animAlpha.animateTo(1f, tween(500, easing = EaseOutCubic))
    }
    LaunchedEffect(Unit) {
        animScale.animateTo(1f, tween(500, easing = EaseOutCubic))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .alpha(animAlpha.value)
            .scale(animScale.value)
    ) {
        // Subtle top atmospheric glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryBlue.copy(alpha = if (isDark) 0.1f else 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Premium Top-Right Skip Button
        TextButton(
            onClick = { onLanguageSelected("en") },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .testTag("language_skip_button")
        ) {
            Text(
                text = "Skip",
                color = primaryBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                // Large Globe Icon inside premium glowing frame
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(primaryBlue.copy(alpha = 0.08f), CircleShape)
                        .border(1.5.dp, primaryBlue.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Language,
                        contentDescription = null,
                        tint = primaryBlue,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Choose Language",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.3).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select the language you want to use.",
                    fontSize = 14.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Language Selection Group (Centered beautifully with custom spacing)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // English Card
                LanguageSelectionCard(
                    title = "English",
                    subtitle = "Use the app in English",
                    flag = "🇬🇧",
                    isSelected = selectedLanguage == "en",
                    onClick = { selectedLanguage = "en" },
                    cardBg = cardBg,
                    unselectedBorder = unselectedBorderColor,
                    primaryBlue = primaryBlue,
                    isDark = isDark,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                // Hindi Card
                LanguageSelectionCard(
                    title = "हिन्दी",
                    subtitle = "हिंदी में ऐप का उपयोग करें",
                    flag = "🇮🇳",
                    isSelected = selectedLanguage == "hi",
                    onClick = { selectedLanguage = "hi" },
                    cardBg = cardBg,
                    unselectedBorder = unselectedBorderColor,
                    primaryBlue = primaryBlue,
                    isDark = isDark,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                // System Default Card
                LanguageSelectionCard(
                    title = "System Default",
                    subtitle = "Follow device language settings",
                    flag = "🌐",
                    isSelected = selectedLanguage == "system",
                    onClick = { selectedLanguage = "system" },
                    cardBg = cardBg,
                    unselectedBorder = unselectedBorderColor,
                    primaryBlue = primaryBlue,
                    isDark = isDark,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Action Button at Bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                val isButtonEnabled = selectedLanguage != null
                
                Button(
                    onClick = { selectedLanguage?.let { onLanguageSelected(it) } },
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Color(0xFF1E222B) else Color(0xFFEFF2F6),
                        disabledContentColor = if (isDark) Color(0xFF475569) else Color(0xFF94A3B8)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .then(
                            if (isButtonEnabled) {
                                Modifier.blueGlow(borderRadius = 16.dp, isDark = isDark, glowColor = primaryBlue)
                            } else {
                                Modifier
                            }
                        )
                        .testTag("language_continue_button")
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionCard(
    title: String,
    subtitle: String,
    flag: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    cardBg: Color,
    unselectedBorder: Color,
    primaryBlue: Color,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color
) {
    val borderStrokeColor = if (isSelected) primaryBlue else unselectedBorder
    val borderStrokeWidth = if (isSelected) 1.5.dp else 1.dp
    val selectedBgColor = if (isDark) Color(0xFF151C2C) else Color(0xFFF0F5FF)
    val actualCardBg = if (isSelected) selectedBgColor else cardBg
    
    val scaleFactor by animateFloatAsState(
        targetValue = if (isSelected) 1.01f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "selection_scale"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = actualCardBg,
        border = BorderStroke(width = borderStrokeWidth, color = borderStrokeColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .scale(scaleFactor)
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (isSelected) {
                    Modifier.blueGlow(borderRadius = 20.dp, isDark = isDark, glowColor = primaryBlue.copy(alpha = 0.2f))
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Round flag container with elegant styling
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(if (isDark) Color(0xFF1F242F) else Color(0xFFF1F5F9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = flag,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = textSecondary,
                        maxLines = 1
                    )
                }
            }

            // Radio/Checkmark circular indicator on the right
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(primaryBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.5.dp, if (isDark) Color(0xFF2E3340) else Color(0xFFCBD5E1), CircleShape)
                )
            }
        }
    }
}
