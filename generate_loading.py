import os

content = """package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.PremiumRoyalBlue
import com.example.ui.theme.SecureGreen
import com.example.ui.theme.WarnOrange
import com.example.ui.theme.DangerRed
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun AnalysisLoadingScreen(
    modifier: Modifier = Modifier,
    textToAnalyze: String = "",
    onBack: () -> Unit = {},
    onAnalysisComplete: () -> Unit = {}
) {
    val steps = remember {
        listOf(
            "Reading message",
            "Detecting suspicious keywords",
            "Checking phishing language",
            "Extracting URLs",
            "Verifying domains",
            "Checking scam database",
            "AI semantic analysis",
            "Calculating risk score",
            "Preparing final report"
        )
    }

    val trustMessages = remember {
        listOf(
            "Analyzing writing style...",
            "Checking urgency indicators...",
            "Inspecting links...",
            "Comparing with known scams...",
            "Running AI semantic engine...",
            "Evaluating financial risk...",
            "Preparing recommendation..."
        )
    }

    // Determine final status
    val finalStatus = remember(textToAnalyze) {
        val inputLower = textToAnalyze.lowercase()
        val score = when {
            inputLower.contains("win") || inputLower.contains("prize") || inputLower.contains("reward") || inputLower.contains("gift") -> 90
            inputLower.contains("urgent") || inputLower.contains("suspend") || inputLower.contains("account") || inputLower.contains("bank") -> 80
            inputLower.contains("offer") || inputLower.contains("discount") || inputLower.contains("deal") -> 60
            else -> 20
        }
        when {
            score >= 80 -> "Danger"
            score >= 50 -> "Suspicious"
            else -> "Safe"
        }
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }
    var currentTrustMessageIndex by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(400, easing = LinearEasing),
        label = "Progress"
    )

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        for (i in steps.indices) {
            currentStepIndex = i
            progress = (i + 1).toFloat() / steps.size
            delay((250..450).random().toLong())
            listState.animateScrollToItem(if (i > 2) i - 2 else 0)
        }
        isFinished = true
        progress = 1.0f
        delay(1500)
        onAnalysisComplete()
    }

    LaunchedEffect(isFinished) {
        if (!isFinished) {
            while (true) {
                delay(1500)
                currentTrustMessageIndex = (currentTrustMessageIndex + 1) % trustMessages.size
            }
        }
    }

    val isDark = LocalIsDark.current
    val textDark = MaterialTheme.colorScheme.onBackground
    val textGray = if (isDark) Color(0xFFA1A1AA) else Color(0xFF6E6E73)
    val backgroundTrack = if (isDark) Color(0xFF1E222B) else Color(0xFFE5E7EB)
    val bgColor = MaterialTheme.colorScheme.background

    val shieldGlowColor by animateColorAsState(
        targetValue = if (isFinished) {
            when(finalStatus) {
                "Safe" -> SecureGreen
                "Suspicious" -> WarnOrange
                else -> DangerRed
            }
        } else {
            PremiumRoyalBlue
        },
        animationSpec = tween(800),
        label = "ShieldGlow"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "AI_Loading_Animations")

    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAngle"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Background soft radial glow
        Canvas(modifier = Modifier.fillMaxSize().align(Alignment.Center)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        shieldGlowColor.copy(alpha = 0.15f),
                        shieldGlowColor.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.35f),
                    radius = 350.dp.toPx()
                ),
                radius = 350.dp.toPx(),
                center = Offset(size.width * 0.5f, size.height * 0.35f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (isDark) MaterialTheme.colorScheme.surface else Color.White, shape = CircleShape)
                        .border(1.dp, if(isDark) Color(0xFF252932) else Color(0xFFE5E7EB), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        tint = textDark
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "AI Security Analysis",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .background(PremiumRoyalBlue.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AI Engine Active",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PremiumRoyalBlue
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CENTER ANIMATION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Radar Scan Ring
                Canvas(modifier = Modifier.size(200.dp)) {
                    val stroke = 1.dp.toPx()
                    drawCircle(
                        color = shieldGlowColor.copy(alpha = 0.2f),
                        style = Stroke(width = stroke)
                    )
                    drawCircle(
                        color = shieldGlowColor.copy(alpha = 0.1f),
                        radius = size.width / 2 - 30.dp.toPx(),
                        style = Stroke(width = stroke)
                    )
                    
                    rotate(radarAngle) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                0.0f to Color.Transparent,
                                0.5f to Color.Transparent,
                                0.95f to shieldGlowColor.copy(alpha = 0.4f),
                                1.0f to shieldGlowColor.copy(alpha = 0.8f)
                            ),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )
                    }
                }
                
                // Particles
                val particles = remember { List(8) { Offset(Random.nextFloat(), Random.nextFloat()) } }
                val particleRotation by infiniteTransition.animateFloat(
                    initialValue = 0f, targetValue = 360f,
                    animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "ParticleRot"
                )
                
                Canvas(modifier = Modifier.size(220.dp)) {
                    rotate(particleRotation) {
                        particles.forEach { offset ->
                            val x = offset.x * size.width
                            val y = offset.y * size.height
                            drawCircle(
                                color = shieldGlowColor.copy(alpha = 0.6f),
                                radius = 2.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }
                }

                // AI Breathing Glow
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(shieldGlowColor.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // 3D Premium Shield
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .semantics { contentDescription = "Security Shield" }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        val shieldPath = Path().apply {
                            moveTo(w * 0.5f, h * 0.05f)
                            quadraticTo(w * 0.22f, h * 0.05f, w * 0.1f, h * 0.2f)
                            cubicTo(w * 0.1f, h * 0.6f, w * 0.25f, h * 0.85f, w * 0.5f, h * 0.98f)
                            cubicTo(w * 0.75f, h * 0.85f, w * 0.9f, h * 0.6f, w * 0.9f, h * 0.2f)
                            quadraticTo(w * 0.78f, h * 0.05f, w * 0.5f, h * 0.05f)
                            close()
                        }
                        
                        // Darker background layer
                        drawPath(
                            path = shieldPath,
                            color = shieldGlowColor.copy(alpha = 0.4f)
                        )
                        
                        // Front Gradient Layer slightly smaller to create 3D bevel
                        val innerW = w * 0.9f
                        val innerH = h * 0.9f
                        val offsetX = w * 0.05f
                        val offsetY = h * 0.05f
                        
                        val innerPath = Path().apply {
                            moveTo(offsetX + innerW * 0.5f, offsetY + innerH * 0.05f)
                            quadraticTo(offsetX + innerW * 0.22f, offsetY + innerH * 0.05f, offsetX + innerW * 0.1f, offsetY + innerH * 0.2f)
                            cubicTo(offsetX + innerW * 0.1f, offsetY + innerH * 0.6f, offsetX + innerW * 0.25f, offsetY + innerH * 0.85f, offsetX + innerW * 0.5f, offsetY + innerH * 0.98f)
                            cubicTo(offsetX + innerW * 0.75f, offsetY + innerH * 0.85f, offsetX + innerW * 0.9f, offsetY + innerH * 0.6f, offsetX + innerW * 0.9f, offsetY + innerH * 0.2f)
                            quadraticTo(offsetX + innerW * 0.78f, offsetY + innerH * 0.05f, offsetX + innerW * 0.5f, offsetY + innerH * 0.05f)
                            close()
                        }
                        
                        drawPath(
                            path = innerPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    shieldGlowColor.copy(alpha = 0.9f),
                                    shieldGlowColor
                                )
                            )
                        )
                        
                        // Center icon/pulse
                        val aiCorePath = Path().apply {
                            val cw = w * 0.35f
                            val ch = h * 0.35f
                            val cx = w * 0.5f
                            val cy = h * 0.5f
                            moveTo(cx, cy - ch/2)
                            quadraticTo(cx, cy, cx + cw/2, cy)
                            quadraticTo(cx, cy, cx, cy + ch/2)
                            quadraticTo(cx, cy, cx - cw/2, cy)
                            quadraticTo(cx, cy, cx, cy - ch/2)
                            close()
                        }
                        drawPath(path = aiCorePath, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PROGRESS SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    AnimatedContent(
                        targetState = currentTrustMessageIndex,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                        },
                        label = "TrustMessage"
                    ) { index ->
                        Text(
                            text = if (isFinished) "Analysis complete." else trustMessages[index],
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = shieldGlowColor
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(backgroundTrack, shape = RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(shieldGlowColor.copy(alpha = 0.7f), shieldGlowColor)
                                ),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LIVE ANALYSIS STEPS (CHECKLIST)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(steps) { index, stepName ->
                    val isCompleted = index < currentStepIndex
                    val isAnalyzing = index == currentStepIndex && !isFinished
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted || (isFinished && index == steps.lastIndex)) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = SecureGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else if (isAnalyzing) {
                                val rot by infiniteTransition.animateFloat(
                                    initialValue = 0f, targetValue = 360f,
                                    animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing)), label = "Hourglass"
                                )
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = "Analyzing",
                                    tint = shieldGlowColor,
                                    modifier = Modifier.size(20.dp).rotate(rot)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(textGray.copy(alpha = 0.3f), CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = stepName,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = if (isAnalyzing) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCompleted || isFinished) textDark else if (isAnalyzing) shieldGlowColor else textGray
                            )
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // BOTTOM SECURITY CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFF8FAFC)
                ),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF252932) else Color(0xFFE5E7EB)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security",
                            tint = SecureGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your analysis is private",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textDark
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Messages are processed securely.\n• No permanent storage.\n• Data is encrypted during analysis.\n• Privacy-first AI scanning.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = textGray,
                            lineHeight = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Usually takes 2–5 seconds.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = textGray,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
"""

with open("app/src/main/java/com/example/AnalysisLoadingScreen.kt", "w") as f:
    f.write(content)

print("File updated.")
