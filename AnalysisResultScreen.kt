package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalIsDark
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisResultScreen(
    modifier: Modifier = Modifier,
    analysis: MessageAnalysis,
    isHindi: Boolean = false,
    onBack: () -> Unit = {},
    onAnalyzeAnother: () -> Unit = {}
) {
    val isDark = LocalIsDark.current
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF9FAFB)

    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var lastAlpha by rememberSaveable { mutableFloatStateOf(0f) }
    var lastOffsetY by rememberSaveable { mutableFloatStateOf(30f) }
    val enterAnim = remember { Animatable(lastAlpha) }
    val slideAnim = remember { Animatable(lastOffsetY) }
    val view = LocalView.current
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isPdfGenerating by remember { mutableStateOf(false) }

    fun shareResult(ctx: Context, data: MessageAnalysis) {
        val statusUpper = data.status.uppercase()
        val textVerdict = data.getTextVerdict()
        val textVerdictStr = when (textVerdict.lowercase()) {
            "danger", "unsafe" -> "High Risk"
            "warning", "suspicious" -> "Suspicious"
            else -> "Low Risk / Safe"
        }
        
        val linksSection = if (data.links.isNotEmpty()) {
            val urlVerdict = data.getUrlVerdict()
            val urlVerdictStr = when (urlVerdict.lowercase()) {
                "danger", "unsafe" -> "Dangerous"
                "suspicious", "warning" -> "Suspicious"
                else -> "Safe"
            }
            "\nLink Verdict:\n$urlVerdictStr\n"
        } else {
            ""
        }
        
        val reasonsList = data.reasons.ifEmpty { data.signals }.ifEmpty { listOf("Unusual message pattern") }
        val reasonsStr = reasonsList.joinToString("\n") { "• $it" }
        
        val adviceList = data.advice.ifEmpty { listOf("Do not click links.", "Do not share OTP or passwords.", "Verify the sender.") }
        val adviceStr = adviceList.joinToString("\n") { "• $it" }
        
        val shareText = """
            ThreatShield AI Scan Result

            Verdict: $statusUpper
            Risk Score: ${data.score}/100

            Message Verdict:
            $textVerdictStr
            $linksSection
            Reasons:
            $reasonsStr

            Recommendation:
            $adviceStr

            Scanned with ThreatShield AI
        """.trimIndent()
        
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Scan Result").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ctx.startActivity(shareIntent)
    }

    LaunchedEffect(analysis.score, isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            launch { enterAnim.animateTo(1f, animationSpec = tween(350, easing = EaseOutCubic)) }
            launch { slideAnim.animateTo(0f, animationSpec = tween(350, easing = EaseOutCubic)) }
            
            when (analysis.status.lowercase()) {
                "danger", "unsafe" -> view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                "warning", "suspicious" -> view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                else -> view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
        } else {
            enterAnim.snapTo(1f)
            slideAnim.snapTo(0f)
        }
        lastAlpha = 1f
        lastOffsetY = 0f
    }

    val isDanger = analysis.status.lowercase() == "danger" || analysis.status.lowercase() == "unsafe"
    val isSuspicious = analysis.status.lowercase() == "warning" || analysis.status.lowercase() == "suspicious"
    
    val primaryColor = when {
        isDanger -> Color(0xFFFF2B55)
        isSuspicious -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize().alpha(enterAnim.value).offset(y = slideAnim.value.dp),
            containerColor = bgColor,
            topBar = { TopNavigationBar(onBack, isDark, primaryColor) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                ThreatShieldHeroCard(analysis, isDark, primaryColor)
                Spacer(modifier = Modifier.height(16.dp))
                MessageAnalysisCard(analysis, isDark, primaryColor)
                if (analysis.links.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinkScanCard(analysis, isDark, primaryColor)
                }
                Spacer(modifier = Modifier.height(16.dp))
                RecommendationCard(analysis, isDark, primaryColor)
                Spacer(modifier = Modifier.height(16.dp))
                
                ActionButtons(
                    onShareResult = {
                        shareResult(context, analysis)
                    },
                    onDownloadPdf = {
                        if (!isPdfGenerating) {
                            isPdfGenerating = true
                            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                ReportExportHelper.savePdfReportLocally(context, analysis, isHindi) { success, message ->
                                    scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                        isPdfGenerating = false
                                        Toast.makeText(context, message ?: "Operation completed", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    },
                    onAnalyzeAnother = onAnalyzeAnother,
                    isDark = isDark,
                    isProcessing = isPdfGenerating
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                FooterRow(analysis, isDark)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (isPdfGenerating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {}, // absorb touches
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color.White),
                    modifier = Modifier.padding(24.dp).widthIn(max = 280.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                        Text(
                            text = "Generating PDF report...",
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopNavigationBar(onBack: () -> Unit, isDark: Boolean, primaryColor: Color) {
    val iconBg = if (isDark) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.background(iconBg, CircleShape).size(40.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary, modifier = Modifier.size(20.dp))
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Security, contentDescription = "Logo", tint = primaryColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Scan Result", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textPrimary)
        }
        
        IconButton(
            onClick = { },
            modifier = Modifier.background(iconBg, CircleShape).size(40.dp)
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = textPrimary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ThreatShieldHeroCard(analysis: MessageAnalysis, isDark: Boolean, primaryColor: Color) {
    val isDanger = analysis.status.lowercase() == "danger" || analysis.status.lowercase() == "unsafe"
    val isSuspicious = analysis.status.lowercase() == "warning" || analysis.status.lowercase() == "suspicious"
    
    val verdictLabel = if (isDanger) "DANGER" else if (isSuspicious) "SUSPICIOUS" else "SAFE"
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
    
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: Large Security Shield
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.15f))
                    )
                    Icon(
                        if (isDanger || isSuspicious) Icons.Rounded.Security else Icons.Rounded.VerifiedUser,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // CENTER: Danger/Safe/Suspicious, Small subtitle, Risk Score
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Surface(
                        color = primaryColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = verdictLabel, 
                            color = primaryColor, 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("This message looks", fontSize = 11.sp, color = textSecondary, fontWeight = FontWeight.Medium)
                    Text(
                        if (isDanger) "very risky" else if (isSuspicious) "suspicious" else "safe", 
                        fontSize = 14.sp, 
                        color = primaryColor, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("RISK SCORE", fontSize = 9.sp, color = textSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${analysis.score}", fontSize = 38.sp, fontWeight = FontWeight.Black, color = primaryColor, lineHeight = 38.sp)
                        Text("/100", fontSize = 14.sp, color = textSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // RIGHT: Large Semi Circle Risk Meter
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    GaugeMeter(score = analysis.score, primaryColor = primaryColor, isDark = isDark, modifier = Modifier.size(80.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = dividerColor)
            Spacer(modifier = Modifier.height(16.dp))
            
            // BOTTOM ROW: Confidence, Threat Level, Scan Depth (Equal width weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Confidence
                val conf = analysis.confidence.takeIf { it > 0 } ?: 85
                val confLabel = if (conf >= 80) "High" else if (conf >= 50) "Medium" else "Low"
                HeroStatBlock(
                    title = "CONFIDENCE",
                    value = "$confLabel ($conf%)",
                    icon = Icons.Rounded.TrackChanges,
                    iconColor = textSecondary,
                    textColor = textPrimary,
                    labelColor = textSecondary,
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(modifier = Modifier.width(48.dp).height(3.dp).clip(CircleShape).background(if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))) {
                        Box(modifier = Modifier.fillMaxWidth(conf / 100f).fillMaxHeight().clip(CircleShape).background(primaryColor))
                    }
                }
                
                // Threat Level
                val tlLabel = if (isDanger) "Severe" else if (isSuspicious) "Elevated" else "Normal"
                HeroStatBlock(
                    title = "THREAT LEVEL",
                    value = tlLabel,
                    icon = Icons.Rounded.Shield,
                    iconColor = primaryColor,
                    textColor = primaryColor,
                    labelColor = textSecondary,
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        val dots = if (isDanger) 5 else if (isSuspicious) 3 else 1
                        for (i in 0 until 5) {
                            val color = if (i < dots) primaryColor else (if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                            Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(color))
                        }
                    }
                }
                
                // Scan Depth
                HeroStatBlock(
                    title = "SCAN DEPTH",
                    value = "3/3 Layers",
                    icon = Icons.Rounded.Layers,
                    iconColor = textSecondary,
                    textColor = textPrimary,
                    labelColor = textSecondary,
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("Complete", fontSize = 9.sp, color = textSecondary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun HeroStatBlock(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    textColor: Color,
    labelColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            content()
        }
    }
}

@Composable
fun GaugeMeter(score: Int, primaryColor: Color, isDark: Boolean, modifier: Modifier = Modifier) {
    val progress = (score / 100f).coerceIn(0f, 1f)
    val trackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val needleColor = if (isDark) Color.White else Color(0xFF0F172A)
    
    Canvas(modifier = modifier) {
        val sweepAngle = 240f
        val startAngle = 150f
        
        // Background track
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        
        // Filled track
        drawArc(
            color = primaryColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle * progress,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        
        // Inner glowing track
        drawArc(
            brush = Brush.radialGradient(
                colors = listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent),
                center = center,
                radius = size.width / 2
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle * progress,
            useCenter = false,
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        
        // Needle
        val needleAngle = startAngle + (sweepAngle * progress)
        rotate(degrees = needleAngle, pivot = center) {
            drawLine(
                color = needleColor,
                start = center,
                end = Offset(center.x + (size.width / 2) - 12.dp.toPx(), center.y),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = needleColor,
                radius = 5.dp.toPx(),
                center = center
            )
        }
    }
}

@Composable
fun MessageAnalysisCard(analysis: MessageAnalysis, isDark: Boolean, primaryColor: Color) {
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val borderColor = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
    val context = LocalContext.current
    
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF6366F1).copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Message Analysis", fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 14.sp)
                }
                IconButton(onClick = {
                    val clip = ClipData.newPlainText("Scam Message", analysis.text)
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                    Toast.makeText(context, "Message copied", Toast.LENGTH_SHORT).show()
                }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy", tint = textSecondary, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Text highlighting links using a robust regex pattern
            val annotatedString = buildAnnotatedString {
                val urlRegex = "(https?://[\\w\\d.-]+(?:\\.[\\w\\d.-]+)+[^\\s]*|[\\w\\d.-]+\\.(?:com|in|org|net|co|xyz|info)[^\\s]*)".toRegex()
                var lastIndex = 0
                urlRegex.findAll(analysis.text).forEach { matchResult ->
                    val start = matchResult.range.first
                    val end = matchResult.range.last + 1
                    
                    if (start > lastIndex) {
                        withStyle(style = SpanStyle(color = textPrimary)) {
                            append(analysis.text.substring(lastIndex, start))
                        }
                    }
                    
                    withStyle(style = SpanStyle(
                        color = if (analysis.status.lowercase() == "danger" || analysis.status.lowercase() == "unsafe") Color(0xFFFF2B55) else Color(0xFF3B82F6),
                        fontWeight = FontWeight.Bold
                    )) {
                        append(analysis.text.substring(start, end))
                    }
                    
                    lastIndex = end
                }
                if (lastIndex < analysis.text.length) {
                    withStyle(style = SpanStyle(color = textPrimary)) {
                        append(analysis.text.substring(lastIndex))
                    }
                }
            }
            
            Text(
                text = annotatedString,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val isDanger = analysis.status.lowercase() == "danger" || analysis.status.lowercase() == "unsafe"
            val isSuspicious = analysis.status.lowercase() == "warning" || analysis.status.lowercase() == "suspicious"
            
            val chipLabel = if (isDanger) {
                "High Risk Content Detected"
            } else if (isSuspicious) {
                "Suspicious Content"
            } else {
                "Safe Content"
            }
            
            val chipBg = if (analysis.status.lowercase() == "safe") Color(0xFF10B981).copy(alpha = 0.1f) else primaryColor.copy(alpha = 0.1f)
            val chipColor = if (analysis.status.lowercase() == "safe") Color(0xFF10B981) else primaryColor
            
            Surface(color = chipBg, shape = RoundedCornerShape(12.dp)) {
                Text(chipLabel, color = chipColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
fun LinkScanCard(analysis: MessageAnalysis, isDark: Boolean, primaryColor: Color) {
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val borderColor = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
    val context = LocalContext.current
    
    val link = analysis.links.firstOrNull() ?: return
    val urlStatus = analysis.getUrlVerdict()
    
    val linkIsDanger = urlStatus.lowercase() == "danger" || urlStatus.lowercase() == "unsafe"
    val linkIsSafe = urlStatus.lowercase() == "safe"
    
    val parsedStatus = remember(analysis) {
        analysis.urlStatuses.firstOrNull { !it.startsWith("METADATA:") }?.let { parseUrlStatus(it, false) }
    }
    
    val webRepVal = parsedStatus?.webRiskVerdict ?: when (urlStatus.lowercase()) {
        "danger", "unsafe" -> "danger"
        "safe" -> "safe"
        "suspicious", "warning" -> "suspicious"
        else -> "unknown"
    }
    
    val phishVal = parsedStatus?.phishtankVerdict ?: when (urlStatus.lowercase()) {
        "danger", "unsafe" -> "danger"
        "safe" -> "safe"
        else -> "unknown"
    }
    
    val urlhausVal = parsedStatus?.urlhausVerdict ?: when (urlStatus.lowercase()) {
        "danger", "unsafe" -> "danger"
        "safe" -> "safe"
        else -> "unknown"
    }

    fun getWebRepInfo(value: String): Pair<String, String> {
        return when (value.lowercase()) {
            "safe", "clean" -> "Safe" to "safe"
            "danger", "unsafe", "malicious" -> "Unsafe" to "danger"
            "suspicious", "warning" -> "Suspicious" to "suspicious"
            else -> "Unknown" to "unknown"
        }
    }

    fun getPhishInfo(value: String): Pair<String, String> {
        return when (value.lowercase()) {
            "safe", "clean" -> "Safe" to "safe"
            "danger", "unsafe", "phish", "phish alert", "phish_alert" -> "Phish Alert" to "danger"
            "suspicious", "warning" -> "Suspicious" to "suspicious"
            else -> "Unverified" to "unknown"
        }
    }

    fun getUrlhausInfo(value: String): Pair<String, String> {
        return when (value.lowercase()) {
            "safe", "clean" -> "Clean" to "safe"
            "danger", "unsafe", "malware", "malicious" -> "Malicious" to "danger"
            "suspicious", "warning" -> "Suspicious" to "suspicious"
            else -> "Unverified" to "unknown"
        }
    }

    val webRepInfo = getWebRepInfo(webRepVal)
    val phishInfo = getPhishInfo(phishVal)
    val urlhausInfo = getUrlhausInfo(urlhausVal)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF8B5CF6).copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Link, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Link Scan", fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 14.sp)
                }
                Surface(color = primaryColor.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                    Text("${analysis.links.size} Link Found", color = primaryColor, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // URL Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .border(1.dp, if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = link,
                        color = if (linkIsDanger) Color(0xFFFF2B55) else if (linkIsSafe) Color(0xFF10B981) else Color(0xFF3B82F6),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        val clip = ClipData.newPlainText("Scam URL", link)
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        Toast.makeText(context, "URL copied", Toast.LENGTH_SHORT).show()
                    }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy", tint = textSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Icon(
                imageVector = Icons.Rounded.ArrowDownward,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Three Equal Mini Cards
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniScanCard(
                    title = "Web Reputation",
                    status = webRepInfo.first,
                    icon = Icons.Rounded.Language,
                    statusType = webRepInfo.second,
                    isDark = isDark
                )
                MiniScanCard(
                    title = "Phishing DB",
                    status = phishInfo.first,
                    icon = Icons.Rounded.Security,
                    statusType = phishInfo.second,
                    isDark = isDark
                )
                MiniScanCard(
                    title = "Malware DB",
                    status = urlhausInfo.first,
                    icon = Icons.Rounded.BugReport,
                    statusType = urlhausInfo.second,
                    isDark = isDark
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Icon(
                imageVector = Icons.Rounded.ArrowDownward,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Final Verdict Row
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Final Link Verdict", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                
                val (finalBadgeLabel, finalBadgeColor, finalBadgeIcon) = when {
                    linkIsDanger -> Triple("Dangerous", Color(0xFFFF2B55), Icons.Rounded.GppBad)
                    linkIsSafe -> Triple("Safe", Color(0xFF10B981), Icons.Rounded.VerifiedUser)
                    urlStatus.lowercase() == "unknown" -> Triple("Unverified", Color(0xFFEAB308), Icons.Rounded.Help)
                    else -> Triple("Suspicious", Color(0xFFF59E0B), Icons.Rounded.Warning)
                }
                
                Surface(color = finalBadgeColor, shape = RoundedCornerShape(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Icon(
                            finalBadgeIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(finalBadgeLabel, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.MiniScanCard(
    title: String,
    status: String,
    icon: ImageVector,
    statusType: String, // "safe", "suspicious", "danger", "unknown"
    isDark: Boolean
) {
    val cardBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val borderColor = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    
    val statusColor = when (statusType) {
        "danger" -> Color(0xFFFF2B55)
        "suspicious" -> Color(0xFFF59E0B)
        "unknown" -> Color(0xFFEAB308) // Yellow
        else -> Color(0xFF10B981)
    }
    val statusBg = statusColor.copy(alpha = 0.1f)
    
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.weight(1f).fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(statusBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(statusColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (statusType) {
                        "danger" -> Icons.Rounded.Close
                        "suspicious" -> Icons.Rounded.PriorityHigh
                        "unknown" -> Icons.Rounded.Help
                        else -> Icons.Rounded.Check
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@Composable
fun RecommendationCard(analysis: MessageAnalysis, isDark: Boolean, primaryColor: Color) {
    val isDanger = analysis.status.lowercase() == "danger" || analysis.status.lowercase() == "unsafe"
    val isSuspicious = analysis.status.lowercase() == "warning" || analysis.status.lowercase() == "suspicious"
    val isSafe = analysis.status.lowercase() == "safe"
    
    val bgGradient = if (isDanger) {
        listOf(if (isDark) Color(0xFF2A0F15) else Color(0xFFFFF1F2), if (isDark) Color(0xFF1E0B0F) else Color(0xFFFEF2F2))
    } else if (isSafe) {
        listOf(if (isDark) Color(0xFF064E3B).copy(alpha=0.3f) else Color(0xFFF0FDF4), if (isDark) Color(0xFF064E3B).copy(alpha=0.1f) else Color(0xFFF0FDF4))
    } else {
        listOf(if (isDark) Color(0xFF451A03).copy(alpha=0.3f) else Color(0xFFFFFBEB), if (isDark) Color(0xFF451A03).copy(alpha=0.1f) else Color(0xFFFFFBEB))
    }
    
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val borderColor = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
    val innerPhoneColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    
    val actions = if (isSafe) {
        listOf(
            Icons.Rounded.Check to "Verified as secure.",
            Icons.Rounded.Visibility to "Safe to interact.",
            Icons.Rounded.Share to "Practice safe habits."
        )
    } else if (isDanger) {
        listOf(
            Icons.Rounded.Close to "Do not click the link.",
            Icons.Rounded.Lock to "Never share OTP.",
            Icons.Rounded.Security to "Verify sender before acting."
        )
    } else {
        listOf(
            Icons.Rounded.Warning to "Verify sender identity.",
            Icons.Rounded.Lock to "Be cautious with links.",
            Icons.Rounded.Security to "Do not share sensitive data."
        )
    }
    
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(bgGradient))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).border(1.dp, primaryColor, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Security, contentDescription = null, tint = primaryColor, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recommendation", fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    actions.forEach { (icon, text) ->
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(bottom = 12.dp)) {
                            Icon(icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(16.dp).offset(y = 2.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text, fontSize = 12.sp, color = textPrimary, lineHeight = 18.sp)
                        }
                    }
                }
                
                // Stylized Phone Graphic (Reduced by 20% to keep it secondary)
                Box(modifier = Modifier.width(56.dp).height(80.dp).padding(start = 8.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Shadow base
                        drawOval(
                            color = primaryColor.copy(alpha = 0.15f),
                            topLeft = Offset(4f, size.height - 12f),
                            size = Size(size.width - 8f, 10f)
                        )
                        // Phone body
                        val phoneWidth = 35.dp.toPx()
                        val phoneHeight = 62.dp.toPx()
                        drawRoundRect(
                            color = primaryColor,
                            topLeft = Offset((size.width - phoneWidth)/2, size.height - phoneHeight - 6f),
                            size = Size(phoneWidth, phoneHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                        )
                        // Phone screen inner
                        drawRoundRect(
                            color = innerPhoneColor,
                            topLeft = Offset((size.width - phoneWidth)/2 + 2.5f.dp.toPx(), size.height - phoneHeight - 6f + 2.5f.dp.toPx()),
                            size = Size(phoneWidth - 5.dp.toPx(), phoneHeight - 5.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                        )
                    }
                    // Warning icon overlay
                    Icon(
                        if (isDanger || isSuspicious) Icons.Rounded.Warning else Icons.Rounded.CheckCircle, 
                        contentDescription = null, 
                        tint = primaryColor, 
                        modifier = Modifier.size(20.dp).offset(y = (-3).dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.ActionButton(
    text: String,
    icon: ImageVector,
    iconColor: Color,
    isDark: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val borderColor = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = textPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionButtons(
    onShareResult: () -> Unit,
    onDownloadPdf: () -> Unit,
    onAnalyzeAnother: () -> Unit,
    isDark: Boolean,
    isProcessing: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), 
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ActionButton(
            text = "Share Result",
            icon = Icons.Rounded.Share,
            iconColor = Color(0xFF8B5CF6),
            isDark = isDark,
            enabled = !isProcessing,
            onClick = onShareResult
        )
        ActionButton(
            text = "Download PDF",
            icon = Icons.Rounded.PictureAsPdf,
            iconColor = Color(0xFFFF2B55),
            isDark = isDark,
            enabled = !isProcessing,
            onClick = onDownloadPdf
        )
        ActionButton(
            text = "Scan Another",
            icon = Icons.Rounded.Camera,
            iconColor = Color(0xFF10B981),
            isDark = isDark,
            enabled = !isProcessing,
            onClick = onAnalyzeAnother
        )
    }
}

@Composable
fun FooterRow(analysis: MessageAnalysis, isDark: Boolean) {
    val textSecondary = Color(0xFF94A3B8)
    
    val dateStr = try {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(analysis.timestamp))
    } catch (e: Exception) {
        "12 Jul 2025, 09:41 AM"
    }
    
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CalendarToday, contentDescription = null, tint = textSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Scanned on $dateStr", fontSize = 10.sp, color = textSecondary)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Timer, contentDescription = null, tint = textSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Processing Time: 1.8s", fontSize = 10.sp, color = textSecondary)
            }
        }
    }
}

data class ParsedUrlStatus(
    val originalUrl: String,
    val normalizedUrl: String,
    val expandedUrl: String?,
    val webRiskVerdict: String,
    val phishtankVerdict: String = "unknown",
    val urlhausVerdict: String = "unknown",
    val riskLevel: String,
    val threatType: String?,
    val explanation: String
)

fun parseUrlStatus(statusStr: String, isHindi: Boolean): ParsedUrlStatus? {
    return try {
        val json = org.json.JSONObject(statusStr)
        val original = json.getString("original_url")
        val normalized = json.getString("normalized_url")
        val expanded = json.optString("expanded_url").takeIf { it.isNotEmpty() }
        val verdict = json.getString("web_risk_verdict")
        val phishtank = json.optString("phishtank_verdict", "unknown")
        val urlhaus = json.optString("urlhaus_verdict", "unknown")
        val risk = json.getString("risk_level")
        val threat = json.optString("threat_type").takeIf { it.isNotEmpty() }
        
        val explanation = ""
        ParsedUrlStatus(original, normalized, expanded, verdict, phishtank, urlhaus, risk, threat, explanation)
    } catch (e: Exception) {
        null
    }
}

data class RiskFactor(
    val name: String,
    val percentage: Int,
    val color: Color
)

fun calculateRiskFactors(text: String, score: Int, status: String, isDark: Boolean): List<RiskFactor> {
    val lowercaseText = text.lowercase()
    val scale = score / 100f
    
    var urgency = 10
    var impersonation = 10
    var financial = 10
    var credential = 10
    var socialEng = 10
    var malware = 10
    
    if (lowercaseText.contains("urgent") || lowercaseText.contains("now") || lowercaseText.contains("quick") || 
        lowercaseText.contains("immediate") || lowercaseText.contains("within") || lowercaseText.contains("block") || 
        lowercaseText.contains("suspend") || lowercaseText.contains("cancel") || lowercaseText.contains("expire") || 
        lowercaseText.contains("soon") || lowercaseText.contains("fast") || lowercaseText.contains("action") || 
        lowercaseText.contains("fine") || lowercaseText.contains("penalty")) {
        urgency += 50
    }
    
    if (lowercaseText.contains("bank") || lowercaseText.contains("sbi") || lowercaseText.contains("hdfc") || 
        lowercaseText.contains("icici") || lowercaseText.contains("amazon") || lowercaseText.contains("flipkart") || 
        lowercaseText.contains("kyc") || lowercaseText.contains("delivery") || lowercaseText.contains("post") || 
        lowercaseText.contains("courier") || lowercaseText.contains("netflix") || lowercaseText.contains("support")) {
        impersonation += 50
    }
    
    if (lowercaseText.contains("pay") || lowercaseText.contains("money") || lowercaseText.contains("rs") || 
        lowercaseText.contains("inr") || lowercaseText.contains("card") || lowercaseText.contains("wallet") || 
        lowercaseText.contains("upi") || lowercaseText.contains("payment") || lowercaseText.contains("transfer") || 
        lowercaseText.contains("prize") || lowercaseText.contains("reward") || lowercaseText.contains("gift") || 
        lowercaseText.contains("win") || lowercaseText.contains("cash")) {
        financial += 55
    }
    
    if (lowercaseText.contains("otp") || lowercaseText.contains("pin") || lowercaseText.contains("password") || 
        lowercaseText.contains("login") || lowercaseText.contains("verify") || lowercaseText.contains("verification") || 
        lowercaseText.contains("credential") || lowercaseText.contains("pan") || lowercaseText.contains("aadhar")) {
        credential += 60
    }
    
    if (lowercaseText.contains("congratulations") || lowercaseText.contains("job") || lowercaseText.contains("free") || 
        lowercaseText.contains("offer") || lowercaseText.contains("warning") || lowercaseText.contains("dear") || 
        lowercaseText.contains("alert") || lowercaseText.contains("secure") || lowercaseText.contains("update")) {
        socialEng += 45
    }
    
    if (lowercaseText.contains("http") || lowercaseText.contains("link") || lowercaseText.contains("bit.ly") || 
        lowercaseText.contains("tinyurl") || lowercaseText.contains("apk") || lowercaseText.contains("install") || 
        lowercaseText.contains("download") || lowercaseText.contains("app") || lowercaseText.contains("visit") || 
        lowercaseText.contains("tap")) {
        malware += 50
    }
    
    val finalUrgency = ((urgency * scale) + (score * 0.2f)).coerceIn(5f, 100f).toInt()
    val finalImpersonation = ((impersonation * scale) + (score * 0.15f)).coerceIn(5f, 100f).toInt()
    val finalFinancial = ((financial * scale) + (score * 0.15f)).coerceIn(5f, 100f).toInt()
    val finalCredential = ((credential * scale) + (score * 0.2f)).coerceIn(5f, 100f).toInt()
    val finalSocialEng = ((socialEng * scale) + (score * 0.1f)).coerceIn(5f, 100f).toInt()
    val finalMalware = ((malware * scale) + (score * 0.2f)).coerceIn(5f, 100f).toInt()
    
    val dangerColor = Color(0xFFEF4444)
    val warningColor = Color(0xFFF59E0B)
    val successColor = Color(0xFF10B981)
    
    fun getColorForScore(factorScore: Int): Color {
        return when {
            factorScore >= 70 -> dangerColor
            factorScore >= 40 -> warningColor
            else -> successColor
        }
    }
    
    return listOf(
        RiskFactor("Urgency", finalUrgency, getColorForScore(finalUrgency)),
        RiskFactor("Identity Theft", finalImpersonation, getColorForScore(finalImpersonation)),
        RiskFactor("Financial Scam", finalFinancial, getColorForScore(finalFinancial)),
        RiskFactor("Fake Offer", finalSocialEng, getColorForScore(finalSocialEng)),
        RiskFactor("Malicious Link", finalMalware, getColorForScore(finalMalware))
    ).sortedByDescending { it.percentage }
}

