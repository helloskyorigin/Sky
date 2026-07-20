package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.ui.theme.premiumShadow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.ui.theme.PremiumIconContainer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val isDark = com.example.ui.theme.LocalIsDark.current
    val primaryBlue = com.example.ui.theme.PremiumColors.PrimaryAccent
    val textDark = MaterialTheme.colorScheme.onBackground
    val textGray = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368)
    val bgColor = MaterialTheme.colorScheme.background
    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF252932) else Color(0xFFDCE3EE)

    var isFirstLaunch_contentAlpha by rememberSaveable { mutableStateOf(true) }
    var last_contentAlpha by rememberSaveable { mutableFloatStateOf(0f) }
    val contentAlpha = remember { Animatable(last_contentAlpha) }
    LaunchedEffect(isFirstLaunch_contentAlpha) {
        if (isFirstLaunch_contentAlpha) {
            isFirstLaunch_contentAlpha = false
            contentAlpha.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic))
        } else {
            contentAlpha.snapTo(1f)
        }
        last_contentAlpha = 1f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("about_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = textDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .alpha(contentAlpha.value)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Premium App Logo Shield Glow Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_official_logo),
                    contentDescription = "ThreatShield AI Logo",
                    modifier = Modifier
                        .height(48.dp)
                        .drawBehind {
                            if (isDark) {
                                val glowRadius = size.height * 1.5f
                                drawCircle(
                                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF3B82F6).copy(alpha = 0.2f),
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
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Brand Titles
            Text(
                text = "ThreatShield AI",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textDark,
                    letterSpacing = (-0.5).sp
                )
            )

            Text(
                text = "Version 1.0.0 (Build 124)\nDeveloper: ThreatShield Team\n© 2026 ThreatShield AI",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = textGray,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // "Built for safety" badge
            Row(
                modifier = Modifier
                    .background(
                        color = if (isDark) Color(0xFF13281E) else Color(0xFFE6F4EA),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFF81C784) else Color(0xFF137333),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Built for digital safety",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFA5D6A7) else Color(0xFF137333)
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Feature summary banner
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .premiumShadow(isDark, 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Airtight Mobile Security",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ThreatShield AI is an intelligent guardian built to detect SMS scams, fake courier messages, phishing websites, and identity theft patterns. By combining localized rulesets and predictive logic, we provide lightning-fast, secure assessments.",
                            style = TextStyle(
                                fontSize = 13.5.sp,
                                color = textGray,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                // Section: Mission
                SectionTitle(title = "Our Mission")
                ParagraphText(
                    text = "We believe that digital security should be intuitive, accessible, and uncompromisingly private. Our mission is to protect individuals and vulnerable groups from modern social engineering and fraud before they suffer financial damage, keeping your communication circles secure."
                )

                // Section: Why It Exists
                SectionTitle(title = "Why ThreatShield AI?")
                ParagraphText(
                    text = "Phishing attempts grow more complex every day, causing billions of dollars in damage annually. Conventional security suites often scrape your private contacts or quiet chat logs to perform audits. ThreatShield AI offers a clean alternative: privacy-first threat evaluation with fully transparent, local sandboxed memory operations."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Core Capabilities Highlight Grid/Cards
                SectionTitle(title = "Core Capabilities")

                CapabilityCard(
                    icon = Icons.Rounded.Memory,
                    title = "Locality-First Scoring",
                    description = "We minimize external lookups by parsing core patterns directly on your Android hardware, protecting your conversations from server leak risks."
                )

                CapabilityCard(
                    icon = Icons.Rounded.Brush,
                    title = "Adaptive M3 Design",
                    description = "A responsive, colorful interface designed to transition smoothly from deep dark mode into system default configurations effortlessly."
                )

                CapabilityCard(
                    icon = Icons.Rounded.Verified,
                    title = "Verified Rulesets",
                    description = "Our database rules are curated to accurately flag high-urgency demands, suspicious bank URLs, and artificial support numbers."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Section: Developer & Support Metadata
                SectionTitle(title = "Developer & Support")

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumShadow(isDark, 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        SupportMetadataRow(
                            icon = Icons.Rounded.Business,
                            label = "Developer Group",
                            value = "ThreatShield AI Labs"
                        )
                        HorizontalDivider(
                            color = cardBorder,
                            thickness = 0.8.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        SupportMetadataRow(
                            icon = Icons.Rounded.Email,
                            label = "Support Email",
                            value = "support@threatshield.ai"
                        )
                        HorizontalDivider(
                            color = cardBorder,
                            thickness = 0.8.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        SupportMetadataRow(
                            icon = Icons.Rounded.Language,
                            label = "Official Web",
                            value = "https://threatshield.ai"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Copyright footer
                HorizontalDivider(color = cardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "© 2026 ThreatShield AI Suite Inc.\nAll rights reserved worldwide.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = textGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp)
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
    )
}

@Composable
fun CapabilityCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    val isDark = com.example.ui.theme.LocalIsDark.current
    val primaryBlue = com.example.ui.theme.PremiumColors.PrimaryAccent
    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF252932) else Color(0xFFDCE3EE)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .premiumShadow(isDark, 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            PremiumIconContainer(
                icon = icon,
                tintColor = Color(0xFF06B6D4), // Semantic Cyan for About/Info
                size = 36.dp,
                cornerRadius = 8.dp,
                iconSize = 18.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368),
                        lineHeight = 17.sp
                    )
                )
            }
        }
    }
}

@Composable
fun ParagraphText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 13.5.sp,
            color = if (com.example.ui.theme.LocalIsDark.current) Color(0xFF9AA0A6) else Color(0xFF5F6368),
            lineHeight = 20.sp
        ),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun SupportMetadataRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = com.example.ui.theme.PremiumColors.PrimaryAccent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
        Text(
            text = value,
            style = TextStyle(
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = com.example.ui.theme.PremiumColors.PrimaryAccent
            )
        )
    }
}
