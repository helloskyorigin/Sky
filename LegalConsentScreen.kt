package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.PremiumIconContainer
import com.example.ui.theme.blueGlow
import com.example.ui.theme.premiumShadow

@Composable
fun LegalConsentScreen(
    onAccept: () -> Unit,
    onExitApp: () -> Unit, // Retained signature for compatibility, not used in UI
    onNavigateToDoc: (String) -> Unit, // Retained signature for compatibility, we use uriHandler directly
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val uriHandler = LocalUriHandler.current
    
    // Core design system tokens
    val primaryBlue = Color(0xFF2563EB)
    val textPrimary = if (isDark) MaterialTheme.colorScheme.onBackground else Color(0xFF0F172A)
    val textSecondary = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF475569)
    val bgColor = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF8FAFC)
    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFE2E8F0)

    // Use current default locale to determine language dynamically
    val isHindi = java.util.Locale.getDefault().language == "hi"

    // Smooth screen entrance animations
    val animAlpha = remember { Animatable(0f) }
    val animScale = remember { Animatable(0.97f) }
    
    LaunchedEffect(Unit) {
        animAlpha.animateTo(1f, tween(500, easing = EaseOutCubic))
    }
    LaunchedEffect(Unit) {
        animScale.animateTo(1f, tween(500, easing = EaseOutCubic))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .alpha(animAlpha.value)
            .scale(animScale.value)
    ) {
        // Atmospheric radial glow at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // Adaptive height
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryBlue.copy(alpha = if (isDark) 0.08f else 0.03f),
                            Color.Transparent
                        )
                    )
                )
        )

        var termsAccepted by remember { mutableStateOf(false) }

        // Main content wrapper
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Consistent 16dp outer padding
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Scrollable content (Header, Cards, Checkbox Row)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp) // Reduced vertical spacing
                ) {
                    // Verified Shield Icon - Slightly reduced icon & container sizes
                    Box(
                        modifier = Modifier
                            .size(56.dp) // Reduced from 72dp
                            .background(primaryBlue.copy(alpha = 0.08f), CircleShape)
                            .border(1.5.dp, primaryBlue.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VerifiedUser,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(26.dp) // Reduced from 34dp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp)) // Reduced spacing

                    Text(
                        text = if (isHindi) "आगे बढ़ने से पहले" else "Before You Continue",
                        fontSize = 20.sp, // Reduced display typography for dense screens
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.3).sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isHindi) 
                            "कृपया अपनी सुरक्षा और गोपनीयता सुनिश्चित करने के लिए ऐप की नीतियों और शर्तों की समीक्षा करें।" 
                        else 
                            "Please review our app policies and terms to understand how we protect your messaging security.",
                        fontSize = 12.sp, // Reduced font size
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Legal Cards List (Reduced spacing and paddings)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp), // Reduced spacing by ~33%
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp) // Reduced padding
                ) {
                    // 1. Privacy Policy
                    LegalDocCardItem(
                        title = if (isHindi) "गोपनीयता नीति (Privacy Policy)" else "Privacy Policy",
                        description = if (isHindi) 
                            "हम आपके टेक्स्ट डेटा को स्थानीय रूप से और ऑफलाइन सुरक्षित रखते हैं।" 
                        else 
                            "Learn how we safely process and protect your message data locally and offline.",
                        icon = Icons.Rounded.Lock,
                        iconTint = primaryBlue,
                        onClick = {
                            onNavigateToDoc("privacy")
                        },
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        testTag = "legal_card_privacy",
                        isDark = isDark,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )

                    // 2. Terms of Use
                    LegalDocCardItem(
                        title = if (isHindi) "उपयोग की शर्तें (Terms of Use)" else "Terms of Use",
                        description = if (isHindi) 
                            "ThreatShield AI का उपयोग करते समय अपने अधिकारों और नियमों को समझें।" 
                        else 
                            "Understand your rights, rules, and guidelines when using ThreatShield AI.",
                        icon = Icons.Rounded.Description,
                        iconTint = Color(0xFF8B5CF6),
                        onClick = {
                            onNavigateToDoc("terms")
                        },
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        testTag = "legal_card_terms",
                        isDark = isDark,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )

                    // 3. Security Disclaimer
                    LegalDocCardItem(
                        title = if (isHindi) "अस्वीकरण (Disclaimer)" else "Security Disclaimer",
                        description = if (isHindi) 
                            "स्कैन परिणामों और सुरक्षा सीमाओं के बारे में महत्वपूर्ण सूचनाएं।" 
                        else 
                            "Important notifications about scan accuracy, results, and defense boundaries.",
                        icon = Icons.Rounded.Info,
                        iconTint = Color(0xFF0D9488),
                        onClick = {
                            onNavigateToDoc("disclaimer")
                        },
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        testTag = "legal_card_disclaimer",
                        isDark = isDark,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )

                    // 4. Contact Support
                    LegalDocCardItem(
                        title = if (isHindi) "संपर्क करें (Contact Us)" else "Contact Support",
                        description = if (isHindi) 
                            "किसी भी पूछताछ या सुरक्षा सहायता के लिए हमारी टीम से संपर्क करें।" 
                        else 
                            "Reach out to our security and support team for any inquiries or assistances.",
                        icon = Icons.Rounded.SupportAgent,
                        iconTint = Color(0xFFF59E0B),
                        onClick = {
                            onNavigateToDoc("contact")
                        },
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        testTag = "legal_card_contact",
                        isDark = isDark,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )
                }

                // Consent Checkbox (Scrolled together with other content)
                ConsentCheckboxRow(
                    text = if (isHindi) "मैंने सभी नीतियों को पढ़ लिया है और मैं नियम एवं शर्तों से सहमत हूँ।" else "I have read all policies and agree to the Terms & Conditions.",
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Fixed Bottom Continue Action Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onAccept,
                    enabled = termsAccepted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                        disabledContentColor = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) // Reduced height slightly for better screen fit
                        .then(
                            if (termsAccepted) Modifier.blueGlow(borderRadius = 16.dp, isDark = isDark, glowColor = primaryBlue)
                            else Modifier
                        )
                        .testTag("legal_consent_continue_button")
                ) {
                    Text(
                        text = if (isHindi) "स्वीकार करें और जारी रखें" else "Accept & Continue",
                        fontSize = 15.sp, // Slightly reduced
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LegalDocCardItem(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    cardBg: Color,
    cardBorder: Color,
    testTag: String,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
            .premiumShadow(isDark, borderRadius = 16.dp, elevation = if (isDark) 0.dp else 2.dp), // Reduced radius to match compact theme
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), // Reduced card internal padding (Rule 6)
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Style-matched, color-coded premium leading icon container - slightly reduced size
            PremiumIconContainer(
                icon = icon,
                tintColor = iconTint,
                containerColor = iconTint.copy(alpha = if (isDark) 0.16f else 0.08f),
                size = 36.dp, // Reduced from 46dp
                cornerRadius = 10.dp, // Reduced from 14dp
                iconSize = 16.dp // Reduced from 22dp (Rule 5)
            )

            Spacer(modifier = Modifier.width(12.dp)) // Reduced spacer from 16dp

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 13.5.sp, // Reduced title size
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp, // Reduced description size
                    color = textSecondary,
                    lineHeight = 14.sp // Compact line spacing
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Rounded.OpenInNew,
                contentDescription = "Open Webpage",
                tint = if (isDark) Color(0xFF475569) else Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp) // Reduced size
            )
        }
    }
}

@Composable
fun ConsentCheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryBlue: Color,
    textPrimary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val backgroundColor by animateColorAsState(
            targetValue = if (checked) primaryBlue else Color.Transparent,
            animationSpec = tween(300),
            label = "checkboxBg"
        )
        val borderColor by animateColorAsState(
            targetValue = if (checked) primaryBlue else Color.Gray.copy(alpha = 0.5f),
            animationSpec = tween(300),
            label = "checkboxBorder"
        )
        
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(backgroundColor, RoundedCornerShape(6.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = checked,
                enter = scaleIn(tween(200)) + fadeIn(tween(200)),
                exit = scaleOut(tween(200)) + fadeOut(tween(200))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            fontSize = 13.sp,
            color = textPrimary,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
