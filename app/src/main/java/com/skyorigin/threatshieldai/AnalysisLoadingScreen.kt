package com.skyorigin.threatshieldai

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
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
    val isHindi = false

    // State management for API execution
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var hasFailed by remember { mutableStateOf(false) }
    var analysisResultState by remember { mutableStateOf<MessageAnalysis?>(null) }
    var isApiCallFinished by remember { mutableStateOf(false) }
    var isExiting by remember { mutableStateOf(false) }

    // 1. Calculate ideal scan duration depending on message length
    val stepDurationMs = remember(textToAnalyze) {
        val len = textToAnalyze.length
        when {
            len <= 80 -> 2800L / 8
            len <= 200 -> 3400L / 8
            len <= 500 -> 4300L / 8
            else -> 5400L / 8
        }
    }

    val containsUrl = remember(textToAnalyze) {
        android.util.Patterns.WEB_URL.matcher(textToAnalyze).find()
    }

    // 4. Checklist steps matching progress
    val stepsEn = listOf(
        "Reading your message...",
        "Understanding context...",
        "Checking suspicious language...",
        "Looking for urgency patterns...",
        "Checking financial requests...",
        if (containsUrl) "Checking links..." else "No links detected",
        "Running AI security analysis...",
        "Building security report...",
        "Analysis complete."
    )

    val stepsHi = listOf(
        "आपका संदेश पढ़ा जा रहा है...",
        "संदर्भ समझा जा रहा है...",
        "संदिग्ध भाषा की जांच...",
        "जल्दबाजी के पैटर्न की खोज...",
        "वित्तीय अनुरोधों की जांच...",
        if (containsUrl) "लिंक की जांच की जा रही है..." else "कोई लिंक नहीं मिला",
        "एआई सुरक्षा विश्लेषण चल रहा है...",
        "सुरक्षा रिपोर्ट तैयार की जा रही है...",
        "विश्लेषण पूरा हुआ।"
    )

    val steps = if (isHindi) stepsHi else stepsEn
    val totalSteps = steps.size

    var currentStepIndex by remember { mutableStateOf(0) }

    LaunchedEffect(stepDurationMs, isApiCallFinished) {
        // Step 0: Reading your message...
        delay(stepDurationMs)
        currentStepIndex = 1
        
        // Step 1: Understanding context...
        delay(stepDurationMs)
        currentStepIndex = 2
        
        // Step 2: Checking suspicious language...
        delay(stepDurationMs)
        currentStepIndex = 3
        
        // Step 3: Looking for urgency patterns...
        delay(stepDurationMs)
        currentStepIndex = 4
        
        // Step 4: Checking financial requests...
        delay(stepDurationMs)
        currentStepIndex = 5
        
        // Step 5: Checking links / No links...
        delay(stepDurationMs)
        currentStepIndex = 6
        
        // Step 6: Running AI security analysis...
        // Wait for the API
        val minWaitTime = stepDurationMs
        val startTime = System.currentTimeMillis()
        
        while (!isApiCallFinished) {
            delay(50L)
        }
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < minWaitTime) {
            delay(minWaitTime - elapsed)
        }
        currentStepIndex = 7
        
        // Step 7: Building security report...
        delay(stepDurationMs)
        currentStepIndex = 8
        // Step 8: Analysis complete.
    }

    val animatedProgress by animateFloatAsState(
        targetValue = currentStepIndex.toFloat() / (totalSteps - 1).toFloat(),
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "animatedProgress"
    )

    // Provide light haptic feedbacks as steps tick over
    LaunchedEffect(currentStepIndex) {
        if (currentStepIndex in 1..totalSteps) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    // Observe single source of truth ScanState from ViewModel
    val scanState by viewModel.scanState.collectAsState()

    LaunchedEffect(scanState) {
        when (val state = scanState) {
            is ScamLensViewModel.ScanState.Idle -> {
                isApiCallFinished = false
                analysisResultState = null
                hasFailed = false
                showErrorDialog = null
            }
            is ScamLensViewModel.ScanState.Scanning -> {
                isApiCallFinished = false
                analysisResultState = null
                hasFailed = false
                showErrorDialog = null
            }
            is ScamLensViewModel.ScanState.Success -> {
                analysisResultState = state.result
                isApiCallFinished = true
                hasFailed = false
                showErrorDialog = null
            }
            is ScamLensViewModel.ScanState.Failed -> {
                if (analysisResultState == null && !isExiting) {
                    hasFailed = true
                    showErrorDialog = state.error
                    isApiCallFinished = true
                }
            }
        }
    }

    // 5. Trigger actual ViewModel API analysis on launch
    fun startAnalysis() {
        showErrorDialog = null
        hasFailed = false
        isApiCallFinished = false
        analysisResultState = null
        currentStepIndex = 0

        if (textToAnalyze.isNotEmpty()) {
            viewModel.performRealAnalysis(
                context = context,
                text = textToAnalyze,
                isHindi = isHindi
            )
        }
    }

    LaunchedEffect(textToAnalyze) {
        if (textToAnalyze.isNotEmpty()) {
            startAnalysis()
        }
    }

    // 6. Navigation out on completion
    val exitScale by animateFloatAsState(
        targetValue = if (isExiting) 0.95f else 1.0f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "exitScale"
    )

    val exitAlpha by animateFloatAsState(
        targetValue = if (isExiting) 0f else 1.0f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "exitAlpha"
    )

    LaunchedEffect(currentStepIndex, isApiCallFinished, analysisResultState) {
        if (currentStepIndex == totalSteps - 1 && isApiCallFinished && analysisResultState != null && !hasFailed) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(300) // Pause briefly
            isExiting = true
            delay(400) // Wait for fade transition
            onAnalysisComplete(analysisResultState!!)
        }
    }

    // High fidelity UI animations (Pulse, breathing, rotating gradients)
    val pulseTransition = rememberInfiniteTransition(label = "breathing")
    val breathingScale by pulseTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val breathingAlpha by pulseTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val rotationAngle by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val particleRotationAngle by pulseTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleRotation"
    )

    // Premium radial background gradient
    val bgBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFF0F1626), // Dark navy core
            Color(0xFF05070C)  // Deep rich black edges
        ),
        center = Offset.Unspecified,
        radius = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
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
            // TOP BAR AREA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.cancelAnalysis()
                        onBack()
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isHindi) "एआई सुरक्षा विश्लेषण" else "AI Security Analysis",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.3).sp
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF30D158)) // Pulsing green online state
                        )
                        Text(
                            text = if (isHindi) "एआई इंजन सक्रिय" else "AI Engine Active",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // MESSAGE GLASS CARD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF161B26).copy(alpha = 0.4f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Color(0xFF0A84FF),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "संदेश का विश्लेषण किया जा रहा है" else "Message Being Analyzed",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0A84FF),
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    // AI Secure badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF30D158).copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = null,
                            tint = Color(0xFF30D158),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "AI SECURE",
                            style = TextStyle(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF30D158)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = textToAnalyze,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${textToAnalyze.length} ${if (isHindi) "वर्ण" else "chars"}",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        )
                        Text(
                            text = "•",
                            style = TextStyle(fontSize = 11.sp, color = Color.White.copy(alpha = 0.3f))
                        )
                        Text(
                            text = if (isHindi) "मैनुअल इनपुट" else "Source: Manual Input",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        )
                    }

                    if (containsUrl) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF9F0A).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Link,
                                contentDescription = null,
                                tint = Color(0xFFFF9F0A),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "URL DETECTED",
                                style = TextStyle(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9F0A)
                                )
                            )
                        }
                    }
                }

                val urlProgress = viewModel.urlScanProgressState
                if (containsUrl) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Link,
                                        contentDescription = null,
                                        tint = Color(0xFF0A84FF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "${urlProgress?.detectedCount ?: 1} यूआरएल मिले" else "${urlProgress?.detectedCount ?: 1} URL Detected",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                
                                val status = urlProgress?.status ?: "scanning"
                                val (statusText, statusColor, statusBg) = when (status) {
                                    "safe" -> Triple(if (isHindi) "सुरक्षित" else "Safe", Color(0xFF30D158), Color(0xFF30D158).copy(alpha = 0.15f))
                                    "danger" -> Triple(if (isHindi) "खतरा" else "Danger", Color(0xFFFF453A), Color(0xFFFF453A).copy(alpha = 0.15f))
                                    "failed" -> Triple(if (isHindi) "अधूरी जांच" else "Partial Check", Color(0xFFFF9F0A), Color(0xFFFF9F0A).copy(alpha = 0.15f))
                                    else -> Triple(if (isHindi) "जांच जारी..." else "Scanning...", Color(0xFF0A84FF), Color(0xFF0A84FF).copy(alpha = 0.15f))
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(statusBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = statusText.uppercase(),
                                        color = statusColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = urlProgress?.verdict ?: (if (isHindi) "यूआरएल की जांच हो रही है..." else "Analyzing detected link..."),
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            LinearProgressIndicator(
                                progress = { urlProgress?.progress ?: 0.1f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(1.5.dp)),
                                color = Color(0xFF0A84FF),
                                trackColor = Color.White.copy(alpha = 0.08f),
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = if (isHindi) "अंतिम परिणाम से पहले यूआरएल की जांच की जाएगी" else "URL will be checked before final scan",
                                color = Color.White.copy(alpha = 0.35f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1.0f))

            // CENTER LOGO AREA WITH GORGEOUS ROTATING LIGHT RINGS
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating sweeping laser visual
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius1 = 70.dp.toPx()
                    val radius2 = 82.dp.toPx()
                    val radius3 = 94.dp.toPx()
                    val centerOffset = size.width / 2

                    // Concentric background rings
                    drawCircle(
                        color = Color(0xFF0A84FF).copy(alpha = 0.06f * breathingAlpha),
                        radius = radius3,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF00D2FF).copy(alpha = 0.1f * breathingAlpha),
                        radius = radius2,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF0A84FF).copy(alpha = 0.18f * breathingAlpha),
                        radius = radius1,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Slow elegant glowing radar arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF0A84FF).copy(alpha = 0f),
                                Color(0xFF00D2FF).copy(alpha = 0.5f),
                                Color(0xFF0A84FF).copy(alpha = 0f)
                            )
                        ),
                        startAngle = rotationAngle,
                        sweepAngle = 100f,
                        useCenter = false,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Subtle floating premium particles
                    val radians1 = Math.toRadians((particleRotationAngle + 30).toDouble())
                    val radians2 = Math.toRadians((particleRotationAngle + 170).toDouble())
                    val radians3 = Math.toRadians((particleRotationAngle + 290).toDouble())
                    val particleRadius = 76.dp.toPx()

                    drawCircle(
                        color = Color(0xFF00D2FF).copy(alpha = 0.6f),
                        radius = 2.2.dp.toPx(),
                        center = Offset(
                            (centerOffset + particleRadius * Math.cos(radians1)).toFloat(),
                            (centerOffset + particleRadius * Math.sin(radians1)).toFloat()
                        )
                    )
                    drawCircle(
                        color = Color(0xFF0A84FF).copy(alpha = 0.5f),
                        radius = 1.6.dp.toPx(),
                        center = Offset(
                            (centerOffset + particleRadius * Math.cos(radians2)).toFloat(),
                            (centerOffset + particleRadius * Math.sin(radians2)).toFloat()
                        )
                    )
                    drawCircle(
                        color = Color(0xFF00D2FF).copy(alpha = 0.7f),
                        radius = 2.8.dp.toPx(),
                        center = Offset(
                            (centerOffset + particleRadius * Math.cos(radians3)).toFloat(),
                            (centerOffset + particleRadius * Math.sin(radians3)).toFloat()
                        )
                    )
                }

                // Blurred soft blue gradient behind logo to make it glow natively
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF0A84FF).copy(alpha = 0.15f * breathingAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Official Crisp Shield Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_official_logo),
                    contentDescription = "ThreatShield AI Logo",
                    modifier = Modifier
                        .height(48.dp)
                        .scale(breathingScale)
                        .alpha(breathingAlpha),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.weight(1.0f))

            // LIVE ANALYSIS CHECKLIST CONTAINER CARD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F1524).copy(alpha = 0.7f))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0A84FF))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "सुरक्षा इंजन चेकलिस्ट" else "Security Engine Checklist",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.4f),
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scroll to active index seamlessly or show rows elegantly
                val scrollState = rememberScrollState()
                LaunchedEffect(currentStepIndex) {
                    val targetScroll = (currentStepIndex * 32).dp.value.toInt()
                    scrollState.animateScrollTo(targetScroll)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    steps.forEachIndexed { index, stepText ->
                        if (index <= currentStepIndex) {
                            val isCompleted = index < currentStepIndex
                            val isActive = index == currentStepIndex

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp)
                                    .animateContentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedContent(
                                    targetState = if (isCompleted) "done" else "active",
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(150))
                                    },
                                    label = "stepIconAnimation"
                                ) { state ->
                                    when (state) {
                                        "done" -> {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = "Completed",
                                                tint = Color(0xFF30D158), // iOS green checkmark
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        "active" -> {
                                            Box(
                                                modifier = Modifier.size(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(14.dp),
                                                    color = Color(0xFF0A84FF),
                                                    strokeWidth = 2.dp
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = stepText,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isActive) Color.White else Color.White.copy(alpha = 0.85f)
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1.0f))

            // BOTTOM PROGRESS BAR & TRUST SIGNALS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Premium Horizontal Progress Gradient Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0A84FF),
                                        Color(0xFF00D2FF)
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = Color(0xFF30D158),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isHindi) "विश्लेषण सुरक्षित है" else "Analysis is secure",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF30D158)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isHindi) "संदेश एन्क्रिप्टेड हैं। कोई स्थायी स्टोरेज नहीं।" else "Messages are encrypted. No permanent storage.",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.4f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Beautiful iOS-style Error Alert Dialog
        showErrorDialog?.let { errorType ->
            val dialogTitle = when (errorType) {
                "INTERNET_DISCONNECTED", "CONNECTION_LOST" -> if (isHindi) "कनेक्शन त्रुटि" else "Connection Error"
                else -> if (isHindi) "विश्लेषण विफल" else "Analysis Failed"
            }

            val dialogMessage = when (errorType) {
                "INTERNET_DISCONNECTED", "CONNECTION_LOST" ->
                    if (isHindi) "कृपया अपना इंटरनेट कनेक्शन जांचें और पुनः प्रयास करें।"
                    else "Please check your internet connection and try again."
                else ->
                    if (isHindi) "सुरक्षित विश्लेषण पूरा नहीं हो सका। कृपया पुनः प्रयास करें।"
                    else "Secure analysis could not be completed. Please try again."
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
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = dialogMessage,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                },
                containerColor = Color(0xFF161B26),
                confirmButton = {
                    TextButton(
                        onClick = {
                            hasFailed = false
                            showErrorDialog = null
                            startAnalysis()
                        }
                    ) {
                        Text(text = if (isHindi) "पुनः प्रयास करें" else "Retry", color = Color(0xFF0A84FF), fontWeight = FontWeight.Bold)
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

