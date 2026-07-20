package com.example.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Premium Layout & Spacing System
 * Consistent, rhythm-based spacing tokens to eliminate random padding.
 */
object PremiumSpacing {
    val micro: Dp = 4.dp
    val small: Dp = 8.dp
    val compact: Dp = 12.dp
    val default: Dp = 16.dp
    val comfortable: Dp = 20.dp
    val large: Dp = 24.dp
    val hero: Dp = 32.dp
}

/**
 * Premium Corner Radius System
 * Clean, consistent corner radius definitions following modern Apple/Material 3 standards.
 */
object PremiumRadius {
    val smallControl: RoundedCornerShape = RoundedCornerShape(12.dp)
    val button: RoundedCornerShape = RoundedCornerShape(16.dp)
    val card: RoundedCornerShape = RoundedCornerShape(22.dp)
    val bottomNav: RoundedCornerShape = RoundedCornerShape(28.dp)
    val dialog: RoundedCornerShape = RoundedCornerShape(24.dp)
    val bottomSheet: RoundedCornerShape = RoundedCornerShape(32.dp)
}

/**
 * Premium Elevation & Shadow System
 * Defines standard modern Material 3/Apple-inspired depth hierarchy.
 */
object PremiumElevation {
    val flat: Dp = 0.dp
    val micro: Dp = 2.dp
    val control: Dp = 4.dp
    val card: Dp = 3.dp
    val floatingNav: Dp = 16.dp
    val dialog: Dp = 24.dp
}

/**
 * Premium Typography Hierarchy
 * Beautiful typography scale with clear hierarchy.
 */
object PremiumTypography {
    val Hero = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    )
    val PageTitle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.2).sp
    )
    val SectionTitle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    )
    val CardTitle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )
    val Body = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    )
    val Caption = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    )
}

/**
 * Premium Color Tokens
 * Single authoritative primary accent, safety status indicators, and clean neutral tones.
 */
object PremiumColors {
    val PrimaryAccent = Color(0xFF2563EB) // Premium Blue (#2563EB)
    val Safe = Color(0xFF16A34A)          // Success Green (#16A34A)
    val Warning = Color(0xFFF59E0B)       // Soft Amber (#F59E0B)
    val Danger = Color(0xFFDC2626)        // Clean Red (#DC2626)
    
    // Neutrals
    val BackgroundLight = Color(0xFFFFFFFF)
    val TextDark = Color(0xFF111827)
    val SubtitleGray = Color(0xFF6B7280) // Secondary text (#6B7280)
    
    // Borders & Lines
    val SubtleBorderLight = Color(0xFFDCE3EE) // Premium border (#DCE3EE)
    val SoftShadowLight = Color(0xFF000000).copy(alpha = 0.04f)
}

/**
 * Premium Motion & Animation Tokens
 * Fluid, natural animation spec and curves.
 */
object PremiumMotion {
    const val DurationFastMs = 250
    
    // Natural physics-based spring for subtle layout or scaling states
    fun <T> springNatural() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    // Slightly snappy transition spring for micro-interactions
    fun <T> springInteractive() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

/**
 * Premium Bottom Navigation Design Tokens
 */
object PremiumBottomNavTokens {
    val shape = PremiumRadius.bottomNav
    val horizontalPadding = PremiumSpacing.large
    val verticalPadding = PremiumSpacing.compact
    val activeIndicatorColor = PremiumColors.PrimaryAccent.copy(alpha = 0.08f)
    val activeIconColor = PremiumColors.PrimaryAccent
    val inactiveIconColor = PremiumColors.SubtitleGray
}

/**
 * Reusable Design System Components
 */

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface, // Pure white surface
    borderColor: Color = MaterialTheme.colorScheme.outline, // Use outline for card border
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = LocalIsDark.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val actualBorderColor = if (isDark) {
        Color(0xFF2D3748)
    } else {
        if (isPressed) Color(0xFFBFC9D7) else borderColor
    }
    
    val elevation = if (isDark) 0.dp else 10.dp // Increased elevation for permanent premium look
    
    val cardModifier = if (onClick != null) {
        modifier
            .premiumShadow(
                isDark = isDark, 
                borderRadius = 22.dp, 
                elevation = elevation,
                shadowColor = if (isDark) Color.Black else Color(0xFF0F172A).copy(alpha = 0.08f) // Ambient soft shadow
            )
            .border(BorderStroke(1.5.dp, actualBorderColor), PremiumRadius.card)
            .clip(PremiumRadius.card)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current
            )
            .background(backgroundColor)
            .padding(20.dp) // Increased padding
    } else {
        modifier
            .premiumShadow(
                isDark = isDark, 
                borderRadius = 22.dp, 
                elevation = elevation,
                shadowColor = if (isDark) Color.Black else Color(0xFF0F172A).copy(alpha = 0.08f) // Ambient soft shadow
            )
            .border(BorderStroke(1.5.dp, actualBorderColor), PremiumRadius.card)
            .clip(PremiumRadius.card)
            .background(backgroundColor)
            .padding(20.dp) // Increased padding
    }

    Column(
        modifier = cardModifier,
        content = content
    )
}

