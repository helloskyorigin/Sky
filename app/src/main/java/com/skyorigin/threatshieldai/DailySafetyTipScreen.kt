package com.skyorigin.threatshieldai

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySafetyTipScreen(
    viewModel: ScamLensViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"
    val context = LocalContext.current

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = MaterialTheme.colorScheme.background

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val sp = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    val currentIndex = remember(todayStr) {
        var startDateStr = sp.getString("safety_tip_start_date", null)
        if (startDateStr == null) {
            startDateStr = todayStr
            sp.edit().putString("safety_tip_start_date", todayStr).apply()
        }
        val daysDiff = getDaysDifference(startDateStr, todayStr)
        daysDiff % DailySafetyTipData.tipsExtended.size
    }

    val currentTip = DailySafetyTipData.tipsExtended[currentIndex]

    // Mark today's tip as opened
    LaunchedEffect(todayStr) {
        sp.edit().putString("last_opened_tip_date", todayStr).apply()
    }

    val title = if (isHindi) currentTip.titleHi else currentTip.titleEn
    val explanation = if (isHindi) currentTip.explanationHi else currentTip.explanationEn
    val whyItMatters = if (isHindi) currentTip.whyItMattersHi else currentTip.whyItMattersEn
    val staySafeAction = if (isHindi) currentTip.staySafeActionHi else currentTip.staySafeActionEn
    val icon = getIconForTip(currentTip.iconName)
    val themeColor = getColorForTip(currentTip.iconName)

    // Log screen view analytics
    LaunchedEffect(currentIndex) {
        AnalyticsManager.getInstance(context).logScreenView("daily_safety_tip_$currentIndex")
    }

    // Helper to format today's date elegantly
    val formattedDate = remember {
        try {
            val locale = if (isHindi) Locale("en", "IN") else Locale.getDefault()
            val sdf = SimpleDateFormat("EEEE, d MMMM", locale)
            sdf.format(Date())
        } catch (e: Exception) {
            if (isHindi) "Today's Tip" else "Today's Tip"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isHindi) "Daily Safety Tip" else "Daily Safety Tip",
                            style = TextStyle(
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                letterSpacing = (-0.3).sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = if (isHindi) "Back" else "Go back",
                            tint = textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor,
                    navigationIconContentColor = textPrimary,
                    titleContentColor = textPrimary
                )
            )
        },
        containerColor = bgColor,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Date Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = formattedDate,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary
                        )
                    )
                    Text(
                        text = if (isHindi) "Today's Safety Tip" else "Today's Safety Tip",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = PremiumColors.SubtitleGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Small indicator showing if this is today's current tip
                Box(
                    modifier = Modifier
                        .background(
                            color = PremiumColors.Safe.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .border(1.dp, PremiumColors.Safe.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isHindi) "Today's Tip" else "Today's Tip",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumColors.Safe
                        )
                    )
                }
            }

            // Central Animated Tip Card Container
            AnimatedContent(
                targetState = currentTip,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "tip_transition"
            ) { targetTip ->
                val cardIcon = getIconForTip(targetTip.iconName)
                val cardThemeColor = getColorForTip(targetTip.iconName)
                val cardTitle = if (isHindi) targetTip.titleHi else targetTip.titleEn
                val cardExplanation = if (isHindi) targetTip.explanationHi else targetTip.explanationEn
                val cardWhyItMatters = if (isHindi) targetTip.whyItMattersHi else targetTip.whyItMattersEn
                val cardStaySafeAction = if (isHindi) targetTip.staySafeActionHi else targetTip.staySafeActionEn

                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (isDark) Color(0xFF111827) else Color.White,
                    borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Large Safety Icon Container with Ambient Aura
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            cardThemeColor.copy(alpha = 0.18f),
                                            Color.Transparent
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        color = if (isDark) Color(0xFF1F2937) else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .border(
                                        1.dp,
                                        cardThemeColor.copy(alpha = 0.3f),
                                        RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cardIcon,
                                    contentDescription = null,
                                    tint = cardThemeColor,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tip Title
                        Text(
                            text = cardTitle,
                            style = TextStyle(
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                letterSpacing = (-0.4).sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Detailed Explanation Paragraph
                        Text(
                            text = cardExplanation,
                            style = TextStyle(
                                fontSize = 14.5.sp,
                                lineHeight = 21.sp,
                                color = textSecondary,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Why this matters section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isDark) Color(0xFF1A2238) else Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isDark) Color(0xFF2E3B5E) else Color(0xFFE2E8F0),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = PremiumColors.PrimaryAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "Why This Matters" else "Why This Matters",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isDark) Color.White else Color(0xFF1E293B),
                                        letterSpacing = 0.2.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = cardWhyItMatters,
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = textSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // "Stay Safe" Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isDark) Color(0xFF1B2E24) else Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isDark) Color(0xFF244D36) else Color(0xFFA7F3D0),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.VerifiedUser,
                                    contentDescription = null,
                                    tint = PremiumColors.Safe,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "Stay Safe Action" else "Stay Safe Action",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isDark) Color.White else Color(0xFF065F46),
                                        letterSpacing = 0.2.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = cardStaySafeAction,
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46)
                                )
                            )
                        }
                    }
                }
            }

            // Quick Actions & Sharing Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Share button spanning left
                Button(
                    onClick = {
                        val shareText = if (isHindi) {
                            "🛡️ *ThreatShield AI - Daily Safety Tip* 🛡️\n\n*${currentTip.titleHi}*\n\n💡 *What is it?*\n${currentTip.explanationHi}\n\n⚠️ *Why This Matters:*\n${currentTip.whyItMattersHi}\n\n✅ *Stay Safe Action:*\n${currentTip.staySafeActionHi}\n\nStay Safe, Stay Secure!"
                        } else {
                            "🛡️ *ThreatShield AI - Daily Safety Tip* 🛡️\n\n*${currentTip.titleEn}*\n\n💡 *What is it?*\n${currentTip.explanationEn}\n\n⚠️ *Why This Matters:*\n${currentTip.whyItMattersEn}\n\n✅ *Stay Safe:* \n${currentTip.staySafeActionEn}\n\nStay Safe, Stay Secure!"
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, if (isHindi) "Cybersecurity Safety Tip" else "Cybersecurity Safety Tip")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, if (isHindi) "Share Tip" else "Share Tip"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = PremiumRadius.button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PremiumColors.PrimaryAccent
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "Share Tip" else "Share Tip",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun getDaysDifference(startDateStr: String, endDateStr: String): Int {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = sdf.parse(startDateStr) ?: return 0
        val endDate = sdf.parse(endDateStr) ?: return 0
        
        val calStart = Calendar.getInstance().apply {
            time = startDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
            time = endDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val diffInMillis = calEnd.timeInMillis - calStart.timeInMillis
        val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
        if (days < 0) 0 else days
    } catch (e: Exception) {
        0
    }
}

fun getIconForTip(iconName: String): ImageVector {
    return when (iconName) {
        "otp" -> Icons.Rounded.Key
        "sms" -> Icons.Rounded.Sms
        "upi" -> Icons.Rounded.Payments
        "screen" -> Icons.Rounded.ScreenShare
        "web" -> Icons.Rounded.Language
        "qr" -> Icons.Rounded.QrCodeScanner
        "alert" -> Icons.Rounded.Warning
        "shield" -> Icons.Rounded.Shield
        "support" -> Icons.Rounded.ContactSupport
        "lock" -> Icons.Rounded.Lock
        "work" -> Icons.Rounded.Work
        "loan" -> Icons.Rounded.AccountBalance
        "trend" -> Icons.Rounded.TrendingUp
        "sim" -> Icons.Rounded.SimCard
        "wifi" -> Icons.Rounded.Wifi
        "app" -> Icons.Rounded.Shop
        "delivery" -> Icons.Rounded.LocalShipping
        "update" -> Icons.Rounded.SystemUpdate
        "fingerprint" -> Icons.Rounded.Fingerprint
        "gift" -> Icons.Rounded.CardGiftcard
        "chat" -> Icons.Rounded.Chat
        "card" -> Icons.Rounded.CreditCard
        else -> Icons.Rounded.Lightbulb
    }
}

fun getColorForTip(iconName: String): Color {
    return when (iconName) {
        "otp" -> Color(0xFFF59E0B) // Amber
        "sms" -> Color(0xFF3B82F6) // Blue
        "upi" -> Color(0xFF10B981) // Emerald Green
        "screen" -> Color(0xFFEF4444) // Red
        "web" -> Color(0xFF6366F1) // Indigo
        "qr" -> Color(0xFF8B5CF6) // Purple
        "alert" -> Color(0xFFF97316) // Orange
        "shield" -> Color(0xFF14B8A6) // Teal
        "support" -> Color(0xFFEC4899) // Pink
        "lock" -> Color(0xFF10B981) // Green
        "work" -> Color(0xFF0EA5E9) // Sky Blue
        "loan" -> Color(0xFFE11D48) // Crimson
        "trend" -> Color(0xFF22C55E) // Bright Green
        "sim" -> Color(0xFF8B5CF6) // Deep Purple
        "wifi" -> Color(0xFF3B82F6) // Blue
        "app" -> Color(0xFFF59E0B) // Amber
        "delivery" -> Color(0xFF10B981) // Emerald
        "update" -> Color(0xFF6366F1) // Indigo
        "fingerprint" -> Color(0xFFEC4899) // Pink
        "gift" -> Color(0xFFF43F5E) // Rose
        "chat" -> Color(0xFF06B6D4) // Cyan
        "card" -> Color(0xFF14B8A6) // Teal
        else -> Color(0xFF8B5CF6)
    }
}
