package com.skyorigin.threatshieldai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
import com.skyorigin.threatshieldai.ui.theme.PremiumColors
import com.skyorigin.threatshieldai.ui.theme.premiumShadow
import kotlinx.coroutines.delay

// Helper extension to calculate protection streak dynamically from history
fun List<MessageAnalysis>.calculateStreak(): Int {
    if (isEmpty()) return 0
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    val uniqueDates = map { 
        val date = java.util.Date(it.timestamp)
        sdf.format(date)
    }.toSet()
    
    val calendar = java.util.Calendar.getInstance()
    val todayStr = sdf.format(calendar.time)
    
    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
    val yesterdayStr = sdf.format(calendar.time)
    
    // If we didn't scan today or yesterday, streak is 0
    if (!uniqueDates.contains(todayStr) && !uniqueDates.contains(yesterdayStr)) {
        return 0
    }
    
    // Start scanning backwards
    var currentStreak = 0
    val checkCal = java.util.Calendar.getInstance()
    
    if (!uniqueDates.contains(todayStr)) {
        checkCal.add(java.util.Calendar.DAY_OF_MONTH, -1)
    }
    
    while (true) {
        val dateStr = sdf.format(checkCal.time)
        if (uniqueDates.contains(dateStr)) {
            currentStreak++
            checkCal.add(java.util.Calendar.DAY_OF_MONTH, -1)
        } else {
            break
        }
    }
    
    return currentStreak
}

// Format Last Scan Time helper
fun formatLastScanTime(timestamp: Long, isHindi: Boolean): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    if (diff < 0) return if (isHindi) "अभी-अभी" else "Just now"
    val minutes = diff / (60 * 1000)
    if (minutes < 1) return if (isHindi) "अभी-अभी" else "Just now"
    if (minutes < 60) return if (isHindi) "$minutes मिनट पहले" else "${minutes}m ago"
    val hours = minutes / 60
    if (hours < 24) return if (isHindi) "$hours घंटे पहले" else "${hours}h ago"
    val days = hours / 24
    if (days < 7) return if (isHindi) "$days दिन पहले" else "${days}d ago"
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}

