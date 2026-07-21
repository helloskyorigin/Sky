package com.skyorigin.threatshieldai

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    viewModel: ScamLensViewModel,
    onNavigateToChallenge: () -> Unit,
    onNavigateToAcademy: () -> Unit,
    onNavigateToDailyTip: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToCommonScams: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"
    val context = LocalContext.current

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = MaterialTheme.colorScheme.background

    LaunchedEffect(Unit) {
        AnalyticsManager.getInstance(context).logLearnOpened()
        AnalyticsManager.getInstance(context).logScreenView("learn_hub")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.School,
                            contentDescription = null,
                            tint = PremiumColors.PrimaryAccent,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isHindi) "स्कैम पाठशाला" else "Learn Hub",
                            style = TextStyle(
                                fontSize = 20.sp,
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Introductory Header Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PremiumRadius.card)
                    .background(
                        if (isDark) Color(0xFF161F2E) else PremiumColors.PrimaryAccent.copy(alpha = 0.05f)
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF1F2937) else PremiumColors.PrimaryAccent.copy(alpha = 0.1f)
                        ),
                        PremiumRadius.card
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = if (isHindi) "सुरक्षित रहने के लिए सीखें" else "Knowledge is Your Best Shield",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) PremiumColors.PrimaryAccent else Color(0xFF1D4ED8)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) {
                            "नवीनतम घोटालों, तकनीकों और व्यावहारिक डिजिटल सुरक्षा सुझावों के साथ खुद को अपडेट रखें।"
                        } else {
                            "Stay ahead of fraudsters. Equip yourself with real-world scam indicators, vocabulary, and interactive quizzes."
                        },
                        style = TextStyle(
                            fontSize = 13.5.sp,
                            lineHeight = 18.sp,
                            color = textSecondary
                        )
                    )
                }
            }

            // 1. Today's Scam Challenge
            LearnCard(
                title = if (isHindi) "आज का स्कैम चैलेंज" else "Today's Scam Challenge",
                description = if (isHindi) "वास्तविक दुनिया के घोटालों को पहचानने की अपनी क्षमता का परीक्षण करें।" else "Test your ability to identify real-world scams.",
                icon = Icons.Rounded.EmojiEvents,
                iconColor = PremiumColors.PrimaryAccent,
                onClick = onNavigateToChallenge
            )

            // 2. Scam Academy
            LearnCard(
                title = if (isHindi) "स्कैम एकेडमी" else "Scam Academy",
                description = if (isHindi) "आम घोटालों के बारे में जानें और सुरक्षित रहने के तरीके सीखें।" else "Learn about common scams and how to stay protected.",
                icon = Icons.Rounded.Class,
                iconColor = Color(0xFF8B5CF6),
                onClick = onNavigateToAcademy
            )

            // 3. Daily Safety Tip
            LearnCard(
                title = if (isHindi) "दैनिक सुरक्षा टिप" else "Daily Safety Tip",
                description = if (isHindi) "हर दिन एक व्यावहारिक साइबर सुरक्षा टिप।" else "One practical cybersecurity tip every day.",
                icon = Icons.Rounded.Lightbulb,
                iconColor = Color(0xFFEAB308),
                onClick = onNavigateToDailyTip
            )

            // 4. Quick Challenge
            LearnCard(
                title = if (isHindi) "त्वरित चुनौती" else "Quick Challenge",
                description = if (isHindi) "क्रमिक सुरक्षा स्तरों के साथ अपने ज्ञान का परीक्षण करें।" else "Test your knowledge with sequential security levels.",
                icon = Icons.Rounded.Quiz,
                iconColor = Color(0xFF10B981),
                onClick = onNavigateToQuiz
            )

            // 5. Common Scam Examples
            LearnCard(
                title = if (isHindi) "सामान्य स्कैम उदाहरण" else "Common Scam Examples",
                description = if (isHindi) "सामान्य घोटालों और चेतावनी संकेतों के उदाहरणों का पता लगाएं।" else "Explore examples of common scams and warning signs.",
                icon = Icons.Rounded.Warning,
                iconColor = PremiumColors.Danger,
                onClick = onNavigateToCommonScams
            )

            // 6. Cyber Dictionary
            LearnCard(
                title = if (isHindi) "साइबर शब्दकोश" else "Cyber Dictionary",
                description = if (isHindi) "महत्वपूर्ण साइबर सुरक्षा शब्दों को समझें।" else "Understand important cybersecurity terms.",
                icon = Icons.Rounded.MenuBook,
                iconColor = Color(0xFF06B6D4),
                onClick = onNavigateToDictionary
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Elegant spring animation for press scale down
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "learn_card_scale"
    )

    val elevation = if (isDark) 0.dp else 3.dp
    val bgColor = if (isDark) Color(0xFF111827) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .premiumShadow(
                isDark = isDark,
                borderRadius = 22.dp,
                elevation = elevation,
                shadowColor = if (isDark) Color.Black else Color(0xFF0F172A).copy(alpha = 0.04f)
            )
            .border(BorderStroke(1.5.dp, borderColor), PremiumRadius.card)
            .clip(PremiumRadius.card),
        color = bgColor,
        shape = PremiumRadius.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with matching design system helper
            PremiumIconContainer(
                icon = icon,
                tintColor = iconColor,
                size = 48.dp,
                cornerRadius = 14.dp,
                iconSize = 22.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = PremiumTypography.CardTitle.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF111827)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = PremiumTypography.Caption.copy(
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Chevron Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isDark) Color(0xFF4B5563) else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
