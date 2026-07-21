package com.skyorigin.threatshieldai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickQuizScreen(
    viewModel: ScamLensViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"
    val context = LocalContext.current

    // Active gameplay variables
    var activeChallengeQuestion by remember { mutableStateOf<QuizQuestion?>(null) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }

    // Navigation and sub-state (false = show list of 50 challenges, true = play single challenge)
    var playingMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "त्वरित चुनौती" else "Quick Challenge",
                        style = PremiumTypography.SectionTitle.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else PremiumColors.TextDark
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (playingMode) {
                                playingMode = false
                                activeChallengeQuestion = null
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.semantics { testTag = "quiz_back_button" }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = if (isHindi) "पीछे जाएं" else "Back",
                            tint = if (isDark) Color.White else PremiumColors.TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF0F172A) else Color.White
                )
            )
        },
        containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Crossfade(
                targetState = playingMode,
                animationSpec = tween(500),
                label = "challenge_state_crossfade"
            ) { isPlaying ->
                if (!isPlaying) {
                    // --- Challenge List Map View ---
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
                    ) {
                        item {
                            // Header Progress Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(PremiumRadius.card)
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Text(
                                        text = if (isHindi) "आपकी प्रगति" else "Your Progress",
                                        style = PremiumTypography.Caption,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val completedCount = viewModel.quickChallengeUnlockedCount - 1
                                    Text(
                                        text = if (isHindi) "${completedCount} / 50 चुनौतियां पूरी की गईं" else "${completedCount} of 50 Challenges Cleared",
                                        style = PremiumTypography.SectionTitle.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    // Simple progress bar
                                    val progressFraction = completedCount.toFloat() / 50f
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(progressFraction)
                                                .background(Color.White, CircleShape)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(QuizData.questions.size) { index ->
                            val question = QuizData.questions[index]
                            val challengeId = question.id
                            val isCompleted = challengeId < viewModel.quickChallengeUnlockedCount
                            val isUnlocked = challengeId == viewModel.quickChallengeUnlockedCount
                            val isLocked = challengeId > viewModel.quickChallengeUnlockedCount

                            val cardBgColor = when {
                                isUnlocked -> if (isDark) Color(0xFF1E293B) else Color.White
                                isCompleted -> if (isDark) Color(0xFF0F172A).copy(alpha = 0.6f) else Color(0xFFEFF6FF)
                                else -> if (isDark) Color(0xFF1E293B).copy(alpha = 0.4f) else Color(0xFFF8FAFC)
                            }

                            val borderStroke = when {
                                isUnlocked -> BorderStroke(2.dp, PremiumColors.PrimaryAccent)
                                isCompleted -> BorderStroke(1.dp, PremiumColors.Safe.copy(alpha = 0.4f))
                                else -> BorderStroke(
                                    1.dp,
                                    if (isDark) Color(0xFF334155).copy(alpha = 0.4f) else Color(0xFFE2E8F0).copy(alpha = 0.6f)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(borderStroke, PremiumRadius.card)
                                    .clip(PremiumRadius.card)
                                    .background(cardBgColor)
                                    .clickable(enabled = !isLocked) {
                                        activeChallengeQuestion = question
                                        selectedOptionIndex = null
                                        isAnswerSubmitted = false
                                        playingMode = true
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Badge / ID Indicator
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = when {
                                                isCompleted -> PremiumColors.Safe.copy(alpha = 0.15f)
                                                isUnlocked -> PremiumColors.PrimaryAccent.copy(alpha = 0.15f)
                                                else -> if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                                            },
                                            shape = CircleShape
                                        )
                                ) {
                                    if (isLocked) {
                                        Icon(
                                            imageVector = Icons.Rounded.Lock,
                                            contentDescription = "Locked",
                                            tint = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = "Completed",
                                            tint = PremiumColors.Safe,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "$challengeId",
                                            style = PremiumTypography.CardTitle.copy(fontWeight = FontWeight.Bold),
                                            color = PremiumColors.PrimaryAccent
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Challenge Title / Topic
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isHindi) "चुनौती #${challengeId}" else "Challenge #${challengeId}",
                                        style = PremiumTypography.CardTitle.copy(fontWeight = FontWeight.Bold),
                                        color = if (isLocked) {
                                            if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                                        } else {
                                            if (isDark) Color.White else PremiumColors.TextDark
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = getLocalizedTopic(question.topic, isHindi),
                                        style = PremiumTypography.Caption,
                                        color = if (isLocked) {
                                            if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1)
                                        } else {
                                            if (isDark) Color(0xFF94A3B8) else PremiumColors.SubtitleGray
                                        }
                                    )
                                }

                                // Right-side state badge
                                if (isUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .background(PremiumColors.PrimaryAccent, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "शुरू करें" else "START",
                                            style = PremiumTypography.Caption.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                } else if (isCompleted) {
                                    Text(
                                        text = if (isHindi) "पुनः खेलें" else "Replay",
                                        style = PremiumTypography.Caption.copy(fontWeight = FontWeight.SemiBold),
                                        color = PremiumColors.PrimaryAccent
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // --- Gameplay Single Level View ---
                    activeChallengeQuestion?.let { question ->
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Challenge ID Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isDark) Color(0xFF334155) else Color(0xFFDBEAFE),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = getLocalizedTopic(question.topic, isHindi),
                                        style = PremiumTypography.Caption.copy(fontWeight = FontWeight.Bold),
                                        color = PremiumColors.PrimaryAccent
                                    )
                                }

                                Text(
                                    text = if (isHindi) "चुनौती #${question.id} / 50" else "Challenge #${question.id} of 50",
                                    style = PremiumTypography.Caption.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDark) Color(0xFF94A3B8) else PremiumColors.SubtitleGray
                                )
                            }

                            // Question Statement Card
                            PremiumCard(
                                backgroundColor = if (isDark) Color(0xFF1E293B) else Color.White,
                                borderColor = if (isDark) Color(0xFF334155) else PremiumColors.SubtleBorderLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Security,
                                        contentDescription = null,
                                        tint = PremiumColors.PrimaryAccent,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = if (isHindi) question.questionHi else question.questionEn,
                                        style = PremiumTypography.SectionTitle.copy(
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 28.sp,
                                            fontSize = 18.sp
                                        ),
                                        color = if (isDark) Color.White else PremiumColors.TextDark
                                    )
                                }
                            }

                            // Options List
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val options = if (isHindi) question.optionsHi else question.optionsEn
                                val correctIdx = question.correctAnswerIndex

                                options.forEachIndexed { idx, option ->
                                    val isSelected = selectedOptionIndex == idx
                                    val isCorrectAnswer = idx == correctIdx

                                    val borderStrokeColor = when {
                                        isAnswerSubmitted && isCorrectAnswer -> PremiumColors.Safe
                                        isAnswerSubmitted && isSelected && !isCorrectAnswer -> PremiumColors.Danger
                                        isSelected -> PremiumColors.PrimaryAccent
                                        else -> if (isDark) Color(0xFF334155) else PremiumColors.SubtleBorderLight
                                    }

                                    val backgroundColor = when {
                                        isAnswerSubmitted && isCorrectAnswer -> PremiumColors.Safe.copy(alpha = if (isDark) 0.16f else 0.08f)
                                        isAnswerSubmitted && isSelected && !isCorrectAnswer -> PremiumColors.Danger.copy(alpha = if (isDark) 0.16f else 0.08f)
                                        isSelected -> PremiumColors.PrimaryAccent.copy(alpha = if (isDark) 0.15f else 0.05f)
                                        else -> if (isDark) Color(0xFF1E293B) else Color.White
                                    }

                                    val indicatorBg = when {
                                        isAnswerSubmitted && isCorrectAnswer -> PremiumColors.Safe
                                        isAnswerSubmitted && isSelected && !isCorrectAnswer -> PremiumColors.Danger
                                        isSelected -> PremiumColors.PrimaryAccent
                                        else -> if (isDark) Color(0xFF475569) else Color(0xFFF1F5F9)
                                    }

                                    val indicatorTextColor = if (isSelected || (isAnswerSubmitted && (isCorrectAnswer || isSelected))) {
                                        Color.White
                                    } else {
                                        if (isDark) Color(0xFFCBD5E1) else PremiumColors.TextDark
                                    }

                                    val optionLabel = when (idx) {
                                        0 -> "A"
                                        1 -> "B"
                                        2 -> "C"
                                        else -> "D"
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(
                                                BorderStroke(if (isSelected || (isAnswerSubmitted && (isCorrectAnswer || isSelected))) 2.dp else 1.5.dp, borderStrokeColor),
                                                PremiumRadius.card
                                            )
                                            .clip(PremiumRadius.card)
                                            .background(backgroundColor)
                                            .clickable(enabled = !isAnswerSubmitted) { selectedOptionIndex = idx }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(indicatorBg, CircleShape)
                                        ) {
                                            Text(
                                                text = optionLabel,
                                                style = PremiumTypography.CardTitle.copy(fontWeight = FontWeight.Bold),
                                                color = indicatorTextColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Text(
                                            text = option,
                                            style = PremiumTypography.Body.copy(fontWeight = FontWeight.Medium),
                                            color = if (isDark) Color.White else PremiumColors.TextDark,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (isAnswerSubmitted) {
                                            if (isCorrectAnswer) {
                                                Icon(
                                                    imageVector = Icons.Rounded.CheckCircle,
                                                    contentDescription = "Correct",
                                                    tint = PremiumColors.Safe,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            } else if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Cancel,
                                                    contentDescription = "Incorrect",
                                                    tint = PremiumColors.Danger,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Explanation Block
                            AnimatedVisibility(
                                visible = isAnswerSubmitted,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                val isUserCorrect = selectedOptionIndex == question.correctAnswerIndex
                                val feedbackText = if (isUserCorrect) {
                                    if (isHindi) "✅ बिल्कुल सही उत्तर!" else "✅ Correct Answer!"
                                } else {
                                    if (isHindi) "❌ गलत उत्तर!" else "❌ Incorrect!"
                                }
                                val feedbackColor = if (isUserCorrect) PremiumColors.Safe else PremiumColors.Danger

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.5.dp,
                                            color = feedbackColor.copy(alpha = 0.3f),
                                            shape = PremiumRadius.card
                                        )
                                        .clip(PremiumRadius.card)
                                        .background(feedbackColor.copy(alpha = if (isDark) 0.08f else 0.03f))
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isUserCorrect) Icons.Rounded.Verified else Icons.Rounded.Error,
                                            contentDescription = null,
                                            tint = feedbackColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = feedbackText,
                                            style = PremiumTypography.SectionTitle.copy(fontWeight = FontWeight.Bold),
                                            color = feedbackColor
                                        )
                                    }

                                    PremiumDivider()

                                    Text(
                                        text = if (isHindi) "महत्वपूर्ण सुरक्षा पाठ:" else "Safety Explanation:",
                                        style = PremiumTypography.CardTitle.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDark) Color.White else PremiumColors.TextDark
                                    )

                                    Text(
                                        text = if (isHindi) question.explanationHi else question.explanationEn,
                                        style = PremiumTypography.Body,
                                        color = if (isDark) Color(0xFFCBD5E1) else PremiumColors.SubtitleGray,
                                        lineHeight = 22.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Submit / Continue Button
                            if (!isAnswerSubmitted) {
                                PremiumButton(
                                    onClick = { isAnswerSubmitted = true },
                                    enabled = selectedOptionIndex != null,
                                    text = if (isHindi) "उत्तर दर्ज करें" else "Submit Answer",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                val isUserCorrect = selectedOptionIndex == question.correctAnswerIndex
                                if (isUserCorrect) {
                                    PremiumButton(
                                        onClick = {
                                            // Unlock the next level
                                            viewModel.unlockNextQuickChallenge(question.id)
                                            // Return back to list map
                                            playingMode = false
                                            activeChallengeQuestion = null
                                        },
                                        text = if (isHindi) "चुनौती पूरी करें" else "Complete Challenge",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        PremiumButton(
                                            onClick = {
                                                selectedOptionIndex = null
                                                isAnswerSubmitted = false
                                            },
                                            style = PremiumButtonStyle.Outlined,
                                            text = if (isHindi) "पुनः प्रयास करें" else "Try Again",
                                            modifier = Modifier.weight(1f)
                                        )
                                        PremiumButton(
                                            onClick = {
                                                playingMode = false
                                                activeChallengeQuestion = null
                                            },
                                            text = if (isHindi) "वापस जाएं" else "Back to Map",
                                            modifier = Modifier.weight(1f)
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

fun getLocalizedTopic(topic: String, isHindi: Boolean): String {
    if (!isHindi) return topic
    return when (topic) {
        "Phishing" -> "फ़िशिंग (Phishing)"
        "OTP Fraud" -> "ओटीपी धोखाधड़ी (OTP Fraud)"
        "UPI Scam" -> "यूपीआई धोखाधड़ी (UPI Scam)"
        "QR Scam" -> "क्यूआर स्कैन घोटाला (QR Scam)"
        "Fake Banking" -> "फर्जी बैंकिंग (Fake Banking)"
        "Social Engineering" -> "सोशल इंजीनियरिंग (Social Engineering)"
        "Password Safety" -> "पासवर्ड सुरक्षा (Password Safety)"
        "Malware" -> "मैलवेयर (Malware)"
        "Online Shopping" -> "ऑनलाइन शॉपिंग सुरक्षा (Online Shopping)"
        "General Cyber Safety" -> "सामान्य साइबर सुरक्षा (Cyber Safety)"
        else -> topic
    }
}
