package com.skyorigin.threatshieldai.ui.theme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.clipPath


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.skyorigin.threatshieldai.ThemeMode
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer

// adaptiveLogo removed

private val DarkColorScheme = darkColorScheme(
  primary = DarkPrimary,
  onPrimary = DarkOnPrimary,
  primaryContainer = DarkPrimaryContainer,
  onPrimaryContainer = DarkOnPrimaryContainer,
  secondary = DarkSecondary,
  onSecondary = DarkOnSecondary,
  secondaryContainer = DarkSecondaryContainer,
  onSecondaryContainer = DarkOnSecondaryContainer,
  background = DarkBackground,
  onBackground = DarkOnBackground,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkOnSurfaceVariant,
  outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
  primary = LightPrimary,
  onPrimary = LightOnPrimary,
  primaryContainer = LightPrimaryContainer,
  onPrimaryContainer = LightOnPrimaryContainer,
  secondary = LightSecondary,
  onSecondary = LightOnSecondary,
  secondaryContainer = LightSecondaryContainer,
  onSecondaryContainer = LightOnSecondaryContainer,
  background = LightBackground,
  onBackground = LightOnBackground,
  surface = LightSurface,
  onSurface = LightOnSurface,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightOnSurfaceVariant,
  outline = LightOutline,
  outlineVariant = LightOutlineVariant,
  surfaceContainer = LightSurfaceContainer,
  surfaceContainerLow = LightSurfaceContainerLow,
  surfaceContainerHigh = LightSurfaceContainerHigh
)

val LocalIsDark = compositionLocalOf { false }

@Composable
fun MyApplicationTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val darkTheme = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
  }

  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  val view = androidx.compose.ui.platform.LocalView.current
  if (!view.isInEditMode) {
    androidx.compose.runtime.SideEffect {
      val window = (view.context as? android.app.Activity)?.window
      if (window != null) {
        androidx.core.view.WindowCompat.getInsetsController(window, view).apply {
          isAppearanceLightStatusBars = !darkTheme
          isAppearanceLightNavigationBars = !darkTheme
        }
      }
    }
  }

  CompositionLocalProvider(LocalIsDark provides darkTheme) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}

@Composable
fun Modifier.blueGlow(
  borderRadius: androidx.compose.ui.unit.Dp = 16.dp,
  isDark: Boolean = LocalIsDark.current,
  glowColor: Color = Color(0xFF0052FF)
): Modifier {
  return this.drawWithContent {
    drawContent()
    val baseAlpha = if (isDark) 0.08f else 0.2f
    val steps = 8
    val stepWidth = 2f
    for (i in 1..steps) {
      val outlineAlpha = baseAlpha * (1f - (i.toFloat() / steps))
      val strokeWidth = i * stepWidth
      drawRoundRect(
        color = glowColor.copy(alpha = outlineAlpha),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(borderRadius.toPx() + strokeWidth / 2)
      )
    }
  }
}

@Composable
fun Modifier.premiumShadow(
    isDark: Boolean = LocalIsDark.current, 
    borderRadius: androidx.compose.ui.unit.Dp = 24.dp, 
    elevation: androidx.compose.ui.unit.Dp = 3.dp,
    shadowColor: Color? = null
): Modifier {
    val baseShadowColor = shadowColor ?: Color(0xFF0F172A)
    return this.shadow(
        elevation = if (isDark) 0.dp else elevation,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(borderRadius),
        ambientColor = if (isDark) Color.Transparent else baseShadowColor.copy(alpha = 0.05f),
        spotColor = if (isDark) Color.Transparent else baseShadowColor.copy(alpha = 0.12f)
    )
}

@Composable
fun OfficialBrandLogo(
    modifier: Modifier = Modifier,
    isDark: Boolean = LocalIsDark.current,
    containerShape: Shape = CircleShape,
    contentScale: ContentScale = ContentScale.Fit,
    noBackground: Boolean = false
) {
    val logoRes = com.skyorigin.threatshieldai.R.drawable.dark
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = logoRes),
            contentDescription = "ThreatShield AI Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
    }
}
