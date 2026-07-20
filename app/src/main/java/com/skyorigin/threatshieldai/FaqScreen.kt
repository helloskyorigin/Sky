package com.skyorigin.threatshieldai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
import com.skyorigin.threatshieldai.ui.theme.PremiumColors
import com.skyorigin.threatshieldai.ui.theme.premiumShadow

data class FaqItem(
    val id: Int,
    val questionEn: String,
    val questionHi: String,
    val answerEn: String,
    val answerHi: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(
    viewModel: ScamLensViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = false

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorderColor = MaterialTheme.colorScheme.outlineVariant

    var searchQuery by remember { mutableStateOf("") }
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    val faqList = remember {
        listOf(
            FaqItem(
                id = 1,
                questionEn = "What is ThreatShield AI?",
                questionHi = "ThreatShield AI क्या है?",
                answerEn = "ThreatShield AI helps detect suspicious scam messages using AI and security analysis. It provides a risk assessment but should not replace your own judgment.",
                answerHi = "ThreatShield AI, AI और security analysis का उपयोग करके suspicious scam messages को detect करने में मदद करता है। यह risk assessment प्रदान करता है, लेकिन इसे आपके अपने निर्णय का विकल्प नहीं माना जाना चाहिए।"
            ),
            FaqItem(
                id = 2,
                questionEn = "Does ThreatShield AI upload my messages?",
                questionHi = "क्या ThreatShield AI मेरे messages को upload करता है?",
                answerEn = "No. Your message is processed according to the app's privacy policy. We do not sell your personal data.",
                answerHi = "नहीं। आपके messages ऐप की privacy policy के अनुसार process होते हैं। हम आपका personal data नहीं बेचते हैं।"
            ),
            FaqItem(
                id = 3,
                questionEn = "Is every analysis 100% accurate?",
                questionHi = "क्या हर analysis 100% सटीक होता है?",
                answerEn = "No. AI predictions are highly useful but not perfect. Always verify financial requests, OTPs, payment links, and unknown contacts yourself.",
                answerHi = "नहीं। AI predictions बहुत उपयोगी होती हैं, लेकिन वे 100% perfect नहीं होतीं। financial requests, OTPs, payment links, और unknown contacts को हमेशा स्वयं verify करें।"
            ),
            FaqItem(
                id = 4,
                questionEn = "What should do if a message is marked as Scam?",
                questionHi = "Message को scam मार्क करने पर मुझे क्या करना चाहिए?",
                answerEn = "• Avoid clicking links.\n• Do not share OTPs.\n• Do not send money.\n• Block the sender if appropriate.\n• Report the scam if necessary.",
                answerHi = "• Links पर click करने से बचें।\n• OTPs साझा न करें।\n• पैसे न भेजें।\n• यदि आवश्यक हो तो sender को block करें।\n• यदि ज़रूरी हो, तो scam की report करें।"
            ),
            FaqItem(
                id = 5,
                questionEn = "If the app says Safe, is it guaranteed?",
                questionHi = "यदि ऐप 'Safe' कहता है, तो क्या इसकी गारंटी है?",
                answerEn = "No. Always stay cautious. AI assists your decision but cannot guarantee absolute safety.",
                answerHi = "नहीं। हमेशा सतर्क रहें। AI आपके निर्णय में मदद करता है, लेकिन पूर्ण safety की गारंटी नहीं दे सकता।"
            ),
            FaqItem(
                id = 6,
                questionEn = "Where is my analysis history stored?",
                questionHi = "मेरा analysis history कहां store होता है?",
                answerEn = "Your analysis history is stored locally on your device unless future cloud sync features are enabled.",
                answerHi = "आपकी analysis history आपके device पर locally store होती है।"
            ),
            FaqItem(
                id = 7,
                questionEn = "How do I clear analysis history?",
                questionHi = "मैं analysis history को कैसे clear करूँ?",
                answerEn = "Go to Settings → Clear History and confirm deletion.",
                answerHi = "Settings → Clear History पर जाएं और delete करने की पुष्टि करें।"
            ),
            FaqItem(
                id = 8,
                questionEn = "What is the Daily Challenge?",
                questionHi = "Daily Challenge क्या है?",
                answerEn = "Each day at 9:00 AM, ThreatShield AI can remind you with one scam awareness challenge to improve your cybersecurity knowledge.",
                answerHi = "हर दिन सुबह 9:00 बजे, ThreatShield AI आपको cybersecurity knowledge improve करने के लिए एक scam awareness challenge के साथ remind कर सकता है।"
            ),
            FaqItem(
                id = 9,
                questionEn = "How do I report a bug or send feedback?",
                questionHi = "मैं bug की report कैसे करूँ या feedback कैसे भेजूँ?",
                answerEn = "Go to Settings → Feedback & Support. You can report bugs, suggest features, or send feedback directly from the app.",
                answerHi = "Settings → Feedback & Support पर जाएं। आप सीधे app से bugs report कर सकते हैं, features suggest कर सकते हैं या feedback भेज सकते हैं।"
            ),
            FaqItem(
                id = 10,
                questionEn = "What features are coming soon?",
                questionHi = "कौन सी features जल्द ही आ रही हैं?",
                answerEn = "Upcoming features include:\n• URL Analyzer\n• Image Scam Detection\n• Call Protection\n• Advanced AI Detection\n• Premium Security Features",
                answerHi = "आगामी features में शामिल हैं:\n• URL Analyzer\n• Image Scam Detection\n• Call Protection\n• Advanced AI Detection\n• Premium Security Features"
            )
        )
    }

    // Filter list based on search query
    val filteredFaqList = remember(searchQuery, isHindi) {
        if (searchQuery.trim().isEmpty()) {
            faqList
        } else {
            faqList.filter { item ->
                if (isHindi) {
                    item.questionHi.contains(searchQuery, ignoreCase = true) ||
                    item.answerHi.contains(searchQuery, ignoreCase = true)
                } else {
                    item.questionEn.contains(searchQuery, ignoreCase = true) ||
                    item.answerEn.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "अक्सर पूछे जाने वाले प्रश्न" else "Frequently Asked Questions",
                        style = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    )
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
                .padding(horizontal = 16.dp)
        ) {
            // Subtitle
            Text(
                text = if (isHindi) "ThreatShield AI के बारे में सामान्य प्रश्नों के उत्तर खोजें।" else "Find answers to common questions about ThreatShield AI.",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = textSecondary,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.padding(bottom = 16.dp, top = 4.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (isHindi) "प्रश्न खोजें..." else "Search questions...",
                        color = textSecondary.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search icon",
                        tint = textSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Clear search",
                                tint = textSecondary
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremiumColors.PrimaryAccent,
                    unfocusedBorderColor = cardBorderColor,
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary
                ),
                singleLine = true
            )

            // FAQ Items List
            if (filteredFaqList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HelpOutline,
                            contentDescription = null,
                            tint = textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isHindi) "कोई परिणाम नहीं मिला" else "No results found",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHindi) "कृपया अन्य शब्दों के साथ प्रयास करें।" else "Try checking other search keywords.",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = textSecondary
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(filteredFaqList, key = { _, item -> item.id }) { index, item ->
                        val isExpanded = expandedIndex == index

                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumShadow(isDark, borderRadius = 16.dp, elevation = if (isDark) 0.dp else 2.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    expandedIndex = if (isExpanded) null else index
                                },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, cardBorderColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isHindi) item.questionHi else item.questionEn,
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isExpanded) PremiumColors.PrimaryAccent else textPrimary,
                                            lineHeight = 20.sp
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                                        tint = if (isExpanded) PremiumColors.PrimaryAccent else textSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = fadeIn(animationSpec = tween(200)) + expandVertically(animationSpec = tween(250)),
                                    exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(animationSpec = tween(200))
                                ) {
                                    Column {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = if (isHindi) item.answerHi else item.answerEn,
                                            style = TextStyle(
                                                fontSize = 13.5.sp,
                                                color = textSecondary,
                                                lineHeight = 22.sp,
                                                fontWeight = FontWeight.Normal
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
