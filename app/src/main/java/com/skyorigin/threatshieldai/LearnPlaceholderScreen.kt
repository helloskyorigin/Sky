package com.skyorigin.threatshieldai

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnPlaceholderScreen(
    title: String,
    icon: ImageVector,
    color: Color,
    moduleType: String,
    viewModel: ScamLensViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"
    val context = LocalContext.current

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface

    // Define custom features list for each module to look highly professional
    val features = remember(moduleType, isHindi) {
        when (moduleType) {
            "scam_academy" -> if (isHindi) {
                listOf(
                    "🔐 फ़िशिंग, विशिंग और स्मिशिंग गाइड",
                    "💳 यूपीआई और वित्तीय सुरक्षा मॉड्यूल",
                    "📦 नकली पार्सल और सरकारी अधिकारी घोटालों की पहचान",
                    "⚠️ इंटरैक्टिव अभ्यास और उदाहरण"
                )
            } else {
                listOf(
                    "🔐 Phishing, vishing, and smishing guides",
                    "💳 UPI & financial scam protection modules",
                    "📦 Fake delivery & government impersonation tactics",
                    "⚠️ Guided walkthroughs & real-world scenario analysis"
                )
            }
            "daily_safety_tip" -> if (isHindi) {
                listOf(
                    "📱 दैनिक सुरक्षा सूचनाएं और अलर्ट",
                    "💻 डिजिटल स्वच्छता के सर्वोत्तम नियम",
                    "📶 सुरक्षित वाई-फाई और नेटवर्क उपयोग नियम",
                    "🔄 नियमित सुरक्षा चेकलिस्ट"
                )
            } else {
                listOf(
                    "📱 Daily customized security tips & action items",
                    "💻 Password hygiene & multi-factor authentication rules",
                    "📶 Safe Wi-Fi and secure browsing recommendations",
                    "🔄 Simple checklists to audit your personal devices"
                )
            }
            "quick_quiz" -> if (isHindi) {
                listOf(
                    "🏆 अपना सिक्योरिटी आईक्यू (Security IQ) जानें",
                    "🏅 बैज और सुरक्षा प्रमाण पत्र अर्जित करें",
                    "⚡ वास्तविक समय में प्रतिक्रिया और स्पष्टीकरण",
                    "👥 दोस्तों को चुनौती देने का विकल्प"
                )
            } else {
                listOf(
                    "🏆 Evaluate your current security hygiene index",
                    "🏅 Earn badges and climb the ThreatShield board",
                    "⚡ Instant detailed explanation for every question",
                    "👥 Challenge questions refreshed weekly"
                )
            }
            "common_scam_examples" -> if (isHindi) {
                listOf(
                    "📸 घोटालों के संदेशों के वास्तविक स्क्रीनशॉट",
                    "🚩 पहचानें प्रमुख चेतावनी संकेत (Red Flags)",
                    "🔍 नकली वेबसाइटों और संदिग्ध लिंक का विश्लेषण",
                    "🚀 नए उभरते घोटालों पर तत्काल रिपोर्ट"
                )
            } else {
                listOf(
                    "📸 Real screenshots of authentic reported scam attempts",
                    "🚩 Key highlighted Warning Signs (Red Flags)",
                    "🔍 Dissections of fake websites and URL tricks",
                    "🚀 Fresh reports on trending scams nationwide"
                )
            }
            "cyber_dictionary" -> if (isHindi) {
                listOf(
                    "📖 रैनसमवेयर, फ़िशिंग और स्पूफिंग को समझें",
                    "🔍 सरल हिंदी और अंग्रेजी परिभाषाएँ",
                    "💡 व्यावहारिक उदाहरणों के साथ शब्द अर्थ",
                    "⚡ त्वरित खोज (A to Z सर्च)"
                )
            } else {
                listOf(
                    "📖 Unpack complex concepts like Spoofing and Ransomware",
                    "🔍 Intuitive dual-language definitions and comparisons",
                    "💡 Real-life examples supporting technical jargon",
                    "⚡ A-to-Z instant search and save-to-favorites list"
                )
            }
            else -> listOf()
        }
    }

    LaunchedEffect(title) {
        AnalyticsManager.getInstance(context).logScreenView("learn_placeholder_$moduleType")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.3).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = if (isHindi) "पीछे जाएं" else "Go back",
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Glowing circular backdrop for the icon (Apple style)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    color.copy(alpha = if (isDark) 0.25f else 0.15f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(
                                color = if (isDark) Color(0xFF1F2937) else color.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .border(
                                width = 1.5.dp,
                                color = color.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(22.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Module Label & Title
                Text(
                    text = if (isHindi) "जल्द ही आ रहा है!" else "COMING SOON",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = color,
                        letterSpacing = 1.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isHindi) "यह मॉड्यूल तैयार हो रहा है" else "Under Active Development",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        letterSpacing = (-0.5).sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isHindi) {
                        "ThreatShield AI सुरक्षा टीम आपके अनुभव को बढ़ाने के लिए उपयोगी पाठ्य सामग्री और अभ्यास प्रश्न तैयार कर रही है।"
                    } else {
                        "Our cybersecurity experts are hand-crafting interactive modules to bolster your anti-scam capabilities."
                    },
                    style = TextStyle(
                        fontSize = 14.5.sp,
                        lineHeight = 20.sp,
                        color = textSecondary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // What's Coming Section (Premium M3 Card Layout)
                if (features.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF111827) else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = if (isHindi) "इस मॉड्यूल में क्या शामिल होगा:" else "What is being crafted for you:",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            )

                            features.forEach { feature ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = feature,
                                        style = TextStyle(
                                            fontSize = 13.5.sp,
                                            lineHeight = 18.sp,
                                            color = textSecondary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Got It Bottom CTA Button
            PremiumButton(
                onClick = onBack,
                text = if (isHindi) "समझ गया" else "Got It",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}