@Composable
fun SecurityOverviewCard(
    viewModel: ScamLensViewModel,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = false
    
    // Core color theme configurations
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    
    val cardBgGradient = Brush.linearGradient(
        colors = if (isDark) {
            listOf(Color(0xFF030712), Color(0xFF0B132B), Color(0xFF030712)) // Matte rich black with electric blue depth
        } else {
            listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFEFF6FF)) // Clean crisp white/cool-blue glass
        }
    )
    
    val borderBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(
                Color(0xFF3B82F6).copy(alpha = 0.35f), // Electric blue highlight on top
                Color(0xFF1E3A8A).copy(alpha = 0.05f)  // Fading to transparent at bottom
            )
        } else {
            listOf(
                Color(0xFF3B82F6).copy(alpha = 0.2f),
                Color(0xFFDBEAFE).copy(alpha = 0.02f)
            )
        }
    )
    
    // Dynamic history state observations
    val history = viewModel.analysesHistory
    val totalScans = history.size
    val safeCount = history.count { it.status.lowercase() == "safe" }
    val scamCount = history.count { it.status.lowercase() == "danger" || it.status.lowercase() == "unsafe" }
    val suspiciousCount = history.count { it.status.lowercase() == "suspicious" }
    
    val protectionStatus = when {
        !viewModel.legalConsentAccepted || !viewModel.onboardingCompleted -> "INACTIVE"
        scamCount > 0 || suspiciousCount > 0 -> "ATTENTION"
        else -> "ACTIVE"
    }
    
    val lastScanTimeLabel = if (history.isNotEmpty()) {
        val latestScan = history.maxByOrNull { it.timestamp }
        if (latestScan != null) {
            formatLastScanTime(latestScan.timestamp, isHindi)
        } else {
            if (isHindi) "कोई डेटा नहीं" else "No analysis yet"
        }
    } else {
        if (isHindi) "कोई डेटा नहीं" else "No analysis yet"
    }

    // Slide up + Fade in entrance animations
    var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        cardVisible = true
    }

    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "cardAlpha"
    )
    val cardOffsetY by animateDpAsState(
        targetValue = if (cardVisible) 0.dp else 30.dp,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "cardOffsetY"
    )

    // Pulse/breathing animation for visual richness
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .offset(y = cardOffsetY)
            .alpha(cardAlpha)
            .premiumShadow(
                isDark = isDark,
                borderRadius = 24.dp,
                elevation = if (isDark) 16.dp else 4.dp
            )
            .border(
                width = 1.2.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBgGradient)
                .padding(20.dp)
        ) {
            // 1. Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Futuristic glowing shield container
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFF3B82F6).copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VerifiedUser,
                            contentDescription = "Shield Icon",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Column {
                        Text(
                            text = if (isHindi) "सुरक्षा अवलोकन" else "Security Overview",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                letterSpacing = (-0.3).sp
                            )
                        )
                        Text(
                            text = if (isHindi) "रीयल-टाइम सुरक्षा आँकड़े" else "Real-time threat diagnostics",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Premium Status Badge
                StatusBadge(
                    status = protectionStatus,
                    isHindi = isHindi
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Protection Status Visual/Icon (Center)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                val statusColor = when (protectionStatus) {
                    "ACTIVE" -> Color(0xFF10B981)
                    "ATTENTION" -> Color(0xFFF59E0B)
                    else -> Color(0xFF94A3B8)
                }
                val statusIcon = when (protectionStatus) {
                    "ACTIVE" -> Icons.Rounded.VerifiedUser
                    "ATTENTION" -> Icons.Rounded.ReportProblem
                    else -> Icons.Rounded.GppMaybe
                }
                val statusText = when (protectionStatus) {
                    "ACTIVE" -> if (isHindi) "सक्रिय" else "ACTIVE"
                    "ATTENTION" -> if (isHindi) "सावधान" else "ATTENTION"
                    else -> if (isHindi) "निष्क्रिय" else "INACTIVE"
                }
                val statusDesc = when (protectionStatus) {
                    "ACTIVE" -> if (isHindi) "सुरक्षा सक्रिय है और स्कैन सामान्य रूप से काम कर रहे हैं।" else "Protection is active and scans are working normally."
                    "ATTENTION" -> if (isHindi) "संदिग्ध या खतरनाक परिणाम मिले हैं। सिफारिशें देखें।" else "Suspicious or Dangerous results detected. Review recommendations."
                    else -> if (isHindi) "सेटअप अधूरा है या स्कैन सेवा अनुपलब्ध है।" else "Scanning service unavailable or setup incomplete."
                }

                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse_scale"
                )
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 0.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse_alpha"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulsing background ring
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .scale(pulseScale)
                                .alpha(pulseAlpha)
                                .background(statusColor.copy(alpha = 0.2f), CircleShape)
                                .border(1.5.dp, statusColor.copy(alpha = 0.4f), CircleShape)
                        )

                        // Inner solid badge container
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(statusColor.copy(alpha = 0.15f), Color.Transparent)
                                    ),
                                    shape = CircleShape
                                )
                                .border(1.5.dp, statusColor.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = "Status Icon",
                                tint = statusColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = if (isHindi) "सुरक्षा स्थिति" else "Protection Status",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = statusText,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = statusColor,
                            letterSpacing = 0.5.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = statusDesc,
                        style = TextStyle(
                            fontSize = 11.5.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 15.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Grid of Statistics
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GridStatCard(
                        icon = Icons.Rounded.QrCodeScanner,
                        label = if (isHindi) "कुल स्कैन" else "Total Scans",
                        value = totalScans,
                        tintColor = Color(0xFF3B82F6),
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    GridStatCard(
                        icon = Icons.Rounded.CheckCircle,
                        label = if (isHindi) "सुरक्षित संदेश" else "Safe Messages",
                        value = safeCount,
                        tintColor = Color(0xFF10B981),
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GridStatCard(
                        icon = Icons.Rounded.GppBad,
                        label = if (isHindi) "खतरे मिले" else "Threats Detected",
                        value = scamCount,
                        tintColor = Color(0xFFEF4444),
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    GridStatCard(
                        icon = Icons.Rounded.ReportProblem,
                        label = if (isHindi) "संदिग्ध" else "Suspicious",
                        value = suspiciousCount,
                        tintColor = Color(0xFFF59E0B),
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GridStatCard(
                        icon = Icons.Rounded.AccessTime,
                        label = if (isHindi) "अंतिम एनालिसिस" else "Last Analysis",
                        value = null,
                        rawValueString = lastScanTimeLabel,
                        tintColor = Color(0xFF8B5CF6),
                        isDark = isDark,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    isHindi: Boolean
) {
    val (text, color) = when (status) {
        "ACTIVE" -> {
            (if (isHindi) "सक्रिय" else "ACTIVE") to Color(0xFF10B981)
        }
        "ATTENTION" -> {
            (if (isHindi) "सावधान" else "ATTENTION") to Color(0xFFF59E0B)
        }
        else -> {
            (if (isHindi) "निष्क्रिय" else "INACTIVE") to Color(0xFF94A3B8)
        }
    }

    val animatedBadgeColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(500),
        label = "badgeColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(animatedBadgeColor.copy(alpha = 0.12f))
            .border(1.dp, animatedBadgeColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(animatedBadgeColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = animatedBadgeColor,
                letterSpacing = 0.2.sp
            )
        )
    }
}

@Composable
fun GridStatCard(
    icon: ImageVector,
    label: String,
    value: Int?, // if null, use rawValueString
    rawValueString: String = "",
    suffix: String = "",
    tintColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val displayValueString = if (value != null) {
        var animateStarted by remember { mutableStateOf(false) }
        LaunchedEffect(value) {
            animateStarted = true
        }
        val animValue by animateIntAsState(
            targetValue = if (animateStarted) value else 0,
            animationSpec = tween(1200, easing = EaseOutCubic),
            label = "stat_count"
        )
        "$animValue$suffix"
    } else {
        rawValueString
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isDark) Color(0xFF111827).copy(alpha = 0.5f)
                else Color(0xFFF8FAFC)
            )
            .border(
                width = 1.dp,
                color = if (isDark) Color(0xFF1F2937).copy(alpha = 0.2f)
                else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(tintColor.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, tintColor.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 10.5.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = displayValueString,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
            )
        }
    }
}
