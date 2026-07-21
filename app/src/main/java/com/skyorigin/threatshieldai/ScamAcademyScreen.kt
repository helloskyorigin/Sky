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
fun ScamAcademyScreen(
    viewModel: ScamLensViewModel,
    onNavigateToCategory: (String) -> Unit,
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
        AnalyticsManager.getInstance(context).logScreenView("scam_academy_list")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Class,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isHindi) "स्कैम एकेडमी" else "Scam Academy",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Academy Intro Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PremiumRadius.card)
                    .background(
                        if (isDark) Color(0xFF1E1B29) else Color(0xFFF5F3FF)
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF2E243E) else Color(0xFFDDD6FE)
                        ),
                        PremiumRadius.card
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = if (isHindi) "डिजिटल जागरूकता आपकी ढाल है" else "Knowledge Protects You",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF8B5CF6)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) {
                            "डिजिटल घोटालों के प्रकारों को गहराई से समझें। प्रत्येक विषय पर विस्तृत विश्लेषण प्राप्त करें और धोखाधड़ी से हमेशा सुरक्षित रहें।"
                        } else {
                            "Deep-dive into different classes of online fraud. Learn how they initiate, spot warning flags, and implement solid safety routines."
                        },
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = textSecondary
                        )
                    )
                }
            }

            // Categories List
            ScamAcademyData.categories.forEach { category ->
                val title = if (isHindi) category.titleHi else category.titleEn
                val desc = if (isHindi) category.shortDescHi else category.shortDescEn
                val icon = getIconForCategory(category.iconName)
                val iconColor = getColorForCategory(category.iconName)

                AcademyCategoryCard(
                    title = title,
                    description = desc,
                    icon = icon,
                    iconColor = iconColor,
                    onClick = { onNavigateToCategory(category.id) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyCategoryCard(
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

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "academy_card_scale"
    )

    val elevation = if (isDark) 0.dp else 2.dp
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
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PremiumIconContainer(
                icon = icon,
                tintColor = iconColor,
                size = 46.dp,
                cornerRadius = 14.dp,
                iconSize = 20.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

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
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isDark) Color(0xFF4B5563) else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun getIconForCategory(iconName: String): ImageVector {
    return when (iconName) {
        "phishing" -> Icons.Rounded.AlternateEmail
        "otp_fraud" -> Icons.Rounded.Key
        "upi_fraud" -> Icons.Rounded.Payments
        "qr_code_scam" -> Icons.Rounded.QrCode
        "job_scam" -> Icons.Rounded.Work
        "loan_scam" -> Icons.Rounded.LocalAtm
        "investment_scam" -> Icons.Rounded.TrendingUp
        "fake_customer_care" -> Icons.Rounded.SupportAgent
        else -> Icons.Rounded.Help
    }
}

fun getColorForCategory(iconName: String): Color {
    return when (iconName) {
        "phishing" -> PremiumColors.PrimaryAccent
        "otp_fraud" -> Color(0xFFF59E0B) // amber
        "upi_fraud" -> Color(0xFF10B981) // emerald
        "qr_code_scam" -> Color(0xFF8B5CF6) // purple
        "job_scam" -> Color(0xFF3B82F6) // blue
        "loan_scam" -> Color(0xFFEF4444) // red
        "investment_scam" -> Color(0xFF22C55E) // green
        "fake_customer_care" -> Color(0xFFEC4899) // pink
        else -> Color(0xFF6B7280)
    }
}
