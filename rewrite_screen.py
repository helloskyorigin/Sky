import sys

def rewrite():
    content = """package com.skyorigin.threatshieldai

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
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
    val context = LocalContext.current
    val isDark = LocalIsDark.current

    // Premium Color Tokens
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF7F9FC)
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val primaryBlue = Color(0xFF2563EB)
    val dangerRed = Color(0xFFEF4444)
    val warningOrange = Color(0xFFF59E0B)
    val safeGreen = Color(0xFF22C55E)

    val verdict = analysis.getTextVerdict()
    val isDanger = verdict.equals("Danger", ignoreCase = true)
    val isWarning = verdict.equals("Warning", ignoreCase = true)
    val isSuspicious = verdict.equals("Suspicious", ignoreCase = true)

    val riskColor = when {
        isDanger -> dangerRed
        isWarning || isSuspicious -> warningOrange
        else -> safeGreen
    }

    val riskTitle = when {
        isDanger -> "HIGH RISK"
        isWarning || isSuspicious -> "SUSPICIOUS"
        else -> "SAFE"
    }

    val shortExplanation = analysis.getLocalizedSummary(isHindi)

    // Animation States
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateIn = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (animateIn) 0.dp else 24.dp,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "offsetY"
    )

    var isGeneratingPdf by remember { mutableStateOf(false) }

    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    ReportExportHelper.writePdfReportToStream(context, analysis, isHindi, outputStream)
                }
                Toast.makeText(context, "PDF saved successfully.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to save PDF. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val formattedDate = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(analysis.timestamp))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Scan Result",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Scanned on $formattedDate",
                            fontSize = 11.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = textPrimary
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Rounded.VerifiedUser,
                        contentDescription = "Shield",
                        tint = textPrimary,
                        modifier = Modifier.padding(end = 8.dp).size(20.dp)
                    )
                    var showDropdown by remember { mutableStateOf(false) }
                    IconButton(onClick = { showDropdown = true }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Options",
                            tint = textPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false },
                        modifier = Modifier.background(cardBg).border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Download PDF", fontWeight = FontWeight.Medium) },
                            onClick = {
                                showDropdown = false
                                savePdfLauncher.launch("ThreatShield_Scan_Report_${System.currentTimeMillis()}.pdf")
                            },
                            leadingIcon = { Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, tint = primaryBlue) }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .alpha(alpha)
                .offset(y = offsetY),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. RISK HERO CARD
            val heroBgTint = riskColor.copy(alpha = 0.08f)
            val heroBorder = riskColor.copy(alpha = 0.2f)
            
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = heroBgTint),
                border = BorderStroke(1.dp, heroBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gauge
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(80.dp)) {
                            drawArc(
                                color = riskColor.copy(alpha = 0.2f),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        val scoreProgress = remember { Animatable(0f) }
                        LaunchedEffect(analysis.score) {
                            scoreProgress.animateTo(
                                targetValue = analysis.score / 100f,
                                animationSpec = tween(1500, easing = FastOutSlowInEasing)
                            )
                        }

                        Canvas(modifier = Modifier.size(80.dp)) {
                            drawArc(
                                color = riskColor,
                                startAngle = 135f,
                                sweepAngle = 270f * scoreProgress.value,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(y = 4.dp)) {
                            Text(
                                text = "${analysis.score}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = textPrimary,
                                lineHeight = 32.sp
                            )
                            Text(
                                text = "/100",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                modifier = Modifier.offset(y = (-4).dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when {
                                    isDanger -> Icons.Rounded.Report
                                    isWarning || isSuspicious -> Icons.Rounded.Warning
                                    else -> Icons.Rounded.VerifiedUser
                                },
                                contentDescription = null,
                                tint = riskColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = riskTitle,
                                color = riskColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = shortExplanation,
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val displayConfidence = if (analysis.confidence > 0) analysis.confidence else if (analysis.score > 0) analysis.score else 94
                        
                        // Pills
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (analysis.scamType.isNotEmpty() && analysis.scamType != "Unknown") {
                                Surface(
                                    color = riskColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(
                                        text = "Threat Type: ${analysis.scamType}",
                                        color = riskColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Surface(
                                color = primaryBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "AI Confidence: $displayConfidence%",
                                    color = primaryBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. AI SUMMARY CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = primaryBlue.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, primaryBlue.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SmartToy,
                        contentDescription = "AI Summary",
                        tint = primaryBlue,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Summary",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = analysis.getLocalizedExplain15(isHindi),
                            fontSize = 13.sp,
                            color = textPrimary.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = primaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. LINK SECURITY CARD
            val parsedUrls = analysis.urlStatuses
                .filter { !it.startsWith("METADATA:") }
                .mapNotNull { parseUrlStatus(it, isHindi) }
                .sortedBy { 
                    when (it.riskLevel.uppercase()) {
                        "MALICIOUS", "DANGER" -> 0
                        "UNVERIFIED", "UNKNOWN", "FAILED", "TIMEOUT" -> 1
                        else -> 2
                    }
                }

            if (parsedUrls.isNotEmpty()) {
                val highestRiskUrl = parsedUrls.first()
                val hasMultiple = parsedUrls.size > 1
                
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Link,
                                    contentDescription = "Link Security",
                                    tint = primaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Link Security",
                                    color = textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            
                            Surface(
                                color = dangerRed.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (parsedUrls.size == 1) "1 Link Detected" else "${parsedUrls.size} Links Detected",
                                    color = dangerRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Detected Link",
                            fontSize = 12.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        
                        val domain = try {
                            java.net.URI(highestRiskUrl.originalUrl).host ?: highestRiskUrl.originalUrl
                        } catch (e: Exception) { highestRiskUrl.originalUrl }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = domain,
                                fontSize = 14.sp,
                                color = textPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Rounded.OpenInNew,
                                contentDescription = "Open",
                                tint = textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val overallLabel = when (highestRiskUrl.riskLevel.uppercase()) {
                            "MALICIOUS", "DANGER" -> "KNOWN THREAT DETECTED"
                            "NO_KNOWN_THREAT", "SAFE" -> "NO KNOWN THREAT"
                            else -> "UNVERIFIED"
                        }
                        
                        val overallColor = when (highestRiskUrl.riskLevel.uppercase()) {
                            "MALICIOUS", "DANGER" -> dangerRed
                            "NO_KNOWN_THREAT", "SAFE" -> safeGreen
                            else -> warningOrange
                        }
                        
                        val subtitle = when (highestRiskUrl.riskLevel.uppercase()) {
                            "MALICIOUS", "DANGER" -> if (isHindi) "यह Link खतरनाक है" else "This link is dangerous"
                            "NO_KNOWN_THREAT", "SAFE" -> if (isHindi) "कोई ज्ञात खतरा नहीं" else "No known threat"
                            else -> if (isHindi) "स्थिति अज्ञात है" else "Status unknown"
                        }

                        // Banner for Overall Link Status
                        Surface(
                            color = overallColor.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = when(highestRiskUrl.riskLevel.uppercase()) {
                                        "MALICIOUS", "DANGER" -> Icons.Rounded.SecurityUpdateWarning
                                        "NO_KNOWN_THREAT", "SAFE" -> Icons.Rounded.VerifiedUser
                                        else -> Icons.Rounded.HelpOutline
                                    },
                                    contentDescription = null,
                                    tint = overallColor,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = overallLabel,
                                        color = overallColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = subtitle,
                                        color = textPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Providers
                        val sources = listOf(
                            Triple("Google Web Risk", Icons.Rounded.GMobiledata, highestRiskUrl.webRiskVerdict),
                            Triple("PhishTank", Icons.Rounded.Phishing, highestRiskUrl.phishtankVerdict),
                            Triple("URLhaus", Icons.Rounded.Shield, highestRiskUrl.urlhausVerdict)
                        )
                        
                        sources.forEachIndexed { i, (name, icon, verdict) ->
                            val vLabel = when (verdict.uppercase()) {
                                "MALICIOUS", "DANGER" -> if (name == "PhishTank") "Phishing Detected" else "Threat Detected"
                                "NO_KNOWN_THREAT", "SAFE" -> "No Known Threat"
                                else -> "Unverified"
                            }
                            val vColor = when (verdict.uppercase()) {
                                "MALICIOUS", "DANGER" -> dangerRed
                                "NO_KNOWN_THREAT", "SAFE" -> safeGreen
                                else -> warningOrange
                            }
                            val vIcon = when (verdict.uppercase()) {
                                "MALICIOUS", "DANGER" -> Icons.Rounded.Error
                                "NO_KNOWN_THREAT", "SAFE" -> Icons.Rounded.CheckCircle
                                else -> null
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = textPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = name, fontSize = 13.sp, color = textPrimary, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (vIcon != null) {
                                        Icon(
                                            imageVector = vIcon,
                                            contentDescription = null,
                                            tint = vColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(text = vLabel, fontSize = 13.sp, color = vColor, fontWeight = FontWeight.Medium)
                                }
                            }
                            if (i < sources.size - 1) {
                                HorizontalDivider(color = cardBorder.copy(alpha = 0.5f), thickness = 1.dp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = cardBorder.copy(alpha = 0.5f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Checked on: $formattedDate",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                            Text(
                                text = "1 of ${parsedUrls.size}",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 4. WHY THIS RESULT? CARD
            val whyBgTint = warningOrange.copy(alpha = 0.05f)
            val whyBorder = warningOrange.copy(alpha = 0.15f)
            
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = whyBgTint),
                border = BorderStroke(1.dp, whyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = "Why",
                            tint = warningOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Why This Result?",
                            color = textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val detectedReasons = (analysis.reasons + analysis.signals).distinct().filter { it.isNotBlank() }
                    
                    if (detectedReasons.isNotEmpty()) {
                        detectedReasons.take(4).forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Link, // A default icon
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = reason,
                                    color = textPrimary.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f),
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isDanger) "High" else if (isWarning || isSuspicious) "Medium" else "Low",
                                    color = riskColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. RECOMMENDED ACTIONS CARD
            val recBgTint = safeGreen.copy(alpha = 0.05f)
            val recBorder = safeGreen.copy(alpha = 0.15f)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = recBgTint),
                border = BorderStroke(1.dp, recBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Recommended Actions",
                            tint = safeGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recommended Actions",
                            color = textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val actions = analysis.getLocalAdvice(isHindi)
                    
                    actions.take(3).forEachIndexed { index, action ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            val actionIcon = when(index) {
                                0 -> Icons.Rounded.Block
                                1 -> Icons.Rounded.DeleteOutline
                                else -> Icons.Rounded.VerifiedUser
                            }
                            val actionColor = when(index) {
                                0 -> dangerRed
                                1 -> safeGreen
                                else -> primaryBlue
                            }
                            Icon(
                                imageVector = actionIcon,
                                contentDescription = null,
                                tint = actionColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = action,
                                color = textPrimary.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. BOTTOM ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (!isGeneratingPdf) {
                            isGeneratingPdf = true
                            Toast.makeText(context, "Preparing PDF report...", Toast.LENGTH_SHORT).show()
                            ReportExportHelper.shareReportAsPdf(context, analysis, isHindi)
                            isGeneratingPdf = false
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share Result",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = {
                        savePdfLauncher.launch("ThreatShield_Scan_Report_${System.currentTimeMillis()}.pdf")
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Download PDF",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onAnalyzeAnother,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.DocumentScanner,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scan Again",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
"""
    with open('app/src/main/java/com/skyorigin/threatshieldai/AnalysisResultScreen.kt', 'w') as f:
        f.write(content)

rewrite()
