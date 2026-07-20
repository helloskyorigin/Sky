package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ScamLensViewModel,
    onNavigateToAbout: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDoc: (String) -> Unit,
    onNavigateToFaq: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"
    val context = androidx.compose.ui.platform.LocalContext.current

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorderColor = MaterialTheme.colorScheme.outlineVariant

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }

    var showFeedbackDialogFromSettings by remember { mutableStateOf(false) }
    var feedbackDialogRating by remember { mutableStateOf(0) }
    var feedbackForceForm by remember { mutableStateOf(false) }
    var feedbackPreSelectedCategory by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.ic_official_logo),
                            contentDescription = "ThreatShield AI Logo",
                            modifier = Modifier.height(32.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Settings",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
        },
        containerColor = bgColor,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official Logo naturally rendered
                Image(
                    painter = painterResource(id = R.drawable.ic_official_logo),
                    contentDescription = "ThreatShield AI Logo",
                    modifier = Modifier
                        .height(48.dp)
                        .drawBehind {
                            if (isDark) {
                                val glowRadius = size.width * 0.7f
                                drawCircle(
                                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF0A84FF).copy(alpha = 0.20f),
                                            Color.Transparent
                                        ),
                                        center = center,
                                        radius = glowRadius
                                    ),
                                    radius = glowRadius,
                                    center = center
                                )
                            }
                        },
                    contentScale = ContentScale.Fit
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "ThreatShield AI",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (isHindi) "AI-आधारित स्कैम सुरक्षा" else "AI-Powered Scam Protection",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = textSecondary
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val appVersion = remember {
                    try {
                        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                        packageInfo.versionName ?: "v1.0.0"
                    } catch (e: Exception) {
                        "v1.0"
                    }
                }
                
                Text(
                    text = if (appVersion.startsWith("v")) "Version $appVersion" else "Version v$appVersion",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = textSecondary.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Section 1: Preferences
            Text(
                text = "Preferences",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumColors.PrimaryAccent
                )
            )

            com.example.ui.theme.PremiumCard {
                SettingsRow(
                    icon = Icons.Rounded.Language,
                    title = "Language",
                    subtitle = if (viewModel.currentLanguage == "hi") "हिन्दी (Hindi)" else "English",
                    onClick = { showLanguageDialog = true },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.Palette,
                    title = "Theme",
                    subtitle = when (viewModel.currentThemeMode) {
                        ThemeMode.SYSTEM -> "System default"
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                    },
                    onClick = { showThemeDialog = true },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
            }

            // Section 2: Account & Safety
            Text(
                text = "Account & Safety",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumColors.PrimaryAccent
                )
            )

            com.example.ui.theme.PremiumCard {
                SettingsRow(
                    icon = Icons.Rounded.History,
                    title = "Scan History",
                    subtitle = if (isHindi) "अपना पिछला Scan History देखें" else "View your past scan history",
                    onClick = onNavigateToHistory,
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.DeleteForever,
                    title = "Clear History",
                    subtitle = if (isHindi) "सभी Scan records मिटाएं" else "Delete all scan records",
                    onClick = {
                        showClearHistoryDialog = true
                    },
                    textColor = com.example.ui.theme.PremiumColors.Danger,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.Analytics,
                    title = if (isHindi) "Statistics" else "Statistics",
                    subtitle = if (isHindi) "सुरक्षा आँकड़े और मेट्रिक्स देखें" else "View security statistics & metrics",
                    onClick = {
                        showStatsDialog = true
                    },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
            }

            // Section: Feedback & Support
            Text(
                text = if (isHindi) "प्रतिक्रिया और रेटिंग" else "Feedback & Support",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumColors.PrimaryAccent
                )
            )

            com.example.ui.theme.PremiumCard {
                SettingsRow(
                    icon = Icons.Rounded.Star,
                    title = if (isHindi) "ऐप को रेट करें" else "Rate ThreatShield AI",
                    subtitle = if (isHindi) "हमें रेटिंग और रिव्यू दें" else "Rate and review our app",
                    onClick = {
                        feedbackDialogRating = 0
                        feedbackForceForm = false
                        feedbackPreSelectedCategory = null
                        showFeedbackDialogFromSettings = true
                    },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.BugReport,
                    title = if (isHindi) "बग रिपोर्ट करें" else "Report a Bug",
                    subtitle = if (isHindi) "कोई समस्या या एरर रिपोर्ट करें" else "Report an issue or error",
                    onClick = {
                        feedbackDialogRating = 3
                        feedbackForceForm = true
                        feedbackPreSelectedCategory = if (isHindi) "बग रिपोर्ट (Bug Report)" else "Bug Report"
                        showFeedbackDialogFromSettings = true
                    },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.Lightbulb,
                    title = if (isHindi) "फीचर का सुझाव दें" else "Request a Feature",
                    subtitle = if (isHindi) "नया सुझाव या सुधार बताएं" else "Suggest a new feature or improvement",
                    onClick = {
                        feedbackDialogRating = 4
                        feedbackForceForm = true
                        feedbackPreSelectedCategory = if (isHindi) "सुझाव (Feature Request)" else "Feature Request"
                        showFeedbackDialogFromSettings = true
                    },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.Feedback,
                    title = if (isHindi) "प्रतिक्रिया भेजें" else "Send Feedback",
                    subtitle = if (isHindi) "अपनी बहुमूल्य प्रतिक्रिया साझा करें" else "Share your valuable feedback",
                    onClick = {
                        feedbackDialogRating = 0
                        feedbackForceForm = false
                        feedbackPreSelectedCategory = null
                        showFeedbackDialogFromSettings = true
                    },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
            }

            // Section 3: Legal & About
            Text(
                text = "Legal & Info",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumColors.PrimaryAccent
                )
            )

            com.example.ui.theme.PremiumCard {
                SettingsRow(
                    icon = Icons.Rounded.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = if (isHindi) "Data security नीतियां" else "Data safety practices",
                    onClick = { onNavigateToDoc("privacy") },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.Gavel,
                    title = "Terms of Use",
                    subtitle = if (isHindi) "Terms and conditions" else "Terms and conditions",
                    onClick = { onNavigateToDoc("terms") },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.Security,
                    title = "Security Disclaimer",
                    subtitle = if (isHindi) "महत्वपूर्ण Security info" else "Important safety info",
                    onClick = { onNavigateToDoc("disclaimer") },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.SupportAgent,
                    title = "Contact Support",
                    subtitle = if (isHindi) "हमसे संपर्क करें" else "Get help and support",
                    onClick = { onNavigateToDoc("contact") },
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.HelpOutline,
                    title = if (isHindi) "Frequently Asked Questions (FAQ)" else "Frequently Asked Questions (FAQ)",
                    subtitle = if (isHindi) "अक्सर पूछे जाने वाले प्रश्न" else "Find answers to common questions",
                    onClick = onNavigateToFaq,
                    textColor = textPrimary,
                    subColor = textSecondary
                )
                HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    icon = Icons.Rounded.Info,
                    title = "About ThreatShield AI",
                    subtitle = if (isHindi) "ThreatShield AI के बारे में" else "About the app",
                    onClick = onNavigateToAbout,
                    textColor = textPrimary,
                    subColor = textSecondary
                )
            }

        }
    }

    if (showFeedbackDialogFromSettings) {
        FeedbackDialog(
            viewModel = viewModel,
            onDismiss = { showFeedbackDialogFromSettings = false },
            initialRating = feedbackDialogRating,
            forceFormStage = feedbackForceForm,
            preSelectedCategory = feedbackPreSelectedCategory
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = "Choose Language",
                    color = textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.currentLanguage = "en"
                                showLanguageDialog = false
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = viewModel.currentLanguage == "en",
                            onClick = {
                                viewModel.currentLanguage = "en"
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("English", color = textPrimary, fontSize = 16.sp)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.currentLanguage = "hi"
                                showLanguageDialog = false
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = viewModel.currentLanguage == "hi",
                            onClick = {
                                viewModel.currentLanguage = "hi"
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("हिन्दी (Hindi)", color = textPrimary, fontSize = 16.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = PremiumColors.PrimaryAccent)
                }
            },
            containerColor = cardBg
        )
    }

    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "Select Theme",
                    color = textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.currentThemeMode = mode
                                    showThemeDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = viewModel.currentThemeMode == mode,
                                onClick = {
                                    viewModel.currentThemeMode = mode
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (mode) {
                                    ThemeMode.SYSTEM -> "System default"
                                    ThemeMode.LIGHT -> "Light"
                                    ThemeMode.DARK -> "Dark"
                                },
                                color = textPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close", color = PremiumColors.PrimaryAccent)
                }
            },
            containerColor = cardBg
        )
    }

    // Clear History Confirmation Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Text(
                    text = if (isHindi) "इतिहास मिटाएं?" else "Clear History?",
                    color = textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isHindi) 
                        "क्या आप सचमुच सभी स्कैन इतिहास मिटाना चाहते हैं? इसे वापस नहीं लाया जा सकता।" 
                        else "Are you sure you want to clear all scan records? This action cannot be undone.",
                    color = textPrimary,
                    fontSize = 15.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearHistoryDialog = false
                        android.widget.Toast.makeText(
                            context,
                            if (isHindi) "इतिहास सफलतापूर्वक मिटा दिया गया" else "History cleared successfully",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(
                        text = if (isHindi) "मिटाएं" else "Clear",
                        color = PremiumColors.Danger,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(
                        text = if (isHindi) "रद्द करें" else "Cancel",
                        color = textSecondary
                    )
                }
            },
            containerColor = cardBg
        )
    }

    // Statistics Dialog
    if (showStatsDialog) {
        val history = viewModel.analysesHistory
        val totalScans = history.size
        val scamCount = history.count { it.status.lowercase() == "danger" || it.status.lowercase() == "unsafe" }
        val safeCount = history.count { it.status.lowercase() == "safe" }
        val suspiciousCount = history.count { it.status.lowercase() == "suspicious" }
        val protectionScore = if (totalScans == 0) 0 else ((safeCount.toFloat() / totalScans) * 100).toInt().coerceIn(0, 100)

        val dbFile = context.getDatabasePath("scam_shield_database")
        val dbSizeStr = if (dbFile != null && dbFile.exists()) {
            val sizeInBytes = dbFile.length()
            if (sizeInBytes < 1024) "$sizeInBytes B"
            else "${sizeInBytes / 1024} KB"
        } else {
            "48 KB"
        }

        val appVersion = remember {
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName ?: "1.0.0"
            } catch (e: Exception) {
                "1.0.0"
            }
        }

        val scoreTextVal = if (totalScans == 0) "--" else "$protectionScore%"

        val labelStatus = if (isHindi) "वर्तमान सुरक्षा स्थिति" else "Current Protection Status"
        val labelLastUpdated = if (isHindi) "अंतिम अपडेट" else "Last Updated"

        val status = ProtectionStatusHelper.calculateStatus(history)
        val statusTextVal = if (isHindi) {
            when (status) {
                ProtectionStatus.EXCELLENT -> "उत्कृष्ट"
                ProtectionStatus.GOOD -> "अच्छा"
                ProtectionStatus.NEEDS_ATTENTION -> "ध्यान दें"
                ProtectionStatus.HIGH_RISK -> "उच्च जोखिम"
                ProtectionStatus.UNKNOWN -> "अज्ञात"
            }
        } else {
            when (status) {
                ProtectionStatus.EXCELLENT -> "Excellent"
                ProtectionStatus.GOOD -> "Good"
                ProtectionStatus.NEEDS_ATTENTION -> "Attention"
                ProtectionStatus.HIGH_RISK -> "High Risk"
                ProtectionStatus.UNKNOWN -> "Unknown"
            }
        }

        val lastUpdatedVal = if (history.isNotEmpty()) {
            val lastScan = history.maxByOrNull { it.timestamp }
            if (lastScan != null) {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US)
                sdf.format(java.util.Date(lastScan.timestamp))
            } else {
                "N/A"
            }
        } else {
            "N/A"
        }

        val labelTotal = if (isHindi) "कुल स्कैन" else "Total Scan"
        val labelScam = if (isHindi) "स्कैम डिटेक्टेड" else "Scam"
        val labelSafe = if (isHindi) "सुरक्षित संदेश" else "Safe"
        val labelSuspicious = if (isHindi) "संदिग्ध संदेश" else "Suspicious"
        val labelScore = if (isHindi) "संरक्षण स्कोर" else "Protection Score"
        val labelAppVersion = if (isHindi) "ऐप संस्करण" else "App Version"
        val labelDbSize = if (isHindi) "डेटाबेस का आकार" else "Database Size"
        val labelHistoryCount = if (isHindi) "इतिहास रिकॉर्ड" else "History Records"

        AlertDialog(
            onDismissRequest = { showStatsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Analytics,
                        contentDescription = null,
                        tint = PremiumColors.PrimaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "सुरक्षा आँकड़े" else "Security Statistics",
                        color = textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "सुरक्षा इंजन प्रदर्शन मीट्रिक" else "Security Engine Performance Metrics",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumColors.PrimaryAccent
                        )
                    )

                    val statsList = listOf(
                        Triple(labelStatus, statusTextVal, status.icon),
                        Triple(labelScore, scoreTextVal, Icons.Rounded.Verified),
                        Triple(labelLastUpdated, lastUpdatedVal, Icons.Rounded.Schedule),
                        Triple(labelTotal, totalScans.toString(), Icons.Rounded.QrCodeScanner),
                        Triple(labelScam, scamCount.toString(), Icons.Rounded.GppBad),
                        Triple(labelSafe, safeCount.toString(), Icons.Rounded.CheckCircle),
                        Triple(labelSuspicious, suspiciousCount.toString(), Icons.Rounded.ReportProblem),
                        Triple(labelAppVersion, appVersion, Icons.Rounded.Info),
                        Triple(labelDbSize, dbSizeStr, Icons.Rounded.Storage),
                        Triple(labelHistoryCount, totalScans.toString(), Icons.Rounded.Dns)
                    )

                    statsList.forEach { (label, value, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val itemColor = if (label == labelStatus) {
                                status.color
                            } else if (label == labelScore) {
                                status.color
                            } else if (label == labelScam) {
                                if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)
                            } else if (label == labelSafe) {
                                Color(0xFF22C55E)
                            } else {
                                textPrimary
                            }
                            val iconColor = if (label == labelStatus) {
                                status.color
                            } else {
                                PremiumColors.PrimaryAccent
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = label,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textPrimary
                                    )
                                )
                            }
                            Text(
                                text = value,
                                style = TextStyle(
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = itemColor
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showStatsDialog = false }
                ) {
                    Text(
                        text = if (isHindi) "बंद करें" else "Close",
                        color = PremiumColors.PrimaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = cardBg
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color,
    subColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (textColor == PremiumColors.Danger) PremiumColors.Danger else PremiumColors.PrimaryAccent,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = subColor
                )
            )
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = "Go",
            tint = subColor,
            modifier = Modifier.size(20.dp)
        )
    }
}
