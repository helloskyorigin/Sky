#!/bin/bash
cat << 'INNER_EOF' > app/src/main/java/com/example/AnalysisResultScreen.kt
package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.PremiumColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisResultScreen(
    modifier: Modifier = Modifier,
    analysis: MessageAnalysis,
    onBack: () -> Unit = {},
    onAnalyzeAnother: () -> Unit = {}
) {
    val context = LocalContext.current
    val isDark = LocalIsDark.current
    val primaryBlue = PremiumColors.PrimaryAccent
    val textPrimary = if (isDark) Color.White else Color(0xFF111111)
    val textSecondary = if (isDark) Color(0xFFA1A1AA) else Color(0xFF6E6E73)
    val bgColor = MaterialTheme.colorScheme.background
    val cardBg = if (isDark) Color(0xFF171A20) else Color.White
    val cardBorder = if (isDark) Color(0xFF252932) else Color(0xFFE5E7EB)

    val dangerColor = Color(0xFFEF4444)
    val warningColor = Color(0xFFF59E0B)
    val successColor = Color(0xFF10B981)

    val statusColor = when (analysis.status) {
        "Danger" -> dangerColor
        "Suspicious" -> warningColor
        else -> successColor
    }

    // Animations
    val enterAnim = remember { Animatable(0f) }
    var animatedScore by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        enterAnim.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
        val targetScore = analysis.score
        if (targetScore > 0) {
            val stepTime = if (targetScore > 50) 10L else 20L
            for (i in 1..targetScore) {
                animatedScore = i
                delay(stepTime)
            }
        }
    }

    var linkToOpen by remember { mutableStateOf<String?>(null) }
    if (linkToOpen != null) {
        AlertDialog(
            onDismissRequest = { linkToOpen = null },
            title = { Text("Open this link?") },
            text = { Text("This link may be unsafe. Please be careful when visiting unknown sites.") },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(linkToOpen))
                    context.startActivity(intent)
                    linkToOpen = null
                }) {
                    Text("Open in Browser", color = dangerColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { linkToOpen = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    val generateReport = {
        """
        Scam Shield AI Report
        Risk Score: ${analysis.score}/100
        Status: ${analysis.status}
        
        Summary:
        ${analysis.summary}
        
        Key Reasons:
        ${analysis.reasons.joinToString("\n") { "- $it" }}
        
        Recommended Action:
        Do not click links. Verify using the official source.
        """.trimIndent()
    }

    Scaffold(
        modifier = modifier.fillMaxSize().alpha(enterAnim.value),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text("Analysis Result", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary)
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = analysis.status.uppercase(),
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Hero Score Card
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = cardBorder,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                    val sweepProgress = (animatedScore / 100f) * 270f
                    drawArc(
                        color = statusColor,
                        startAngle = 135f,
                        sweepAngle = sweepProgress,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = animatedScore.toString(),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )
                    Text(
                        text = "RISK SCORE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // AI Summary
            ResultCard(title = "AI Summary", icon = Icons.Rounded.AutoAwesome, iconColor = primaryBlue, cardBg, cardBorder, textPrimary, textSecondary) {
                Text(analysis.summary, fontSize = 15.sp, color = textPrimary, lineHeight = 22.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Risk Indicators
            if (analysis.reasons.isNotEmpty()) {
                ResultCard(title = "Why was this detected?", icon = Icons.Rounded.GppMaybe, iconColor = warningColor, cardBg, cardBorder, textPrimary, textSecondary) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        analysis.reasons.forEach { reason ->
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Rounded.FiberManualRecord, contentDescription = null, tint = warningColor, modifier = Modifier.size(10.dp).padding(top = 6.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(reason, fontSize = 15.sp, color = textPrimary, lineHeight = 22.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Detected Links
            if (analysis.links.isNotEmpty()) {
                ResultCard(title = "Detected Links", icon = Icons.Rounded.Link, iconColor = dangerColor, cardBg, cardBorder, textPrimary, textSecondary) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        analysis.links.forEach { link ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Color(0xFF252932) else Color(0xFFF3F4F6),
                                onClick = { linkToOpen = link }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(link, color = primaryBlue, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Link", link))
                                            Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy Link", tint = textSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recommended Action
            ResultCard(title = "What should you do?", icon = Icons.Rounded.VerifiedUser, iconColor = successColor, cardBg, cardBorder, textPrimary, textSecondary) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val actions = listOf(
                        "Do not click any links.",
                        "Do not share OTP or personal information.",
                        "Verify directly through the official app or website.",
                        "Delete the message if you do not trust the sender."
                    )
                    actions.forEachIndexed { index, action ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text("${index + 1}.", fontSize = 15.sp, color = textSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(action, fontSize = 15.sp, color = textPrimary, lineHeight = 22.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Explain Like I'm 15
            ResultCard(title = "Explain Like I'm 15", icon = Icons.Rounded.Lightbulb, iconColor = Color(0xFF8B5CF6), cardBg, cardBorder, textPrimary, textSecondary) {
                Text(analysis.explain15, fontSize = 15.sp, color = textPrimary, lineHeight = 22.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Actions
            val interactionSourceCopy = remember { MutableInteractionSource() }
            val isPressedCopy by interactionSourceCopy.collectIsPressedAsState()
            val scaleCopy by animateFloatAsState(if (isPressedCopy) 0.96f else 1f)
            
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Report", generateReport()))
                    Toast.makeText(context, "Report Copied", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).scale(scaleCopy),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF252932) else Color(0xFFF3F4F6), contentColor = textPrimary),
                interactionSource = interactionSourceCopy
            ) {
                Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy Report", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            val interactionSourceShare = remember { MutableInteractionSource() }
            val isPressedShare by interactionSourceShare.collectIsPressedAsState()
            val scaleShare by animateFloatAsState(if (isPressedShare) 0.96f else 1f)

            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, generateReport())
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Safety Result")
                    context.startActivity(shareIntent)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).scale(scaleShare),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White),
                interactionSource = interactionSourceShare
            ) {
                Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Safety Result", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            TextButton(onClick = onAnalyzeAnother) {
                Text("Analyze Another Message", color = primaryBlue, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ResultCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
INNER_EOF