@Composable
fun PremiumButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: PremiumButtonStyle = PremiumButtonStyle.Filled,
    text: String
) {
    val isDark = LocalIsDark.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "premium_btn_press"
    )

    // Gradient definitions for Primary Button
    val primaryGradient = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = if (isDark) {
            listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
        } else {
            listOf(Color(0xFF3B82F6), Color(0xFF2563EB)) // Premium Blue gradient
        }
    )

    val containerColor = when (style) {
        PremiumButtonStyle.Filled -> if (enabled) Color.Transparent else PremiumColors.PrimaryAccent.copy(alpha = 0.3f)
        PremiumButtonStyle.Outlined -> Color.Transparent
        PremiumButtonStyle.Ghost -> Color.Transparent
        PremiumButtonStyle.Danger -> if (enabled) PremiumColors.Danger else PremiumColors.Danger.copy(alpha = 0.3f)
    }
    
    val contentColor = when (style) {
        PremiumButtonStyle.Filled -> Color.White
        PremiumButtonStyle.Outlined -> if (enabled) PremiumColors.PrimaryAccent else PremiumColors.SubtitleGray
        PremiumButtonStyle.Ghost -> if (enabled) PremiumColors.PrimaryAccent else PremiumColors.SubtitleGray
        PremiumButtonStyle.Danger -> Color.White
    }

    val border = when (style) {
        PremiumButtonStyle.Outlined -> BorderStroke(1.dp, if (enabled) PremiumColors.PrimaryAccent else PremiumColors.SubtitleGray.copy(alpha = 0.3f))
        else -> null
    }

    val buttonModifier = modifier
        .height(52.dp)
        .scale(scale)
        .premiumShadow(isDark, 16.dp, elevation = if (style == PremiumButtonStyle.Filled && enabled) 6.dp else 0.dp)
        .clip(PremiumRadius.button)
        .then(
            if (style == PremiumButtonStyle.Filled && enabled) {
                Modifier.background(primaryGradient)
            } else {
                Modifier
            }
        )

    Surface(
        onClick = onClick,
        modifier = buttonModifier,
        enabled = enabled,
        color = containerColor,
        contentColor = contentColor,
        border = border,
        shape = PremiumRadius.button,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = PremiumTypography.CardTitle.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

enum class PremiumButtonStyle {
    Filled,
    Outlined,
    Ghost,
    Danger
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    minHeight: Dp = 120.dp,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val isDark = LocalIsDark.current
    var isFocused by remember { mutableStateOf(false) }
    
    val inputBackground = if (isDark) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)
    val borderNormalColor = if (isDark) Color(0xFF2D3748) else PremiumColors.SubtleBorderLight
    val borderFocusedColor = PremiumColors.PrimaryAccent
    
    val currentBorderColor = if (isFocused) borderFocusedColor else borderNormalColor
    val glowModifier = if (isFocused) Modifier.blueGlow(borderRadius = 18.dp) else Modifier

    Box(
        modifier = modifier
            .then(glowModifier)
            .border(BorderStroke(1.dp, currentBorderColor), PremiumRadius.button)
            .clip(PremiumRadius.button)
            .background(inputBackground)
            .onFocusChanged { isFocused = it.isFocused }
            .padding(PremiumSpacing.comfortable)
    ) {
        if (value.isEmpty() && placeholder.isNotEmpty()) {
            Text(
                text = placeholder,
                style = PremiumTypography.Body,
                color = PremiumColors.SubtitleGray.copy(alpha = 0.6f)
            )
        }
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (singleLine) 0.dp else minHeight),
            textStyle = PremiumTypography.Body.copy(
                color = if (isDark) Color.White else PremiumColors.TextDark
            ),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            cursorBrush = SolidColor(PremiumColors.PrimaryAccent)
        )
    }
}

@Composable
fun PremiumDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp
) {
    val isDark = LocalIsDark.current
    val color = if (isDark) Color(0xFF202124) else PremiumColors.PrimaryAccent.copy(alpha = 0.05f)
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
fun PremiumIconContainer(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    size: Dp = 44.dp,
    cornerRadius: Dp = 12.dp,
    contentDescription: String? = null,
    iconSize: Dp = 20.dp
) {
    val isDark = LocalIsDark.current
    
    val baseBgColor = containerColor ?: (if (isDark) tintColor.copy(alpha = 0.16f) else MaterialTheme.colorScheme.primaryContainer)
    val highlightColor = if (isDark) tintColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outlineVariant
    
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = baseBgColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                BorderStroke(1.dp, highlightColor),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tintColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
