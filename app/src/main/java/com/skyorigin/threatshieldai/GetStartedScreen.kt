package com.skyorigin.threatshieldai

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.blueGlow

@Composable
fun GetStartedScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var isVisible by remember { mutableStateOf(false) }
    var consentChecked by remember { mutableStateOf(false) }
    val primaryBlue = Color(0xFF2563EB)

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.85f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logo_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.ic_official_logo),
                contentDescription = "ThreatShield AI Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .blueGlow(borderRadius = 32.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = androidx.compose.ui.text.buildAnnotatedString {
                    append("Stay ")
                    withStyle(androidx.compose.ui.text.SpanStyle(color = primaryBlue)) {
                        append("Safe.")
                    }
                },
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your digital world\ndeserves better protection.",
                fontSize = 17.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Consent check row with clickable legal text
            val annotatedText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF4B5563), fontSize = 14.sp)) {
                    append("I agree to the ")
                }
                pushStringAnnotation(tag = "terms", annotation = "terms")
                withStyle(style = SpanStyle(color = primaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                    append("Terms of Service")
                }
                pop()
                withStyle(style = SpanStyle(color = Color(0xFF4B5563), fontSize = 14.sp)) {
                    append(" and ")
                }
                pushStringAnnotation(tag = "privacy", annotation = "privacy")
                withStyle(style = SpanStyle(color = primaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                    append("Privacy Policy")
                }
                pop()
                withStyle(style = SpanStyle(color = Color(0xFF4B5563), fontSize = 14.sp)) {
                    append(".")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .alpha(alpha),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = consentChecked,
                    onCheckedChange = { consentChecked = it },
                    modifier = Modifier.testTag("consent_checkbox"),
                    colors = CheckboxDefaults.colors(
                        checkedColor = primaryBlue,
                        uncheckedColor = Color(0xFF9CA3AF)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(tag = "terms", start = offset, end = offset)
                            .firstOrNull()?.let {
                                LegalConstants.openTermsAndConditions(context)
                            }
                        annotatedText.getStringAnnotations(tag = "privacy", start = offset, end = offset)
                            .firstOrNull()?.let {
                                LegalConstants.openPrivacyPolicy(context)
                            }
                    }
                )
            }

            Button(
                onClick = {
                    AnalyticsManager.getInstance(context).logOnboardingCompleted()
                    onComplete()
                },
                enabled = consentChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding()
                    .height(56.dp)
                    .alpha(alpha)
                    .testTag("onboarding_primary_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue,
                    contentColor = Color.White,
                    disabledContainerColor = primaryBlue.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
