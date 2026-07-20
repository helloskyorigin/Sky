import os

with open('app/src/main/java/com/example/AnalysisLoadingScreen.kt', 'w') as f:
    f.write("""package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisLoadingScreen(
    modifier: Modifier = Modifier,
    textToAnalyze: String = "",
    viewModel: ScamLensViewModel,
    onBack: () -> Unit = {},
    onAnalysisComplete: (MessageAnalysis) -> Unit = {}
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val isHindi = remember { viewModel.currentLanguage == "hi" }

    // Core variables for the API states
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var hasFailed by remember { mutableStateOf(false) }
    var analysisResultState by remember { mutableStateOf<MessageAnalysis?>(null) }
    var isApiCallFinished by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    // 1. Calculate dynamic scan duration from the message length
    val scanDurationMs = remember(textToAnalyze) {
        val len = textToAnalyze.length
        when {
            len <= 80 -> 2500L
            len <= 200 -> 3500L
            len <= 500 -> 4000L
            else -> 5000L
        }
    }

    // 2. Continuous stopwatch state
    var elapsedMs by remember { mutableStateOf(0L) }
    LaunchedEffect(scanDurationMs, isApiCallFinished) {
        val interval = 16L // ~60fps
        while (elapsedMs < scanDurationMs && !isApiCallFinished) {
            delay(interval)
            elapsedMs = (elapsedMs + interval).coerceAtMost(scanDurationMs)
        }
    }

    // 3. Natural progress interpolation
    val targetProgress = remember(elapsedMs, scanDurationMs, isApiCallFinished) {
        if (isApiCallFinished && !hasFailed) {
            1.0f
        } else {
            (elapsedMs.toFloat() / scanDurationMs).coerceIn(0f, 0.95f)
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = if (isApiCallFinished) {
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
        } else {
            tween(durationMillis = 300, easing = LinearEasing)
        },
        label = "animatedProgress"
    )

    // 4. Checklist Steps
    val stepsEn = listOf(
        "Reading message",
        "Detecting suspicious keywords",
        "Checking for urgency patterns",
        "Checking payment or reward requests",
        "Checking URLs",
        "Checking sender manipulation",
        "Checking social engineering signals",
        "Generating AI risk analysis",
        "Finalizing report"
    )
    val stepsHi = listOf(
        "संदेश पढ़ा जा रहा है",
        "संदिग्ध कीवर्ड्स की जांच",
        "तत्काल पैटर्न की जांच",
        "भुगतान अनुरोधों की जांच",
        "यूआरएल सत्यापित किए जा रहे हैं",
        "प्रेषक हेरफेर की जांच",
        "सोशल इंजीनियरिंग की जांच",
        "एआई रिपोर्ट तैयार की जा रही है",
        "रिपोर्ट को अंतिम रूप दिया जा रहा है"
    )
    val steps = if (isHindi) stepsHi else stepsEn
    
    val totalSteps = steps.size
    
    // Determine which step we are on (0 to totalSteps - 1)
    val currentStepIndex = remember(animatedProgress) {
        if (animatedProgress >= 1f) {
            totalSteps // special index for 'Done'
        } else {
            (animatedProgress * totalSteps).toInt().coerceIn(0, totalSteps - 1)
        }
    }
    
    // Determine if the current step is "completing" (e.g. last 20% of its slot)
    val stepProgress = remember(animatedProgress, totalSteps) {
        (animatedProgress * totalSteps) % 1f
    }
    val isStepCompleting = stepProgress > 0.8f

    // 5. Trigger Real Analysis API
    fun startAnalysis() {
        showErrorDialog = null
        hasFailed = false
        isApiCallFinished = false
        analysisResultState = null
        elapsedMs = 0L

        viewModel.performRealAnalysis(
            context = context,
            text = textToAnalyze,
            isHindi = isHindi,
            onComplete = { result ->
                analysisResultState = result
                isApiCallFinished = true
            },
            onError = { error ->
                hasFailed = true
                showErrorDialog = error
                isApiCallFinished = true
            }
        )
    }

    LaunchedEffect(textToAnalyze) {
        startAnalysis()
    }

    // 6. Scale and fade transitions before navigation to results
    var isExiting by remember { mutableStateOf(false) }

    val exitScale by animateFloatAsState(
        targetValue = if (isExiting) 0.96f else 1.0f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "exitScale"
    )

    val exitAlpha by animateFloatAsState(
        targetValue = if (isExiting) 0f else 1.0f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "exitAlpha"
    )

    LaunchedEffect(animatedProgress, isApiCallFinished, analysisResultState) {
        if (animatedProgress >= 1.0f && isApiCallFinished && analysisResultState != null && !hasFailed) {
            isFinished = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(1000) // Brief pause to observe "Analysis complete"
            isExiting = true
            delay(400) // Allow exit animation to complete
            onAnalysisComplete(analysisResultState!!)
        }
    }

    // Subtle breathing animation for central icon
    val pulseTransition = rememberInfiniteTransition(label = "pulse_breathing")
    val breathingScale by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )
    
    val breathingAlpha by pulseTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0C)) // Matte dark background
            .alpha(exitAlpha)
            .scale(exitScale)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP AREA
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                // Subtle Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            viewModel.cancelAnalysis()
                            onBack()
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Go Back",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = if (isHindi) "एआई सुरक्षा विश्लेषण" else "AI Security Analysis",
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = (-0.3).sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    // Invisible spacer for balance
                    Spacer(modifier = Modifier.width(48.dp))
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (isHindi) "आपके संदेश का सुरक्षित रूप से विश्लेषण किया जा रहा है" else "Analyzing your message securely",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                )
            }

            // MESSAGE PREVIEW CARD
            val containsUrl = remember(textToAnalyze) {
                android.util.Patterns.WEB_URL.matcher(textToAnalyze).find()
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1C1C1E).copy(alpha = 0.6f))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.ChatBubbleOutline,
                        contentDescription = null,
                        tint = Color(0xFF0A84FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "संदेश का विश्लेषण किया जा रहा है" else "Message Being Analyzed",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0A84FF),
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = textToAnalyze,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 22.sp
                    ),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Length and Source
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${textToAnalyze.length} ${if(isHindi) "वर्ण" else "chars"}",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        )
                        Text(
                            text = " • ",
                            style = TextStyle(fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                        )
                        Text(
                            text = if (isHindi) "मैनुअल इनपुट" else "Manual input",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        )
                    }
                    
                    // URL badge
                    if (containsUrl) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0A84FF).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Link,
                                contentDescription = null,
                                tint = Color(0xFF0A84FF),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "URL",
                                style = TextStyle(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0A84FF)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // CENTER ANIMATION
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background track
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFF1C1C1E),
                        radius = size.width / 2,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }

                // Smooth Progress Ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color(0xFF0A84FF),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                // Pulsing Center Icon
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_official_logo),
                    contentDescription = "AI Analyzing",
                    modifier = Modifier
                        .size(48.dp)
                        .scale(breathingScale)
                        .alpha(breathingAlpha)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // LIVE SCANNING EXPERIENCE (Active Step text)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentStepIndex,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300, delayMillis = 100)) + 
                         slideInVertically(animationSpec = tween(400, easing = EaseOutQuart)) { height -> height / 2 }) togetherWith
                        (fadeOut(animationSpec = tween(200)) + 
                         slideOutVertically(animationSpec = tween(400, easing = EaseOutQuart)) { height -> -height / 2 })
                    },
                    label = "stepTextAnimation"
                ) { index ->
                    if (index == totalSteps) {
                        // Done state
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF34C759),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isHindi) "विश्लेषण पूर्ण हुआ" else "Analysis Complete",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            )
                        }
                    } else {
                        // Active state
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isStepCompleting) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = Color(0xFF0A84FF),
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF0A84FF),
                                    strokeWidth = 2.dp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = steps[index],
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // TRUST SIGNALS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrustBadge(
                    icon = Icons.Rounded.Lock,
                    text = if (isHindi) "सुरक्षित" else "Secure"
                )
                Spacer(modifier = Modifier.width(16.dp))
                TrustBadge(
                    icon = Icons.Rounded.VisibilityOff,
                    text = if (isHindi) "निजी" else "Private"
                )
                Spacer(modifier = Modifier.width(16.dp))
                TrustBadge(
                    icon = Icons.Rounded.AutoAwesome,
                    text = if (isHindi) "एआई सक्रिय" else "AI Active"
                )
            }
        }

        // Error Dialog
        showErrorDialog?.let { errorType ->
            val dialogTitle = when (errorType) {
                "INTERNET_DISCONNECTED", "CONNECTION_LOST" -> if (isHindi) "कनेक्शन त्रुटि" else "Connection Error"
                else -> if (isHindi) "विश्लेषण विफल" else "Analysis Failed"
            }
            
            val dialogMessage = when (errorType) {
                "INTERNET_DISCONNECTED", "CONNECTION_LOST" -> 
                    if (isHindi) "कृपया अपना इंटरनेट जांचें और पुनः प्रयास करें।" 
                    else "Please check your internet connection and try again."
                else -> 
                    if (isHindi) "विश्लेषण पूरा नहीं हो सका। कृपया पुनः प्रयास करें।" 
                    else "Could not complete the analysis. Please try again."
            }

            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFFF453A),
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = dialogTitle,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = dialogMessage, 
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 20.sp
                    )
                },
                containerColor = Color(0xFF1C1C1E),
                confirmButton = {
                    TextButton(
                        onClick = {
                            hasFailed = false
                            showErrorDialog = null
                            isFinished = false
                            isApiCallFinished = false
                            analysisResultState = null
                            startAnalysis()
                        }
                    ) {
                        Text(text = if (isHindi) "पुनः प्रयास करें" else "Retry", color = Color(0xFF0A84FF))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.cancelAnalysis()
                            onBack()
                        }
                    ) {
                        Text(
                            text = if (isHindi) "रद्द करें" else "Cancel", 
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun TrustBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.6f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF34C759),
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.6f)
            )
        )
    }
}
"""
