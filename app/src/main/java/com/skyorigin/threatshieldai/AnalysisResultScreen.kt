package com.skyorigin.threatshieldai

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
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
    val isUnableToDetermine = verdict.equals("Unable to Determine", ignoreCase = true)

    val riskColor = when {
        isDanger -> dangerRed
        isWarning || isSuspicious -> warningOrange
        isUnableToDetermine -> Color(0xFF94A3B8)
        else -> safeGreen
    }

    val riskTitle = when {
        isDanger -> "HIGH RISK"
        isWarning || isSuspicious -> "SUSPICIOUS"
        isUnableToDetermine -> "UNABLE TO DETERMINE"
        else -> "SAFE"
    }

    // Animation States
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateIn = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(500),
        label = "alpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (animateIn) 0.dp else 16.dp,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "offsetY"
    )

    var isGeneratingPdf by remember { mutableStateOf(false) }

    var showUnverifiedDialog by remember { mutableStateOf(false) }
    var urlToOpen by remember { mutableStateOf("") }
    var showDangerousBlockedDialog by remember { mutableStateOf(false) }
    var blockedUrlReason by remember { mutableStateOf("") }

    val handleUrlClick: (String, String) -> Unit = { url, riskLevel ->
        val uppercaseRisk = riskLevel.uppercase()
        if (uppercaseRisk == "DANGER" || uppercaseRisk == "MALICIOUS") {
            blockedUrlReason = url
            showDangerousBlockedDialog = true
        } else if (uppercaseRisk == "SAFE" || uppercaseRisk == "NO_KNOWN_THREAT") {
            try {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, if (isHindi) "URL खोलने में असमर्थ" else "Unable to open URL", Toast.LENGTH_SHORT).show()
            }
        } else {
            urlToOpen = url
            showUnverifiedDialog = true
        }
    }

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
                            fontSize = 16.sp,
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
                            tint = textPrimary,
                            modifier = Modifier.size(20.dp)
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
                            tint = textPrimary,
                            modifier = Modifier.size(20.dp)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // 1. COMPACT HERO RISK CARD
            val heroBgTint = riskColor.copy(alpha = 0.06f)
            val heroBorder = riskColor.copy(alpha = 0.15f)
            
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = heroBgTint),
                border = BorderStroke(1.dp, heroBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gauge (LEFT)
                    Box(
                        modifier = Modifier.size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(64.dp)) {
                            drawArc(
                                color = riskColor.copy(alpha = 0.15f),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        val scoreProgress = remember { Animatable(0f) }
                        LaunchedEffect(analysis.score) {
                            scoreProgress.animateTo(
                                targetValue = analysis.score / 100f,
                                animationSpec = tween(1200, easing = FastOutSlowInEasing)
                            )
                        }

                        Canvas(modifier = Modifier.size(64.dp)) {
                            drawArc(
                                color = riskColor,
                                startAngle = 135f,
                                sweepAngle = 270f * scoreProgress.value,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(y = 2.dp)) {
                            Text(
                                text = "${analysis.score}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = textPrimary,
                                lineHeight = 24.sp
                            )
                            Text(
                                text = "/100",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                modifier = Modifier.offset(y = (-2).dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = riskColor,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDanger) Icons.Rounded.PriorityHigh else if (isWarning || isSuspicious) Icons.Rounded.Warning else if (isUnableToDetermine) Icons.Rounded.HelpOutline else Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = riskTitle,
                                color = riskColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val verdictSentence = when {
                            isDanger -> if (isHindi) "उच्च जोखिम वाले घोटाले के संकेत मिले।" else "High-risk scam indicators detected."
                            isWarning || isSuspicious -> if (isHindi) "संदिग्ध गतिविधि के संकेत मिले।" else "Suspicious activity indicators detected."
                            isUnableToDetermine -> if (isHindi) "विश्लेषण के लिए पर्याप्त डेटा नहीं है।" else "Insufficient data to confidently classify."
                            else -> if (isHindi) "सुरक्षित संदेश पैटर्न पाया गया।" else "Safe message pattern detected."
                        }
                        
                        Text(
                            text = verdictSentence,
                            color = textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        val displayConfidence = if (analysis.confidence > 0) analysis.confidence else if (analysis.score > 0) analysis.score else 94

                        // Pills Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (analysis.scamType.isNotEmpty() && analysis.scamType != "Unknown" && analysis.scamType != "None") {
                                val shortenedType = if (analysis.scamType.length > 15) {
                                    analysis.scamType.take(12) + "..."
                                } else {
                                    analysis.scamType
                                }
                                Surface(
                                    color = riskColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(
                                        text = shortenedType,
                                        color = riskColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
                        
                        if (isHindi) {
                            Spacer(modifier = Modifier.height(4.dp))
                            val confidenceSupport = if (displayConfidence >= 75) {
                                "इस result पर AI की confidence अच्छी है।"
                            } else {
                                "यह analysis काफी भरोसेमंद लग रहा है।"
                            }
                            Text(
                                text = confidenceSupport,
                                color = primaryBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 2. AI SUMMARY CARD (Subtle blue tint, compact)
            val aiExplanation = sanitizeText(analysis.getLocalizedExplain15(isHindi))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = primaryBlue.copy(alpha = 0.04f)),
                border = BorderStroke(1.dp, primaryBlue.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(primaryBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SmartToy,
                            contentDescription = "AI Summary",
                            tint = primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Summary",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = aiExplanation,
                            fontSize = 11.sp,
                            color = textPrimary.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 3. LINK SECURITY CARD (Compact horizontal reputation block)
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
                var isLinkDetailsExpanded by remember { mutableStateOf(false) }
                val highestRiskUrl = parsedUrls.first()
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isLinkDetailsExpanded = !isLinkDetailsExpanded }
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        // Header row
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
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Link Security",
                                    color = textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            
                            val badgeColor = when (highestRiskUrl.riskLevel.uppercase()) {
                                "MALICIOUS", "DANGER" -> dangerRed
                                "NO_KNOWN_THREAT", "SAFE" -> safeGreen
                                else -> warningOrange
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Detected URL box
                        val isHighestDangerous = highestRiskUrl.riskLevel.uppercase() in listOf("MALICIOUS", "DANGER")
                        val isHighestSafe = highestRiskUrl.riskLevel.uppercase() in listOf("NO_KNOWN_THREAT", "SAFE")
                        
                        Surface(
                            color = if (isHighestDangerous) dangerRed.copy(alpha = 0.08f) 
                                    else if (isHighestSafe) cardBorder.copy(alpha = 0.2f)
                                    else warningOrange.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(10.dp),
                            border = if (isHighestDangerous) BorderStroke(1.dp, dangerRed.copy(alpha = 0.3f))
                                     else if (isHighestSafe) null
                                     else BorderStroke(1.dp, warningOrange.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    handleUrlClick(highestRiskUrl.originalUrl, highestRiskUrl.riskLevel)
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val displayUrl = try {
                                    val uri = java.net.URI(highestRiskUrl.originalUrl)
                                    val host = uri.host ?: highestRiskUrl.originalUrl
                                    val path = uri.path ?: ""
                                    if (path.length > 15) {
                                        host + path.take(12) + "..."
                                    } else {
                                        host + path
                                    }
                                } catch (e: Exception) {
                                    highestRiskUrl.originalUrl
                                }
                                
                                Text(
                                    text = displayUrl,
                                    fontSize = 12.sp,
                                    color = if (isHighestDangerous) dangerRed else textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = if (isHighestDangerous) Icons.Rounded.Block else Icons.Rounded.OpenInNew,
                                    contentDescription = if (isHighestDangerous) "Dangerous Link Blocked" else "Open Link",
                                    tint = if (isHighestDangerous) dangerRed else textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Overall status banner
                        val isHighestFailed = highestRiskUrl.webRiskStatus == "FAILED" || highestRiskUrl.webRiskStatus == "TIMEOUT"
                        
                        val overallLabel = when {
                            highestRiskUrl.riskLevel.uppercase() in listOf("MALICIOUS", "DANGER") -> "KNOWN THREAT"
                            highestRiskUrl.riskLevel.uppercase() in listOf("NO_KNOWN_THREAT", "SAFE") -> "NO KNOWN THREAT"
                            isHighestFailed -> "UNABLE TO VERIFY"
                            else -> "UNVERIFIED"
                        }
                        
                        val overallColor = when {
                            highestRiskUrl.riskLevel.uppercase() in listOf("MALICIOUS", "DANGER") -> dangerRed
                            highestRiskUrl.riskLevel.uppercase() in listOf("NO_KNOWN_THREAT", "SAFE") -> safeGreen
                            else -> warningOrange
                        }
                        
                        val overallSubtitle = when {
                            highestRiskUrl.riskLevel.uppercase() in listOf("MALICIOUS", "DANGER") -> if (isHindi) "यह Link खतरनाक है" else "This link is dangerous."
                            highestRiskUrl.riskLevel.uppercase() in listOf("NO_KNOWN_THREAT", "SAFE") -> if (isHindi) "कोई ज्ञात खतरा नहीं पाया गया" else "No known threat confirmed by reputation sources."
                            isHighestFailed -> if (isHindi) "API विफलता के कारण स्थिति अज्ञात है" else "Unable to verify link safety status due to API failure."
                            else -> if (isHindi) "स्थिति अज्ञात है - सावधानी बरतें" else "Unverified or unknown link safety status."
                        }

                        Surface(
                            color = overallColor.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = when(highestRiskUrl.riskLevel.uppercase()) {
                                        "MALICIOUS", "DANGER" -> Icons.Rounded.GppBad
                                        "NO_KNOWN_THREAT", "SAFE" -> Icons.Rounded.GppGood
                                        else -> Icons.Rounded.GppMaybe
                                    },
                                    contentDescription = null,
                                    tint = overallColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = overallLabel,
                                        color = overallColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = overallSubtitle,
                                        color = textPrimary.copy(alpha = 0.8f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Provider Horizontal Row (3 Symmetrical horizontal cells)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ProviderCell("Web Risk", highestRiskUrl.webRiskVerdict, highestRiskUrl.webRiskStatus, isHindi, Modifier.weight(1f))
                            ProviderCell("PhishTank", highestRiskUrl.phishtankVerdict, highestRiskUrl.phishtankStatus, isHindi, Modifier.weight(1f))
                            ProviderCell("URLhaus", highestRiskUrl.urlhausVerdict, highestRiskUrl.urlhausStatus, isHindi, Modifier.weight(1f))
                        }
                        
                        // Expandable Detail for Multiple URLs
                        if (parsedUrls.size > 1) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isLinkDetailsExpanded) {
                                        if (isHindi) "कम जानकारी देखें" else "Show Less"
                                    } else {
                                        if (isHindi) "अन्य सभी Links देखें (${parsedUrls.size})" else "Show All Links (${parsedUrls.size})"
                                    },
                                    color = primaryBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = if (isLinkDetailsExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            
                            if (isLinkDetailsExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = cardBorder.copy(alpha = 0.5f), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                parsedUrls.drop(1).forEachIndexed { index, urlStatus ->
                                    val otherOverallColor = when (urlStatus.riskLevel.uppercase()) {
                                        "MALICIOUS", "DANGER" -> dangerRed
                                        "NO_KNOWN_THREAT", "SAFE" -> safeGreen
                                        else -> warningOrange
                                    }
                                    val isOtherDangerous = urlStatus.riskLevel.uppercase() in listOf("MALICIOUS", "DANGER")
                                    val isOtherSafe = urlStatus.riskLevel.uppercase() in listOf("NO_KNOWN_THREAT", "SAFE")
                                    
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isOtherDangerous) dangerRed.copy(alpha = 0.05f)
                                                else if (isOtherSafe) Color.Transparent
                                                else warningOrange.copy(alpha = 0.03f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isOtherDangerous) dangerRed.copy(alpha = 0.2f) else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                handleUrlClick(urlStatus.originalUrl, urlStatus.riskLevel)
                                            }
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = urlStatus.originalUrl,
                                                fontSize = 11.sp,
                                                color = if (isOtherDangerous) dangerRed else textPrimary,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = otherOverallColor.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = urlStatus.riskLevel.uppercase(),
                                                    color = otherOverallColor,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            ProviderCell("Web Risk", urlStatus.webRiskVerdict, urlStatus.webRiskStatus, isHindi, Modifier.weight(1f))
                                            ProviderCell("PhishTank", urlStatus.phishtankVerdict, urlStatus.phishtankStatus, isHindi, Modifier.weight(1f))
                                            ProviderCell("URLhaus", urlStatus.urlhausVerdict, urlStatus.urlhausStatus, isHindi, Modifier.weight(1f))
                                        }
                                    }
                                    if (index < parsedUrls.size - 2) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        HorizontalDivider(color = cardBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. WHY THIS RESULT CARD (Transformed, human-readable reasons, max 3)
            val whyBgTint = when {
                isDanger -> dangerRed.copy(alpha = 0.04f)
                isWarning || isSuspicious -> warningOrange.copy(alpha = 0.04f)
                isUnableToDetermine -> Color(0xFF94A3B8).copy(alpha = 0.04f)
                else -> safeGreen.copy(alpha = 0.04f)
            }
            val whyBorder = when {
                isDanger -> dangerRed.copy(alpha = 0.12f)
                isWarning || isSuspicious -> warningOrange.copy(alpha = 0.12f)
                isUnableToDetermine -> Color(0xFF94A3B8).copy(alpha = 0.12f)
                else -> safeGreen.copy(alpha = 0.12f)
            }
            
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = whyBgTint),
                border = BorderStroke(1.dp, whyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = "Why",
                            tint = when {
                                isDanger -> dangerRed
                                isWarning || isSuspicious -> warningOrange
                                isUnableToDetermine -> Color(0xFF94A3B8)
                                else -> safeGreen
                            },
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Why This Result?",
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val rawReasons = (analysis.reasons + analysis.signals).distinct().filter { it.isNotBlank() }
                    val mappedReasons = rawReasons.map { mapSignalToReason(it, isHindi) }.distinct().filter { it.isNotBlank() }
                    
                    val finalReasons = if (mappedReasons.isEmpty()) {
                        if (isHindi) {
                            listOf("कोई संदिग्ध पैटर्न नहीं पाया गया", "कोई खतरनाक लिंक नहीं पाया गया")
                        } else {
                            listOf("No strong scam pattern detected", "No known malicious link detected")
                        }
                    } else {
                        mappedReasons.take(3)
                    }
                    
                    finalReasons.forEachIndexed { index, reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val reasonLower = reason.lowercase()
                            val icon = when {
                                reasonLower.contains("link") || reasonLower.contains("url") -> Icons.Rounded.Link
                                reasonLower.contains("fake") || reasonLower.contains("impersonat") || reasonLower.contains("सत्यापन") -> Icons.Rounded.Policy
                                reasonLower.contains("urgent") || reasonLower.contains("pressure") || reasonLower.contains("दबाव") || reasonLower.contains("तात्कालिकता") -> Icons.Rounded.WarningAmber
                                reasonLower.contains("private") || reasonLower.contains("info") || reasonLower.contains("credential") || reasonLower.contains("निजी") -> Icons.Rounded.PersonSearch
                                reasonLower.contains("bank") || reasonLower.contains("finance") || reasonLower.contains("लॉटरी") -> Icons.Rounded.AccountBalance
                                else -> Icons.Rounded.Info
                            }
                            val iconColor = when {
                                isDanger -> dangerRed
                                isWarning || isSuspicious -> warningOrange
                                isUnableToDetermine -> Color(0xFF94A3B8)
                                else -> safeGreen
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = reason,
                                color = textPrimary.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f),
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            
                            val severityText = when {
                                isDanger && index < 2 -> "High"
                                isDanger -> "Medium"
                                isWarning || isSuspicious -> "Medium"
                                isUnableToDetermine -> "Low"
                                else -> "Low"
                            }
                            val severityColor = when {
                                severityText == "High" -> dangerRed
                                severityText == "Medium" -> warningOrange
                                isUnableToDetermine -> Color(0xFF94A3B8)
                                else -> safeGreen
                            }
                            
                            Surface(
                                color = severityColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = severityText,
                                    color = severityColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. RECOMMENDED ACTIONS CARD (Maximum 2 dynamic actions, compact tint)
            val recBgTint = when {
                isDanger -> dangerRed.copy(alpha = 0.04f)
                isWarning || isSuspicious -> warningOrange.copy(alpha = 0.04f)
                isUnableToDetermine -> Color(0xFF94A3B8).copy(alpha = 0.04f)
                else -> safeGreen.copy(alpha = 0.04f)
            }
            val recBorder = when {
                isDanger -> dangerRed.copy(alpha = 0.12f)
                isWarning || isSuspicious -> warningOrange.copy(alpha = 0.12f)
                isUnableToDetermine -> Color(0xFF94A3B8).copy(alpha = 0.12f)
                else -> safeGreen.copy(alpha = 0.12f)
            }
            
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = recBgTint),
                border = BorderStroke(1.dp, recBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = when {
                                isDanger -> dangerRed
                                isWarning || isSuspicious -> warningOrange
                                isUnableToDetermine -> Color(0xFF94A3B8)
                                else -> safeGreen
                            },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Recommended Actions",
                                tint = Color.White,
                                modifier = Modifier.padding(3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Recommended Actions",
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val actionItems = analysis.getLocalAdvice(isHindi)
                    
                    actionItems.forEachIndexed { idx, action ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val actionIcon = when {
                                isDanger && idx == 0 -> Icons.Rounded.Block
                                isDanger && idx == 1 -> Icons.Rounded.LockReset
                                isWarning || isSuspicious -> Icons.Rounded.GppMaybe
                                isUnableToDetermine -> Icons.Rounded.HelpOutline
                                else -> Icons.Rounded.CheckCircleOutline
                            }
                            val actionColor = when {
                                isDanger -> dangerRed
                                isWarning || isSuspicious -> warningOrange
                                isUnableToDetermine -> Color(0xFF94A3B8)
                                else -> safeGreen
                            }
                            Icon(
                                imageVector = actionIcon,
                                contentDescription = null,
                                tint = actionColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = action,
                                color = textPrimary.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 6. BOTTOM ACTION BAR (Share, PDF, Scan Again - Horizontal symmetry, 48dp minimum touch target)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textPrimary),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = {
                        savePdfLauncher.launch("ThreatShield_Scan_Report_${System.currentTimeMillis()}.pdf")
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textPrimary),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PictureAsPdf,
                        contentDescription = "PDF",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PDF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Button(
                    onClick = onAnalyzeAnother,
                    modifier = Modifier.weight(1.2f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF1E293B),
                        contentColor = if (isDark) Color(0xFF0F172A) else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DocumentScanner,
                        contentDescription = "Scan",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Scan Again",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. ABOUT THIS RESULT NOTE (Premium info card)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color(0xFFF1F5F9)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp).offset(y = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "About this result",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Analyzes message content and checks links for known phishing or malicious threats. This is not a full website scan, and results may not always be accurate.",
                            fontSize = 11.sp,
                            color = textSecondary,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showUnverifiedDialog) {
            AlertDialog(
                onDismissRequest = { showUnverifiedDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.GppMaybe,
                            contentDescription = "Unverified Link",
                            tint = warningOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Unverified Link",
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                },
                text = {
                    Text(
                        text = "This link could not be fully verified by the available security sources.\n\nContinue only if you trust the source.",
                        color = textSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showUnverifiedDialog = false
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(urlToOpen))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable to open URL", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text("Open Anyway", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showUnverifiedDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = textSecondary)
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = cardBg,
                shape = RoundedCornerShape(20.dp)
            )
        }

        if (showDangerousBlockedDialog) {
            AlertDialog(
                onDismissRequest = { showDangerousBlockedDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.GppBad,
                            contentDescription = "Dangerous Link Blocked",
                            tint = dangerRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dangerous Link Blocked",
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Opening is disabled for safety",
                            fontWeight = FontWeight.Bold,
                            color = dangerRed,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This link is classified as a known security threat (Known threat). Opening this link is disabled to protect your device and data.",
                            color = textSecondary,
                            fontSize = 14.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDangerousBlockedDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = dangerRed)
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = cardBg,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun ProviderCell(name: String, verdict: String, status: String, isHindi: Boolean, modifier: Modifier = Modifier) {
    val isDark = LocalIsDark.current
    
    val displayLabel = when {
        status.uppercase() in listOf("SCAN_SKIPPED_PRIVACY", "BEHAVIORAL_SCAN_SKIPPED_PRIVACY") ||
        verdict.uppercase() in listOf("SCAN_SKIPPED_PRIVACY", "BEHAVIORAL_SCAN_SKIPPED_PRIVACY") -> "Skipped (Privacy)"
        
        verdict.uppercase() == "SUSPICIOUS_BEHAVIOR" -> "Suspicious"
        verdict.uppercase() == "NO_STRONG_SUSPICIOUS_BEHAVIOR" -> "No Suspicious"
        verdict.uppercase() == "INCONCLUSIVE" -> "Inconclusive"
        
        status.uppercase() == "FAILED" || status.uppercase() == "TIMEOUT" || status.uppercase() == "SCAN_FAILED" -> "Failed"
        verdict.uppercase() in listOf("MALICIOUS", "DANGER") -> if (name == "PhishTank") "Phishing" else "Threat"
        verdict.uppercase() in listOf("NO_KNOWN_THREAT", "SAFE") -> "Not Flagged"
        else -> "Unverified"
    }
    
    val displayColor = when {
        status.uppercase() in listOf("SCAN_SKIPPED_PRIVACY", "BEHAVIORAL_SCAN_SKIPPED_PRIVACY") ||
        verdict.uppercase() in listOf("SCAN_SKIPPED_PRIVACY", "BEHAVIORAL_SCAN_SKIPPED_PRIVACY") -> Color(0xFF3B82F6)
        
        verdict.uppercase() == "SUSPICIOUS_BEHAVIOR" -> Color(0xFFEF4444)
        verdict.uppercase() == "NO_STRONG_SUSPICIOUS_BEHAVIOR" -> Color(0xFF22C55E)
        verdict.uppercase() == "INCONCLUSIVE" -> Color(0xFF94A3B8)
        
        status.uppercase() == "FAILED" || status.uppercase() == "TIMEOUT" || status.uppercase() == "SCAN_FAILED" -> Color(0xFFF59E0B)
        verdict.uppercase() in listOf("MALICIOUS", "DANGER") -> Color(0xFFEF4444)
        verdict.uppercase() in listOf("NO_KNOWN_THREAT", "SAFE") -> Color(0xFF22C55E)
        else -> Color(0xFFF59E0B)
    }
    
    val cellBg = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
    val cellBorder = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    
    Column(
        modifier = modifier
            .background(cellBg, RoundedCornerShape(8.dp))
            .border(1.dp, cellBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = displayLabel,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = displayColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun mapSignalToReason(signal: String, isHindi: Boolean): String {
    val clean = signal.trim().lowercase()
    if (clean.isBlank()) return ""
    
    if (isHindi) {
        return when {
            clean.contains("phishing url") || clean.contains("phishing link") -> "इस message में एक phishing URL मिला है।"
            clean.contains("malware url") || clean.contains("malware link") -> "इस message में एक malware URL मिला है।"
            clean.contains("social engineering url") || clean.contains("social engineering link") -> "इस message में एक social engineering URL मिला है।"
            clean.contains("malicious url") || clean.contains("malicious link") -> "इस message में एक malicious URL मिला है।"
            clean.contains("suspicious link") || clean.contains("suspicious url") -> "इस message में एक suspicious link मिला है।"
            clean.contains("unverified external link") || clean.contains("unverified link") -> "Message में unverified external links शामिल हैं।"
            clean.contains("no malicious url") || clean.contains("no known malicious link") || clean.contains("no scam pattern") || clean.contains("no strong scam") || clean.contains("no malicious") -> "कोई malicious URL या scam indicators नहीं मिले।"
            clean.contains("urgent action") || clean.contains("urgent request") || clean.contains("urgently asks") || clean.contains("urgency") || clean.contains("urgent payment") || clean.contains("urgently") -> "आपको जल्दी action लेने के लिए दबाव डाला जा रहा है।"
            clean.contains("request for otp") || clean.contains("otp") || clean.contains("pin") || clean.contains("password") -> "OTP या PIN मांगा जा रहा है, जो highly risky हो सकता है।"
            clean.contains("bank impersonation") || clean.contains("non-official bank") -> "Bank impersonation और non-official domain की कोशिश पाई गई है।"
            clean.contains("government impersonation") -> "Government department का fake impersonation मिला है।"
            clean.contains("high-risk scam") -> "Message text में high-risk scam indicators मिले हैं।"
            clean.contains("significant threat") -> "Analysis engine द्वारा गंभीर खतरा (Significant threat) पाया गया है।"
            clean.contains("potential threat") -> "Potential threat signals पाए गए हैं।"
            clean.contains("unverified url requires") -> "Unverified URL मिला है, कृपया सावधानी बरतें।"
            clean.contains("url reputation") -> "URL reputation उपलब्ध नहीं है।"
            clean.contains("unsolicited promotional") -> "Unsolicited promotional sender मिला है।"
            clean.contains("weak or partial") -> "हल्के या आंशिक (Weak or partial) scam indicators मिले हैं।"
            clean.contains("scan incomplete") -> "Scan incomplete है, Security API services offline हैं।"
            clean.contains("unusual message") -> "असामान्य (Unusual) message pattern पाया गया।"
            // Default keyword fallbacks
            clean.contains("win") || clean.contains("won") || clean.contains("winner") || clean.contains("prize") || 
            clean.contains("$") || clean.contains("reward") || clean.contains("cash") || clean.contains("money") ||
            clean.contains("crore") || clean.contains("lakh") || clean.contains("lottery") || clean.contains("इनाम") -> {
                "इनाम या लॉटरी का अनपेक्षित दावा (Unexpected cash prize claim)"
            }
            clean.contains("click") || clean.contains("open") || clean.contains("visit") || clean.contains("link") || 
            clean.contains("url") || clean.contains("website") || clean.contains("http") -> {
                "संदिग्ध Link खोलने का अनुरोध"
            }
            clean.contains("urgent") || clean.contains("urgency") || clean.contains("now") || clean.contains("immediately") || 
            clean.contains("expire") || clean.contains("blocked") || clean.contains("suspend") || clean.contains("त्वरित") -> {
                "दबाव या तात्कालिकता (Urgency) पैदा करना"
            }
            clean.contains("verify") || clean.contains("verification") || clean.contains("kyc") || clean.contains("update") || 
            clean.contains("satyapan") || clean.contains("सत्यापन") -> {
                "खाता सत्यापन (Verification) का अनुरोध"
            }
            clean.contains("otp") || clean.contains("pin") || clean.contains("password") || clean.contains("credential") || 
            clean.contains("private") || clean.contains("personal") || clean.contains("info") || clean.contains("card") || 
            clean.contains("bank") || clean.contains("finance") || clean.contains("credit") || clean.contains("cvv") -> {
                "संवेदनशील या निजी जानकारी मांगना"
            }
            clean.contains("claim") || clean.contains("redeem") || clean.contains("offer") || clean.contains("gift") -> {
                "ऑफ़र या पुरस्कार का दावा करने के लिए प्रोत्साहन"
            }
            else -> sanitizeText(signal)
        }
    } else {
        val words = clean.split("\\s+".toRegex())
        if (words.size >= 4) {
            return sanitizeText(signal)
        }
        return when {
            clean.contains("win") || clean.contains("won") || clean.contains("winner") || clean.contains("prize") || 
            clean.contains("$") || clean.contains("reward") || clean.contains("cash") || clean.contains("money") ||
            clean.contains("crore") || clean.contains("lakh") || clean.contains("lottery") -> {
                "Unexpected cash prize claim"
            }
            clean.contains("click") || clean.contains("open") || clean.contains("visit") || clean.contains("link") || 
            clean.contains("url") || clean.contains("website") || clean.contains("http") -> {
                "Asks you to open a link"
            }
            clean.contains("urgent") || clean.contains("urgency") || clean.contains("now") || clean.contains("immediately") || 
            clean.contains("expire") || clean.contains("blocked") || clean.contains("suspend") -> {
                "Creates urgency or pressure"
            }
            clean.contains("verify") || clean.contains("verification") || clean.contains("kyc") || clean.contains("update") -> {
                "Requests account verification"
            }
            clean.contains("otp") || clean.contains("pin") || clean.contains("password") || clean.contains("credential") || 
            clean.contains("private") || clean.contains("personal") || clean.contains("info") || clean.contains("card") || 
            clean.contains("bank") || clean.contains("finance") || clean.contains("credit") || clean.contains("cvv") -> {
                "Requests sensitive or private information"
            }
            clean.contains("claim") || clean.contains("redeem") || clean.contains("offer") || clean.contains("gift") -> {
                "Encourages you to claim a reward"
            }
            else -> sanitizeText(signal)
        }
    }
}

fun sanitizeText(text: String): String {
    return text.replace("(?i)gpt-oss".toRegex(), "ThreatShield AI")
               .replace("(?i)gpt".toRegex(), "AI")
               .replace("(?i)groq".toRegex(), "AI Engine")
               .replace("(?i)llama".toRegex(), "AI Model")
}
