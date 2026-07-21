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

    val titleText = if (isHindi) "365 दिन का स्कैम चैलेंज" else "365-Day Scam Challenge"

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ------------------ PROGRESS TRACKING PANEL ------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .premiumShadow(isDark, 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "आपकी सुरक्षा प्रगति" else "Your Security Progress",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                letterSpacing = (-0.3).sp
                            )
                        )
                        
                        val completionPercent = (viewModel.totalCompleted / 365.0f * 100).toInt()
                        Text(
                            text = "$completionPercent%",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryBlue
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Linear Progress Bar
                    LinearProgressIndicator(
                        progress = { viewModel.totalCompleted / 365.0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = primaryBlue,
                        trackColor = if (isDark) Color(0xFF1E242E) else Color(0xFFE2E8F0)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Row of stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Total Completed Stat
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
                                text = if (isHindi) "कुल पूरे किए" else "Total Completed",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = textSecondary,
                                    fontWeight = FontWeight.Medium
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Vertical Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(cardBorderColor)
                                .align(Alignment.CenterVertically)
                        )
                        
                        // Current Streak Stat
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
                                text = if (isHindi) "वर्तमान स्ट्रीक" else "Current Streak",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = textSecondary,
                                    fontWeight = FontWeight.Medium
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Vertical Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(cardBorderColor)
                                .align(Alignment.CenterVertically)
                        )
                        
                        // Best Streak Stat
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
                                text = if (isHindi) "सर्वश्रेष्ठ स्ट्रीक" else "Best Streak",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = textSecondary,
                                    fontWeight = FontWeight.Medium
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // ------------------ DAY / CHALLENGE SELECTOR ------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button
                IconButton(
                    onClick = { viewModel.moveToPreviousChallenge() },
                    enabled = challengeDay > 1,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isDark) Color(0xFF1E242E) else Color(0xFFE2E8F0),
                        disabledContainerColor = if (isDark) Color(0xFF13171F) else Color(0xFFF1F5F9)
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Previous Day",
                        tint = if (challengeDay > 1) textPrimary else textSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Central Title
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isHindi) "चैलेंज दिन $challengeDay / 365" else "Challenge Day $challengeDay of 365",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.3).sp
                        )
                    )
                    
                    val todayDay = viewModel.getTodayChallengeDay()
                    if (challengeDay == todayDay) {
                        Text(
                            text = if (isHindi) "आज की चुनौती" else "TODAY'S CHALLENGE",
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = primaryBlue,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Next Button
                IconButton(
                    onClick = { viewModel.moveToNextChallenge() },
                    enabled = challengeDay < 365,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isDark) Color(0xFF1E242E) else Color(0xFFE2E8F0),
                        disabledContainerColor = if (isDark) Color(0xFF13171F) else Color(0xFFF1F5F9)
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = "Next Day",
                        tint = if (challengeDay < 365) textPrimary else textSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ------------------ SCENARIO CARD ------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .premiumShadow(isDark, 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorderColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header tag row inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Chip
                        Text(
                            text = challenge.category.uppercase(),
                            style = TextStyle(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textSecondary
                            ),
                            modifier = Modifier
                                .background(
                                    color = if (isDark) Color(0xFF232833) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        )

                        // Difficulty Tag
                        val diffColor = when (challenge.difficulty) {
                            "Easy" -> successGreen
                            "Medium" -> warningOrange
                            "Hard" -> dangerRed
                            else -> primaryBlue
                        }
                        Text(
                            text = (if (isHindi) challenge.difficultyHi else challenge.difficulty).uppercase(),
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier
                                .background(
                                    color = diffColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = challenge.title.getText(isHindi),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary,
                            letterSpacing = (-0.5).sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scenario simulation container (Apple style message)
                    val isMessageStyle = challenge.category in listOf("PHISHING", "OTP_FRAUD", "DELIVERY_SCAM", "WHATSAPP_SCAM", "SOCIAL_ENG")
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isDark) {
                                    if (isMessageStyle) Color(0xFF1E2430) else Color(0xFF14171E)
                                } else {
                                    if (isMessageStyle) Color(0xFFE8F0FE) else Color(0xFFF1F5F9)
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                BorderStroke(
                                    width = 1.dp,
                                    color = if (isMessageStyle) primaryBlue.copy(alpha = 0.2f) else cardBorderColor
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            // Mock sender info
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
                                        if (isHindi) "संदिग्ध अलर्ट / संदेश" else "SUSPICIOUS ALERT / MESSAGE"
                                    } else {
                                        if (isHindi) "स्कैम परिदृश्य" else "SCAM SCENARIO"
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

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider(color = cardBorderColor, thickness = 1.dp)
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Question section
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "❓",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text(
                            text = challenge.question.getText(isHindi),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            }

            // Options Header
            Text(
                text = if (isHindi) "सुरक्षित कार्रवाई का चयन करें:" else "Select the secure response action:",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )

            // Option Selection Cards
            challenge.options.forEachIndexed { index, option ->
                val optionText = option.getText(isHindi)
                val isSelected = selectedIndex == index
                val isCorrect = challenge.correctOptionIndex == index

                // Define visual styles based on states: pre-completed vs post-completed
                val optionBorderColor = when {
                    isCompleted -> {
                        when {
                            isCorrect -> successGreen
                            isSelected -> dangerRed
                            else -> cardBorderColor
                        }
                    }
                    isSelected -> primaryBlue
                    else -> cardBorderColor
                }

                val optionBgColor = when {
                    isCompleted -> {
                        when {
                            isCorrect -> successGreen.copy(alpha = if (isDark) 0.15f else 0.08f)
                            isSelected -> dangerRed.copy(alpha = if (isDark) 0.15f else 0.08f)
                            else -> cardBg
                        }
                    }
                    isSelected -> primaryBlue.copy(alpha = if (isDark) 0.12f else 0.06f)
                    else -> cardBg
                }

                val optionTextColor = when {
                    isCompleted -> {
                        when {
                            isCorrect -> successGreen
                            isSelected -> dangerRed
                            else -> textPrimary
                        }
                    }
                    isSelected -> primaryBlue
                    else -> textPrimary
                }

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val pressScale by animateFloatAsState(
                    targetValue = if (isPressed && !isCompleted) 0.98f else 1.0f,
                    animationSpec = tween(100),
                    label = "option_scale"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(pressScale)
                        .background(optionBgColor, shape = RoundedCornerShape(12.dp))
                        .border(
                            BorderStroke(if (isSelected || (isCompleted && isCorrect)) 2.dp else 1.dp, optionBorderColor),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            enabled = !isCompleted,
                            onClick = { tempSelectedIndex = index },
                            interactionSource = interactionSource,
                            indication = LocalIndication.current
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Option letter marker (A, B, C, D)
                    val letter = ('A' + index).toString()
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = when {
                                    isCompleted && isCorrect -> successGreen
                                    isCompleted && isSelected -> dangerRed
                                    isSelected -> primaryBlue
                                    else -> if (isDark) Color(0xFF232A35) else Color(0xFFDCE3EE)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected || (isCompleted && (isCorrect || isSelected))) Color.White else textSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = optionText,
                        style = TextStyle(
                            fontSize = 14.5.sp,
                            fontWeight = if (isSelected || (isCompleted && isCorrect)) FontWeight.Bold else FontWeight.Medium,
                            color = optionTextColor,
                            lineHeight = 19.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // End status icon (check or cross)
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isCorrect) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Correct",
                                tint = successGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Cancel,
                                contentDescription = "Incorrect",
                                tint = dangerRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Submit / Action Button (Pre-Completed state)
            if (!isCompleted) {
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
                        .padding(top = 8.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Color(0xFF202631) else Color(0xFFDCE3EE),
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
            }

            // Explanation & Result Cards (Post-Completed state)
            if (isCompleted) {
                val userWasCorrect = savedSelectedIndex == challenge.correctOptionIndex

                // Result Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (userWasCorrect) {
                            successGreen.copy(alpha = 0.08f)
                        } else {
                            dangerRed.copy(alpha = 0.08f)
                        }
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (userWasCorrect) successGreen.copy(alpha = 0.3f) else dangerRed.copy(alpha = 0.3f)
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
                            modifier = Modifier.size(32.dp)
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
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userWasCorrect) successGreen else dangerRed
                                )
                            )
                            Text(
                                text = if (userWasCorrect) {
                                    if (isHindi) "शानदार काम! आपकी सुरक्षा समझ मजबूत है।" else "Outstanding decision. Your shield against this threat held firm."
                                } else {
                                    if (isHindi) "कोई बात नहीं, सीखें कि इस स्कैम से कैसे बचना है!" else "Critical compromise. Study the warning signs below to defend yourself."
                                },
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = textSecondary
                                )
                            )
                        }
                    }
                }

                // Detailed Cyber-Security Explanation Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumShadow(isDark, 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lightbulb,
                                contentDescription = null,
                                tint = warningOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "विशेषज्ञ सुरक्षा विश्लेषण:" else "Expert Security Analysis:",
                                style = TextStyle(
                                    fontSize = 14.sp,
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

                // Key Warning Signs Card
                if (challenge.warningSigns.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .premiumShadow(isDark, 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Warning,
                                    contentDescription = null,
                                    tint = dangerRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "महत्वपूर्ण चेतावनी संकेत:" else "Key Warning Signs:",
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

                // How to Stay Safe Card
                if (challenge.howToStaySafe.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .premiumShadow(isDark, 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
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
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp, end = 6.dp)
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

                // Did You Know? / Safety Tip Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = warningOrange.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, warningOrange.copy(alpha = 0.2f))
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
                                text = if (isHindi) "क्या आप जानते हैं?" else "Did You Know?",
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

                // Navigation Buttons (Next / Prev or Back to Dashboard)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Back Button
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF1F2937) else Color(0xFFF1F5F9),
                            contentColor = textPrimary
                        ),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Text(
                            text = if (isHindi) "डैशबोर्ड" else "Dashboard",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                    // Next Challenge Quick Access
                    if (challengeDay < 365) {
                        Button(
                            onClick = { viewModel.moveToNextChallenge() },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "अगला चैलेंज" else "Next Challenge",
                                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
