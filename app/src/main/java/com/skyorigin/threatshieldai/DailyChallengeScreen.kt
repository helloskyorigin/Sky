package com.skyorigin.threatshieldai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import com.skyorigin.threatshieldai.ui.theme.premiumShadow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
import com.skyorigin.threatshieldai.ui.theme.PremiumColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    viewModel: ScamLensViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"

    // Observed states from ViewModel
    val challenge = viewModel.currentChallenge
    val challengeDay = viewModel.currentChallengeDay
    val isCompleted = viewModel.challengeCompletedToday
    val savedSelectedIndex = viewModel.selectedOptionIndex

    var tempSelectedIndex by rememberSaveable(challengeDay) { mutableStateOf(-1) }
    val selectedIndex = if (isCompleted) savedSelectedIndex else tempSelectedIndex

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AnalyticsManager.getInstance(context).logLearnOpened()
        AnalyticsManager.getInstance(context).logScreenView("daily_challenge")
    }

    LaunchedEffect(challengeDay) {
        AnalyticsManager.getInstance(context).logArticleOpened(
            articleId = "day_$challengeDay",
            category = challenge.category
        )
    }

    val primaryBlue = PremiumColors.PrimaryAccent
    val textPrimary = if (isDark) Color.White else Color(0xFF111111)
    val textSecondary = if (isDark) Color(0xFFA1A1AA) else Color(0xFF6E6E73)
    val bgColor = if (isDark) Color(0xFF0F1115) else Color(0xFFF8FAFC)
    val cardBg = if (isDark) Color(0xFF171A20) else Color(0xFFFFFFFF)
    val cardBorderColor = if (isDark) Color(0xFF252932) else Color(0xFFDCE3EE)

    val successGreen = PremiumColors.Safe
    val warningOrange = PremiumColors.Warning
    val dangerRed = PremiumColors.Danger

    val titleText = if (isHindi) "Aaj ka Scam Challenge" else "Today's Scam Challenge"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titleText,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.3).sp
                        )
                    )
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedContent(
                targetState = isCompleted,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "challenge_state_transition"
            ) { completed ->
                if (!completed) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Compact Progress Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumShadow(isDark, 4.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, cardBorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Total Completed
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = viewModel.totalCompleted.toString(),
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isHindi) "Total Complete" else "Total Completed",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Divider
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(cardBorderColor)
                                )

                                // Current Streak
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "🔥 ",
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = viewModel.challengeStreak.toString(),
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (viewModel.challengeStreak > 0) Color(0xFFFF5A00) else textPrimary
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isHindi) "Current Streak" else "Current Streak",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Divider
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(cardBorderColor)
                                )

                                // Best Streak
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "🏆 ",
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = viewModel.longestStreak.toString(),
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (viewModel.longestStreak > 0) Color(0xFFFFC107) else textPrimary
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isHindi) "Best Streak" else "Best Streak",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // 2. Challenge Header Card (Category Badge, Difficulty Badge, Challenge Title)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumShadow(isDark, 4.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, cardBorderColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Category Badge
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isDark) Color(0xFF232833) else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = challenge.category.uppercase(),
                                            style = TextStyle(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = textSecondary,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                    }

                                    // Difficulty Badge
                                    val diffColor = when (challenge.difficulty) {
                                        "Easy" -> successGreen
                                        "Medium" -> warningOrange
                                        "Hard" -> dangerRed
                                        else -> primaryBlue
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = diffColor,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = (if (isHindi) challenge.difficultyHi else challenge.difficulty).uppercase(),
                                            style = TextStyle(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = challenge.title.getText(isHindi),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = textPrimary,
                                        letterSpacing = (-0.4).sp,
                                        lineHeight = 24.sp
                                    )
                                )
                            }
                        }

                        // 3. Scenario Card (Soft blue/neutral background, message inside, no excessive padding)
                        val isMessageStyle = challenge.category in listOf("PHISHING", "OTP_FRAUD", "DELIVERY_SCAM", "WHATSAPP_SCAM", "SOCIAL_ENG")
                        val scenarioBg = if (isDark) {
                            if (isMessageStyle) Color(0xFF141A26) else Color(0xFF11141B)
                        } else {
                            if (isMessageStyle) Color(0xFFEDF4FE) else Color(0xFFF3F7FA)
                        }
                        val scenarioBorder = if (isMessageStyle) primaryBlue.copy(alpha = 0.25f) else cardBorderColor

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumShadow(isDark, 4.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = scenarioBg),
                            border = BorderStroke(1.dp, scenarioBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMessageStyle) Icons.Rounded.Sms else Icons.Rounded.Campaign,
                                        contentDescription = null,
                                        tint = if (isMessageStyle) primaryBlue else warningOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isMessageStyle) {
                                            if (isHindi) "Suspicious Message" else "SUSPICIOUS ALERT / MESSAGE"
                                        } else {
                                            if (isHindi) "Scam Scenario" else "SCAM SCENARIO"
                                        },
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isMessageStyle) primaryBlue else warningOrange,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }

                                Text(
                                    text = challenge.scenario.getText(isHindi),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textPrimary,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }

                        // 4. Question Card (Large question title, small helper text)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumShadow(isDark, 4.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, cardBorderColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(
                                        text = "❓",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Column {
                                        Text(
                                            text = challenge.question.getText(isHindi),
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary,
                                                lineHeight = 22.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isHindi) "Safe Option Select kare:" else "Select the secure action response below:",
                                            style = TextStyle(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = textSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Answer Cards (Equal height, Rounded 18dp, Material ripple, Consistent spacing)
                        challenge.options.forEachIndexed { index, option ->
                            val optionText = option.getText(isHindi)
                            val isSelected = selectedIndex == index

                            val optionBorderColor = if (isSelected) primaryBlue else cardBorderColor
                            val optionBgColor = if (isSelected) primaryBlue.copy(alpha = if (isDark) 0.12f else 0.06f) else cardBg
                            val optionTextColor = if (isSelected) primaryBlue else textPrimary

                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val pressScale by animateFloatAsState(
                                targetValue = if (isPressed) 0.98f else 1.0f,
                                animationSpec = tween(100),
                                label = "option_scale"
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(pressScale)
                                    .premiumShadow(isDark, if (isSelected) 6.dp else 2.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = optionBgColor),
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, optionBorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            onClick = { tempSelectedIndex = index },
                                            interactionSource = interactionSource,
                                            indication = LocalIndication.current
                                        )
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val letter = ('A' + index).toString()
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                color = if (isSelected) primaryBlue else if (isDark) Color(0xFF232A35) else Color(0xFFE2E8F0),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter,
                                            style = TextStyle(
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else textSecondary
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = optionText,
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = optionTextColor,
                                            lineHeight = 18.sp
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // 6. Primary Button (56dp height, 18dp radius, Bottom spacing 24dp)
                        var isSubmitting by remember { mutableStateOf(false) }
                        val hasSelection = tempSelectedIndex != -1

                        Button(
                            onClick = {
                                if (hasSelection && !isSubmitting) {
                                    isSubmitting = true
                                    viewModel.completeChallenge(tempSelectedIndex)
                                }
                            },
                            enabled = hasSelection && !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryBlue,
                                contentColor = Color.White,
                                disabledContainerColor = if (isDark) Color(0xFF202631) else Color(0xFFE2E8F0),
                                disabledContentColor = textSecondary.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                val isButtonActive = hasSelection && !isSubmitting
                                Icon(
                                    imageVector = Icons.Rounded.VerifiedUser,
                                    contentDescription = null,
                                    tint = if (isButtonActive) Color.White else textSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "उत्तर सबमिट करें" else "Submit Secure Response",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.3.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                } else {
                    // RESULT SCREEN (LOCKED ORDER)
                    val userWasCorrect = savedSelectedIndex == challenge.correctOptionIndex

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Status Card (red or green)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumShadow(isDark, 6.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (userWasCorrect) {
                                    successGreen.copy(alpha = if (isDark) 0.15f else 0.08f)
                                } else {
                                    dangerRed.copy(alpha = if (isDark) 0.15f else 0.08f)
                                }
                            ),
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = if (userWasCorrect) successGreen.copy(alpha = 0.4f) else dangerRed.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (userWasCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                                    contentDescription = null,
                                    tint = if (userWasCorrect) successGreen else dangerRed,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (userWasCorrect) {
                                            if (isHindi) "बधाई हो! सही उत्तर" else "Correct Shield Earned!"
                                        } else {
                                            if (isHindi) "असुरक्षित उत्तर!" else "Response Breached!"
                                        },
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (userWasCorrect) successGreen else dangerRed,
                                            letterSpacing = (-0.3).sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (userWasCorrect) {
                                            if (isHindi) "शानदार काम! आपकी सुरक्षा समझ मजबूत है।" else "Outstanding decision. Your shield against this threat held firm."
                                        } else {
                                            if (isHindi) "कोई बात नहीं, सीखें कि इस स्कैम से कैसे बचना है!" else "Critical compromise. Study the warning signs below to defend yourself."
                                        },
                                        style = TextStyle(
                                            fontSize = 13.sp,
                                            color = textSecondary,
                                            lineHeight = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        // 2. Expert Analysis Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumShadow(isDark, 4.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, cardBorderColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lightbulb,
                                        contentDescription = null,
                                        tint = warningOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHindi) "Expert Analysis:" else "Expert Security Analysis:",
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                    )
                                }

                                Text(
                                    text = challenge.explanation.getText(isHindi),
                                    style = TextStyle(
                                        fontSize = 13.5.sp,
                                        color = textSecondary,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        // 3. Key Warning Signs Card
                        if (challenge.warningSigns.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .premiumShadow(isDark, 4.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(1.dp, cardBorderColor)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Warning,
                                            contentDescription = null,
                                            tint = dangerRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isHindi) "Warning Signs:" else "Key Warning Signs:",
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                        )
                                    }

                                    challenge.warningSigns.forEach { sign ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "•",
                                                color = dangerRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(
                                                text = sign.getText(isHindi),
                                                style = TextStyle(
                                                    fontSize = 13.5.sp,
                                                    color = textSecondary,
                                                    lineHeight = 19.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 4. How To Stay Safe Card
                        if (challenge.howToStaySafe.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .premiumShadow(isDark, 4.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(1.dp, cardBorderColor)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Shield,
                                            contentDescription = null,
                                            tint = successGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isHindi) "सुरक्षित रहने के नियम:" else "How to Stay Safe:",
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                        )
                                    }

                                    challenge.howToStaySafe.forEach { rule ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = successGreen,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .padding(top = 2.dp, end = 6.dp)
                                            )
                                            Text(
                                                text = rule.getText(isHindi),
                                                style = TextStyle(
                                                    fontSize = 13.5.sp,
                                                    color = textSecondary,
                                                    lineHeight = 19.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 5. Did You Know Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumShadow(isDark, 4.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = warningOrange.copy(alpha = if (isDark) 0.15f else 0.08f)
                            ),
                            border = BorderStroke(1.dp, warningOrange.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💡",
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isHindi) "Did You Know?" else "Did You Know?",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = warningOrange,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = challenge.didYouKnow.getText(isHindi),
                                        style = TextStyle(
                                            fontSize = 13.5.sp,
                                            color = textPrimary,
                                            lineHeight = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        // Challenge Completed Status Message Box
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = successGreen.copy(alpha = if (isDark) 0.15f else 0.08f)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, successGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = successGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHindi) "✅ Challenge Completed" else "✅ Challenge Completed",
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = successGreen
                                        )
                                    )
                                }
                                Text(
                                    text = if (isHindi) "Naye Scam Challenge ke liye kal waapas aayein." else "Come back tomorrow for a new Scam Challenge.",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textSecondary
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // 6. Bottom CTA Button
                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF1F2937) else Color(0xFFF1F5F9),
                                contentColor = textPrimary
                            ),
                            border = BorderStroke(1.dp, cardBorderColor)
                        ) {
                            Text(
                                text = if (isHindi) "Back to Dashboard" else "Back to Dashboard",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
