package com.example

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
import com.example.ui.theme.premiumShadow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.PremiumColors
import com.example.ui.theme.PremiumTypography
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    viewModel: ScamLensViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"

    // Use the challenge and day from ViewModel
    val challenge = viewModel.currentChallenge
    val challengeDay = viewModel.currentChallengeDay

    // Local states
    val isCompleted = viewModel.challengeCompletedToday
    val savedSelectedIndex = viewModel.selectedOptionIndex
    
    var tempSelectedIndex by rememberSaveable { mutableStateOf(-1) }
    val selectedIndex = if (isCompleted) savedSelectedIndex else tempSelectedIndex

    val primaryBlue = PremiumColors.PrimaryAccent
    val textPrimary = if (isDark) Color.White else Color(0xFF111111)
    val textSecondary = if (isDark) Color(0xFFA1A1AA) else Color(0xFF6E6E73)
    val bgColor = if (isDark) Color(0xFF0F1115) else Color(0xFFF8FAFC)
    val cardBg = if (isDark) Color(0xFF171A20) else Color(0xFFFFFFFF)
    val cardBorderColor = if (isDark) Color(0xFF252932) else Color(0xFFDCE3EE)

    val successGreen = PremiumColors.Safe
    val warningOrange = PremiumColors.Warning
    val dangerRed = PremiumColors.Danger

    val titleText = if (isCompleted) {
        if (isHindi) "आज की चुनौती पूरी हुई" else "Today's Challenge Completed"
    } else {
        if (isHindi) "आज का स्कैम चैलेंज" else "Today's Scam Challenge"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = titleText,
                            style = TextStyle(
                                fontSize = 18.sp,
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
            // Header Info Card (Day & Category Badges)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day Badge
                Row(
                    modifier = Modifier
                        .background(
                            color = primaryBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Event,
                        contentDescription = null,
                        tint = primaryBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "दिन $challengeDay / 30" else "Day $challengeDay of 30",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                    )
                }

                // Category & Difficulty Badges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Category
                    Text(
                        text = challenge.category.uppercase(),
                        style = TextStyle(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textSecondary
                        ),
                        modifier = Modifier
                            .background(
                                color = if (isDark) Color(0xFF2E3545) else Color(0xFFDCE3EE),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    // Difficulty
                    val diffColor = when (challenge.difficulty) {
                        "Easy" -> successGreen
                        "Medium" -> warningOrange
                        "Hard" -> dangerRed
                        else -> primaryBlue
                    }
                    Text(
                        text = (if (isHindi) challenge.difficultyHi else challenge.difficulty).uppercase(),
                        style = TextStyle(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .background(
                                color = diffColor,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Daily Quiz Notification / Reminder Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = primaryBlue.copy(alpha = 0.05f)
                ),
                border = BorderStroke(1.dp, primaryBlue.copy(alpha = 0.15f))
            ) {
                val context = LocalContext.current
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "दैनिक क्विज़ अनुस्मारक 🔔" else "Daily Quiz Reminder 🔔",
                            style = TextStyle(
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryBlue
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isHindi) "हर सुबह 9:00 बजे अधिसूचना प्राप्त करें।" else "Get notified every morning at 9:00 AM.",
                            style = TextStyle(
                                fontSize = 11.5.sp,
                                color = textSecondary
                            )
                        )
                    }
                    Button(
                        onClick = {
                            NotificationHelper.showDailyChallengeNotification(context)
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isHindi) "अभी जांचें" else "Test Now",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Question Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = if (isDark) Color.Black else Color(0xFF0052FF).copy(alpha = 0.05f),
                        spotColor = if (isDark) Color.Black else Color(0xFF0052FF).copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorderColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Scam Type Label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ReportProblem,
                            contentDescription = null,
                            tint = warningOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) challenge.scamTypeLabelHi else challenge.scamTypeLabel,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = warningOrange
                            )
                        )
                    }

                    // Question Text
                    Text(
                        text = challenge.question.getText(isHindi),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // Options Header
            Text(
                text = if (isHindi) "एक विकल्प चुनें:" else "Choose one option:",
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
                            indication = androidx.compose.foundation.LocalIndication.current
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Option letter/marker (A, B, C...)
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
                val buttonInteraction = remember { MutableInteractionSource() }
                val isBtnPressed by buttonInteraction.collectIsPressedAsState()
                val btnScale by animateFloatAsState(
                    targetValue = if (isBtnPressed) 0.97f else 1.0f,
                    animationSpec = tween(100),
                    label = "btn_scale"
                )

                val hasSelection = tempSelectedIndex != -1

                Button(
                    onClick = {
                        if (hasSelection) {
                            viewModel.completeChallenge(tempSelectedIndex)
                        }
                    },
                    enabled = hasSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(54.dp)
                        .scale(btnScale),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Color(0xFF202631) else Color(0xFFDCE3EE),
                        disabledContentColor = textSecondary.copy(alpha = 0.5f)
                    ),
                    interactionSource = buttonInteraction
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VerifiedUser,
                            contentDescription = null,
                            tint = if (hasSelection) Color.White else textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "चैलेंज सबमिट करें" else "Submit Challenge Answer",
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
                                    if (isHindi) "बधाई हो! सही उत्तर" else "Correct Answer!"
                                } else {
                                    if (isHindi) "गलत उत्तर" else "Incorrect!"
                                },
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userWasCorrect) successGreen else dangerRed
                                )
                            )
                            Text(
                                text = if (userWasCorrect) {
                                    if (isHindi) "शानदार काम! आपकी सुरक्षा समझ मजबूत है।" else "Splendid work! Your security awareness is sharp."
                                } else {
                                    if (isHindi) "कोई बात नहीं, यह सीखने का एक अच्छा अवसर था!" else "Don't worry, every wrong answer is a learning event!"
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
                        .premiumShadow(isDark, 16.dp),
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

                // Did You Know? Card
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

                // Back to Dashboard / Continue Button
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF1F2937) else Color(0xFFF1F5F9),
                        contentColor = textPrimary
                    ),
                    border = BorderStroke(1.dp, cardBorderColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = null,
                            tint = textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "डैशबोर्ड पर वापस जाएं" else "Back to Dashboard",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
