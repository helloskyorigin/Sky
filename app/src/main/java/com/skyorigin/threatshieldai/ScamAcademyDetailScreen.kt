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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamAcademyDetailScreen(
    categoryId: String,
    viewModel: ScamLensViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"
    val context = LocalContext.current

    val category = remember(categoryId) {
        ScamAcademyData.categories.find { it.id == categoryId }
    }

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = MaterialTheme.colorScheme.background

    if (category == null) {
        // Fallback if not found
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isHindi) "Detail" else "Detail") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            containerColor = bgColor
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isHindi) "Category not found" else "Category not found")
            }
        }
        return
    }

    val title = if (isHindi) category.titleHi else category.titleEn
    val icon = getIconForCategory(category.iconName)
    val themeColor = getColorForCategory(category.iconName)

    LaunchedEffect(categoryId) {
        AnalyticsManager.getInstance(context).logScreenView("scam_academy_detail_$categoryId")
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Header Card with Ambient Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PremiumRadius.card)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isDark) {
                                listOf(themeColor.copy(alpha = 0.12f), Color(0xFF111827))
                            } else {
                                listOf(themeColor.copy(alpha = 0.08f), Color.White)
                            }
                        )
                    )
                    .border(
                        BorderStroke(
                            1.5.dp,
                            if (isDark) themeColor.copy(alpha = 0.25f) else themeColor.copy(alpha = 0.15f)
                        ),
                        PremiumRadius.card
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Elevated Icon Backdrop
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                color = if (isDark) Color(0xFF1F2937) else Color.White,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .premiumShadow(
                                isDark = isDark,
                                borderRadius = 18.dp,
                                elevation = 3.dp,
                                shadowColor = themeColor.copy(alpha = 0.15f)
                            )
                            .border(
                                width = 1.dp,
                                color = themeColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.5).sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isHindi) "Threat Intel" else "Security Threat Intel",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = themeColor,
                            letterSpacing = 1.2.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 1. What is it?
            DetailSection(
                title = if (isHindi) "What is it?" else "What is it?",
                icon = Icons.Rounded.Info,
                color = themeColor
            ) {
                Text(
                    text = if (isHindi) category.whatIsItHi else category.whatIsItEn,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // 2. How does it work?
            DetailSection(
                title = if (isHindi) "How does it work?" else "How does it work?",
                icon = Icons.Rounded.Settings,
                color = themeColor
            ) {
                val list = if (isHindi) category.howItWorksHi else category.howItWorksEn
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    list.forEachIndexed { index, step ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(20.dp)
                                    .background(
                                        color = themeColor.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeColor
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                style = TextStyle(
                                    fontSize = 13.5.sp,
                                    lineHeight = 18.sp,
                                    color = textSecondary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 3. Common Warning Signs (Red Flags)
            DetailSection(
                title = if (isHindi) "Warning Signs" else "Common Warning Signs",
                icon = Icons.Rounded.Warning,
                color = PremiumColors.Danger
            ) {
                val list = if (isHindi) category.warningSignsHi else category.warningSignsEn
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    list.forEach { sign ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Report,
                                contentDescription = null,
                                tint = PremiumColors.Danger,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = sign,
                                style = TextStyle(
                                    fontSize = 13.5.sp,
                                    lineHeight = 18.sp,
                                    color = textSecondary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 4. How to Stay Safe
            DetailSection(
                title = if (isHindi) "Stay Safe Tips" else "How to Stay Safe",
                icon = Icons.Rounded.Security,
                color = Color(0xFF10B981) // safe green
            ) {
                val list = if (isHindi) category.howToStaySafeHi else category.howToStaySafeEn
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    list.forEach { safeAction ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = safeAction,
                                style = TextStyle(
                                    fontSize = 13.5.sp,
                                    lineHeight = 18.sp,
                                    color = textSecondary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 5. Quick Summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PremiumRadius.card)
                    .background(
                        if (isDark) Color(0xFF111827) else Color(0xFFF8FAFC)
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)
                        ),
                        PremiumRadius.card
                    )
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Assignment,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "Quick Summary" else "Quick Summary",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textPrimary,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) category.quickSummaryHi else category.quickSummaryEn,
                            style = TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Close button at bottom
            BannerAdComposable()

            PremiumButton(
                onClick = onBack,
                text = if (isHindi) "Got It" else "Got It",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = LocalIsDark.current
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF111827) else Color.White
        ),
        shape = PremiumRadius.card,
        border = BorderStroke(
            1.dp,
            if (isDark) Color(0xFF1F2937) else Color(0xFFF1F5F9)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 0.2.sp
                    )
                )
            }
            content()
        }
    }
}
