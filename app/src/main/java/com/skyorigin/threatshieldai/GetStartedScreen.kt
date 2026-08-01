package com.skyorigin.threatshieldai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GetStartedScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }
    var consentChecked by remember { mutableStateOf(false) }
    var showErrorValidation by remember { mutableStateOf(false) }
    var isHighlightingCheckbox by remember { mutableStateOf(false) }
    
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val primaryBlue = Color(0xFF2563EB)

    LaunchedEffect(Unit) {
        isVisible = true
    }

    fun triggerValidation() {
        showErrorValidation = true
        isHighlightingCheckbox = true
        scope.launch {
            val keyframes = listOf(-14f, 14f, -10f, 10f, -5f, 5f, 0f)
            for (target in keyframes) {
                shakeOffset.animateTo(
                    targetValue = target,
                    animationSpec = tween(durationMillis = 45, easing = LinearEasing)
                )
            }
            delay(1200)
            isHighlightingCheckbox = false
        }
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
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Center Branding Group
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dark),
                    contentDescription = "ThreatShield AI Logo",
                    modifier = Modifier
                        .size(340.dp)
                        .scale(scale)
                        .alpha(alpha),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFFFFFFFF))) {
                            append("Threat")
                        }
                        withStyle(SpanStyle(color = Color(0xFF2F7BFF))) {
                            append("Shield AI")
                        }
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(alpha)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFFFFFFFF))) {
                            append("Stay ")
                        }
                        withStyle(SpanStyle(color = Color(0xFF2F7BFF))) {
                            append("Safe.")
                        }
                    },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(alpha)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your digital world\ndeserves better protection.",
                    fontSize = 16.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.alpha(alpha)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Action Group
            val annotatedText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF9CA3AF), fontSize = 14.sp)) {
                    append("I agree to the ")
                }
                pushStringAnnotation(tag = "terms", annotation = "terms")
                withStyle(style = SpanStyle(color = primaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                    append("Terms of Service")
                }
                pop()
                withStyle(style = SpanStyle(color = Color(0xFF9CA3AF), fontSize = 14.sp)) {
                    append(" and ")
                }
                pushStringAnnotation(tag = "privacy", annotation = "privacy")
                withStyle(style = SpanStyle(color = primaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                    append("Privacy Policy")
                }
                pop()
                withStyle(style = SpanStyle(color = Color(0xFF9CA3AF), fontSize = 14.sp)) {
                    append(".")
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = shakeOffset.value.dp)
                        .alpha(alpha),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = consentChecked,
                        onCheckedChange = { checked ->
                            consentChecked = checked
                            if (checked) {
                                showErrorValidation = false
                                isHighlightingCheckbox = false
                            }
                        },
                        modifier = Modifier.testTag("consent_checkbox"),
                        colors = CheckboxDefaults.colors(
                            checkedColor = primaryBlue,
                            uncheckedColor = if (isHighlightingCheckbox) primaryBlue else Color(0xFF9CA3AF)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ClickableText(
                        text = annotatedText,
                        style = TextStyle(color = Color.White),
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

                AnimatedVisibility(
                    visible = showErrorValidation && !consentChecked,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -10 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -10 })
                ) {
                    Text(
                        text = "Please agree to the Terms & Privacy Policy to continue.",
                        fontSize = 13.sp,
                        color = Color(0xFF60A5FA),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, top = 4.dp),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .alpha(alpha)
                ) {
                    Button(
                        onClick = {
                            AnalyticsManager.getInstance(context).logOnboardingCompleted()
                            onComplete()
                        },
                        enabled = consentChecked,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("onboarding_primary_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White,
                            disabledContainerColor = primaryBlue.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.6f)
                        )
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
}

