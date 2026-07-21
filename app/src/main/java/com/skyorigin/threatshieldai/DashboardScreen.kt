package com.skyorigin.threatshieldai
import androidx.compose.material.icons.automirrored.rounded.ArrowForward

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build
import android.Manifest

import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.skyorigin.threatshieldai.ui.theme.premiumShadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.skyorigin.threatshieldai.ui.theme.PremiumTypography
import com.skyorigin.threatshieldai.ui.theme.blueGlow
import com.skyorigin.threatshieldai.ui.theme.PremiumColors
import com.skyorigin.threatshieldai.ui.theme.PremiumIconContainer

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val isDark = LocalIsDark.current
    val colors = if (isDark) {
        listOf(
            Color(0xFF1E222B),
            Color(0xFF2A2E37),
            Color(0xFF1E222B)
        )
    } else {
        listOf(
            Color(0xFFDCE3EE),
            Color(0xFFF1F5F9),
            Color(0xFFDCE3EE)
        )
    }

    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    return this.background(brush)
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: ScamLensViewModel,
    onNavigateToScan: () -> Unit = {},
    onNavigateToResult: (MessageAnalysis) -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToDailyChallenge: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDark = LocalIsDark.current

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (!viewModel.notifOnboardingShown) {
            viewModel.notifOnboardingShown = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    // Core Premium Design Tokens
    val primaryBlue = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB)
    val textPrimary = if (isDark) Color(0xFFFFFFFF) else Color(0xFF111827)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val bgColor = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorderColor = MaterialTheme.colorScheme.outlineVariant
    
    val dangerRed = if (isDark) Color(0xFFEF4444) else Color(0xFFDC2626)
    val warningOrange = Color(0xFFF59E0B)
    val successGreen = Color(0xFF22C55E)

    // Animations
    var isFirstLaunchDash by rememberSaveable { mutableStateOf(true) }
    var lastContentAlpha by rememberSaveable { mutableFloatStateOf(0f) }
    var lastSlideUp by rememberSaveable { mutableFloatStateOf(40f) }
    val contentAlpha = remember { Animatable(lastContentAlpha) }
    val slideUp = remember { Animatable(lastSlideUp) }
    
    LaunchedEffect(isFirstLaunchDash) {
        if (isFirstLaunchDash) {
            isFirstLaunchDash = false
            launch { contentAlpha.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic)) }
            launch { slideUp.animateTo(0f, animationSpec = tween(500, easing = EaseOutCubic)) }
        } else {
            contentAlpha.snapTo(1f)
            slideUp.snapTo(0f)
        }
        lastContentAlpha = 1f
        lastSlideUp = 0f
    }

    val history = viewModel.analysesHistory

    // Modal and Dialog states
    var showLearnScamGuide by remember { mutableStateOf(false) }
    var showReportScamDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(bgColor)) {
        // Top right subtle radial glow
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension * 0.8f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryBlue.copy(alpha = if (isDark) 0.12f else 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.05f),
                    radius = radius
                ),
                radius = radius,
                center = Offset(size.width * 0.9f, size.height * 0.05f)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha.value)
                .offset(y = slideUp.value.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Premium Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_official_logo),
                                contentDescription = "ThreatShield AI Logo",
                                modifier = Modifier.height(24.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                            )
                            Text(
                                text = "ThreatShield AI",
                                style = TextStyle(
                                    color = textPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                        }
                        Text(
                            text = if (viewModel.currentLanguage == "hi") "Autonomous AI Cyber Defense" else "Autonomous AI Cyber Defense",
                            style = TextStyle(
                                color = textSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.5.sp,
                                letterSpacing = 0.2.sp
                            )
                        )
                    }

                    // Settings icon on the right
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .background(cardBg, RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, cardBorderColor), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. New Premium Security Hero Section Card (replaces previous multi-card overview)
            item {
                val isHindi = false
                
                // Infinite breathing pulsing state for active dot
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .premiumShadow(isDark, 28.dp, elevation = if (isDark) 12.dp else 6.dp)
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(Color(0xFF3B82F6).copy(alpha = 0.35f), Color(0xFF1E1B4B).copy(alpha = 0.05f))
                    } else {
                        listOf(Color(0xFF3B82F6).copy(alpha = 0.18f), Color(0xFFDBEAFE).copy(alpha = 0.05f))
                    }
                ),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = if (isDark) {
                                        listOf(Color(0xFF0C101A), Color(0xFF111827), Color(0xFF141230))
                                    } else {
                                        listOf(Color(0xFFF0F6FF), Color(0xFFF8FAFC), Color(0xFFEFF6FF))
                                    }
                                )
                            )
                            .padding(20.dp)
                    ) {
                        // Header Status Row (Live protection indicator)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Badge with breathing green pulse
                            Row(
                                modifier = Modifier
                                    .background(
                                        color = successGreen.copy(alpha = if (isDark) 0.14f else 0.08f),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                                    .border(
                                        BorderStroke(1.dp, successGreen.copy(alpha = if (isDark) 0.3f else 0.15f)),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .alpha(pulseAlpha)
                                        .background(successGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "REAL-TIME PROTECTION ACTIVE" else "REAL-TIME PROTECTION ACTIVE",
                                    style = TextStyle(
                                        color = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.6.sp
                                    )
                                )
                            }
                            
                            // Minimal threat database count or shield icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "v1",
                                    style = TextStyle(
                                        color = textSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(
                                    text = if (isHindi) "Scammers से हमेशा एक कदम आगे रहें" else "Stay One Step Ahead of Scammers",
                                    style = TextStyle(
                                        color = textPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        letterSpacing = (-0.5).sp,
                                        lineHeight = 25.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (isHindi) 
                                        "Phishing, fraud और suspicious links का तुरंत पता लगाने के लिए advance AI का use करें।" 
                                    else 
                                        "AI-powered shield scanning and protecting you from phishing, fraud & social engineering.",
                                    style = TextStyle(
                                        color = textSecondary,
                                        fontSize = 12.5.sp,
                                        lineHeight = 17.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                            
                            // Futuristic glowing shield core
                            Box(
                                modifier = Modifier
                                        .size(80.dp)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    primaryBlue.copy(alpha = if (isDark) 0.15f else 0.08f),
                                                    Color.Transparent
                                                )
                                            ),
                                            CircleShape
                                        )
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        primaryBlue.copy(alpha = 0.25f),
                                                        Color.Transparent
                                                    )
                                                )
                                            ),
                                            CircleShape
                                        ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    primaryBlue.copy(alpha = if (isDark) 0.2f else 0.1f),
                                                    primaryBlue.copy(alpha = if (isDark) 0.05f else 0.02f)
                                                )
                                            ),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .border(
                                            BorderStroke(1.dp, primaryBlue.copy(alpha = 0.35f)),
                                            RoundedCornerShape(14.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {}
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large primary Scan button with gradient and soft glow
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val buttonScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.96f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
                            label = "scan_button_scale"
                        )
                        val buttonGradient = if (isDark) {
                            Brush.horizontalGradient(colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)))
                        } else {
                            Brush.horizontalGradient(colors = listOf(Color(0xFF2563EB), Color(0xFF3B82F6)))
                        }

                        Button(
                            onClick = onNavigateToScan,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .scale(buttonScale)
                                .blueGlow(borderRadius = 16.dp, glowColor = primaryBlue.copy(alpha = if (isDark) 0.4f else 0.25f))
                                .testTag("hero_scan_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(),
                            interactionSource = interactionSource
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(buttonGradient)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.QrCodeScanner,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHindi) "नया Message Analyze करें" else "Analyze Message Now",
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = (-0.1).sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Thin separator line
                        HorizontalDivider(color = cardBorderColor.copy(alpha = if (isDark) 0.3f else 0.5f))
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        // Status details row inside card footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Memory,
                                    contentDescription = null,
                                    tint = textSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isHindi) "Threat Check Processor Active" else "Threat Check Processor Active",
                                    style = TextStyle(
                                        color = textSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(primaryBlue, CircleShape)
                                )
                                Text(
                                    text = if (isHindi) "Link Verify: On" else "Link Verify: On",
                                    style = TextStyle(
                                        color = textSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }


            // Protection Status Card (Security Health Profile)
            item {
                ProtectionStatusCard(viewModel = viewModel)
            }

            // Security Overview Card (Live statistics)
            item {
                SecurityOverviewCard(viewModel = viewModel)
            }

            // 3. Quick Actions Header
            item {
                val isHindi = false
                Text(
                    text = if (isHindi) "Quick Actions" else "Quick Actions",
                    style = PremiumTypography.SectionTitle.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        fontSize = 15.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp)
                )
            }

            // 4. Quick Actions 2x2 Grid of Square Cards
            item {
                val isHindi = false
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PremiumSquareActionCard(
                            title = if (isHindi) "Analyze Message" else "Analyze Message",
                            subtitle = if (isHindi) "Text या Link जांचें" else "Check text or link",
                            icon = Icons.Rounded.Shield,
                            tintColor = primaryBlue,
                            onClick = onNavigateToScan,
                            badgeText = if (isHindi) "सक्रिय" else "ACTIVE",
                            badgeColor = successGreen,
                            modifier = Modifier.weight(1f)
                        )
                        PremiumSquareActionCard(
                            title = if (isHindi) "Analysis History" else "Analysis History",
                            subtitle = if (isHindi) "पिछला एनालिसिस" else "Previous analysis",
                            icon = Icons.Rounded.History,
                            tintColor = successGreen,
                            onClick = onNavigateToHistory,
                            badgeText = if (isHindi) "सक्रिय" else "ACTIVE",
                            badgeColor = successGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PremiumSquareActionCard(
                            title = if (isHindi) "URL Analyzer" else "URL Analyzer",
                            subtitle = if (isHindi) "Coming soon v1.1" else "Coming soon v1.1",
                            icon = Icons.Rounded.Link,
                            tintColor = Color(0xFF8B5CF6),
                            badgeText = "V1.1",
                            onClick = {
                                android.widget.Toast.makeText(
                                    context,
                                    if (isHindi) "URL Analyzer v1.1 जल्द आ रहा है!" else "URL Analyzer v1.1 is coming soon!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        PremiumSquareActionCard(
                            title = if (isHindi) "Image Analysis" else "Image Analysis",
                            subtitle = if (isHindi) "Coming soon v1.1" else "Coming soon v1.1",
                            icon = Icons.Rounded.Image,
                            tintColor = Color(0xFFEC4899),
                            badgeText = "v1.1",
                            badgeColor = Color(0xFFF59E0B),
                            onClick = {
                                android.widget.Toast.makeText(
                                    context,
                                    if (isHindi) "इमेज एनालिसिस v1.1 जल्द आ रहा है!" else "Image Analysis v1.1 is coming soon!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 5. Recent Scans Header
            item {
                val isHindi = false
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "हालिया एनालिसिस" else "Recent Analyses",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            fontSize = 15.sp,
                            letterSpacing = (-0.2).sp
                        )
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(primaryBlue, CircleShape)
                        )
                        Text(
                            text = if (isHindi) "LIVE FEED" else "LIVE FEED",
                            style = TextStyle(
                                color = primaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }

            // 6. Recent Scans Items
            if (viewModel.isHistoryLoading) {
                items(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerEffect()
                    )
                }
            } else if (history.isEmpty()) {
                item {
                    val isHindi = false
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .premiumShadow(isDark, 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = textSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHindi) "कोई हालिया एनालिसिस नहीं" else "No Recent Analyses",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isHindi) 
                                    "Fraud indicators की जांच करने के लिए Messages को Analyze करें।" 
                                else 
                                    "Analyze messages to check for fraudulent scam indicators.",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            } else {
                items(history.take(3)) { item ->
                    RecentScanListItem(
                        item = item,
                        isHindi = viewModel.currentLanguage == "hi",
                        onClick = { onNavigateToResult(item) },
                        successGreen = successGreen,
                        warningOrange = warningOrange,
                        dangerRed = dangerRed,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorderColor = cardBorderColor
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(2.dp))
                    ViewAllButtonRow(
                        onClick = onNavigateToHistory,
                        primaryBlue = primaryBlue,
                        cardBorderColor = cardBorderColor
                    )
                }
            }
        }

        // --- Dialogs ---
        
        // 1. Report Scam Dialog
        if (showReportScamDialog) {
            val isHindi = false
            AlertDialog(
                onDismissRequest = { showReportScamDialog = false },
                shape = RoundedCornerShape(24.dp),
                containerColor = cardBg,
                tonalElevation = 6.dp,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Report,
                        contentDescription = null,
                        tint = dangerRed,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = if (isHindi) "Scam की Report करें" else "Report Scam",
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isHindi)
                                "Cyber Crime Portal या authorities को malicious activities की report करें। आप हमारी global Threat Intelligence Desk को भी Scam Report भेज सकते हैं।"
                            else
                                "Submit suspicious numbers and scam patterns anonymously to our global Threat Intelligence desk or file a complaint with your local cyber defense cell.",
                            color = textPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(
                            modifier = Modifier
                                .background(primaryBlue.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .border(BorderStroke(1.dp, primaryBlue.copy(alpha = 0.15f)), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "threat-reports@threatshield.ai",
                                color = primaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            try {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("ThreatShield Report Desk", "threat-reports@threatshield.ai")
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, if (isHindi) "Email Address कॉपी हो गया!" else "Email address copied!", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                // Fallback
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(if (isHindi) "Email Copy करें" else "Copy Email", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showReportScamDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = textSecondary)
                    ) {
                        Text(if (isHindi) "Close" else "Close", fontWeight = FontWeight.Medium)
                    }
                }
            )
        }

        // 2. Learn Scam Prevention Guide Dialog
        if (showLearnScamGuide) {
            AlertDialog(
                onDismissRequest = { showLearnScamGuide = false },
                shape = RoundedCornerShape(24.dp),
                containerColor = cardBg,
                tonalElevation = 6.dp,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.VerifiedUser,
                            contentDescription = null,
                            tint = successGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scam Prevention Guide",
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ScamGuideItem(
                            scamType = "Phishing / Smishing",
                            description = "Fake SMS links purporting to be postal delivery updates, banking blocks, or secure system notifications. Always verify sender address.",
                            icon = Icons.Rounded.Link,
                            iconColor = Color(0xFF8B5CF6)
                        )
                        ScamGuideItem(
                            scamType = "Lottery / Win Scams",
                            description = "Unexpected notifications declaring congratulations for winning expensive gift cards or jackpots. High urgency is a primary signal of scamming.",
                            icon = Icons.Rounded.CardGiftcard,
                            iconColor = warningOrange
                        )
                        ScamGuideItem(
                            scamType = "Impersonation",
                            description = "Impostors posing as tax agents, electric company support, or bank staff requesting urgent wire transfers or immediate action.",
                            icon = Icons.Rounded.SupportAgent,
                            iconColor = dangerRed
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = cardBorderColor)
                        
                        Text(
                            text = "General Checklist:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = textPrimary
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            BulletRow(text = "Verify phone numbers independently.", textSecondary = textSecondary)
                            BulletRow(text = "Do not share passwords or one-time codes (OTP).", textSecondary = textSecondary)
                            BulletRow(text = "Never copy-paste sensitive credentials on requested links.", textSecondary = textSecondary)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showLearnScamGuide = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = primaryBlue)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun ScamGuideItem(
    scamType: String,
    description: String,
    icon: ImageVector,
    iconColor: Color
) {
    val isDark = LocalIsDark.current
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(iconColor.copy(alpha = if (isDark) 0.16f else 0.08f), RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, iconColor.copy(alpha = 0.2f)), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = scamType,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isDark) Color.White else Color(0xFF111111)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA1A1AA) else Color(0xFF6E6E73),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun BulletRow(text: String, textSecondary: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(PremiumColors.PrimaryAccent, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 11.sp, color = textSecondary)
    }
}

data class DailyScanStats(
    val dayLabel: String,
    val safe: Int,
    val suspicious: Int,
    val danger: Int
)

@Composable
fun AnimatedSecurityCard(
    delayMs: Int = 0,
    content: @Composable () -> Unit
) {
    var isVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = EaseOutCubic),
        label = "fade"
    )
    val translationY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 25f,
        animationSpec = tween(300, easing = EaseOutCubic),
        label = "slide"
    )
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.97f,
        animationSpec = tween(300, easing = EaseOutCubic),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer(
                alpha = alpha,
                translationY = translationY,
                scaleX = scale,
                scaleY = scale
            )
    ) {
        content()
    }
}

@Composable
fun SmallCircularProgressIndicator(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val trackColor = if (isDark) Color(0xFF2E333F) else Color(0xFFDCE3EE)
    
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var lastSeenProgress by rememberSaveable { mutableFloatStateOf(0f) }
    val animProgressAnim = remember { Animatable(lastSeenProgress) }
    
    LaunchedEffect(progress, isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            delay(100)
            animProgressAnim.animateTo(progress, tween(1000, easing = EaseOutCubic))
        } else {
            animProgressAnim.animateTo(progress, tween(1000, easing = EaseOutCubic))
        }
        lastSeenProgress = progress
    }
    val animProgress = animProgressAnim.value

    Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 2.5.dp.toPx()
        val diameter = size.width - strokeWidth
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
        val arcSize = size.copy(width = diameter, height = diameter)

        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth)
        )

        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * animProgress,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PremiumStatCard(
    count: Int,
    label: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    trendText: String,
    progress: Float,
    delayMs: Int,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val cardBgColor = if (isDark) {
        accentColor.copy(alpha = 0.08f)
    } else {
        accentColor.copy(alpha = 0.04f)
    }
    val cardBorderColor = accentColor.copy(alpha = if (isDark) 0.22f else 0.12f)
    val textPrimary = if (isDark) Color(0xFFFFFFFF) else Color(0xFF111111)
    val textSecondary = if (isDark) Color(0xFFA1A1AA) else Color(0xFF6E6E73)

    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var lastSeenCount by rememberSaveable { mutableIntStateOf(0) }
    val animatedCountAnim = remember { Animatable(lastSeenCount, Int.VectorConverter) }
    
    LaunchedEffect(count, isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            delay(delayMs.toLong() + 100) // sync with AnimatedSecurityCard appearance
            animatedCountAnim.animateTo(count, tween(1000, easing = EaseOutCubic))
        } else {
            animatedCountAnim.animateTo(count, tween(1000, easing = EaseOutCubic))
        }
        lastSeenCount = count
    }
    val animatedCount = animatedCountAnim.value

    AnimatedSecurityCard(delayMs = delayMs) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            border = BorderStroke(1.dp, cardBorderColor),
            
            modifier = modifier.fillMaxWidth().premiumShadow(isDark, 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(accentColor.copy(alpha = if (isDark) 0.16f else 0.08f), RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
                                .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)), RoundedCornerShape(100.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = trendText,
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                            )
                        }

                        SmallCircularProgressIndicator(
                            progress = progress,
                            color = accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$animatedCount",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = label,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        ),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}

@Composable
fun ChartLegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (LocalIsDark.current) Color(0xFFA1A1AA) else Color(0xFF6E6E73)
        )
    }
}

@Composable
fun PremiumHeroCardRedesigned(
    todayCount: Int,
    safeCount: Int,
    suspiciousCount: Int,
    dangerCount: Int,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorderColor: Color,
    successGreen: Color
) {
    val isDark = LocalIsDark.current
    val warningOrange = Color(0xFFF59E0B)
    val dangerRed = Color(0xFFEF4444)
    val safeGreen = Color(0xFF22C55E)
    
    val totalCount = safeCount + suspiciousCount + dangerCount

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // A. Title Header for analytics
        Text(
            text = "Security Summary",
            style = PremiumTypography.SectionTitle.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 2.dp)
        )

        // B. Three Premium Statistics Cards
        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val isWideScreen = configuration.screenWidthDp > 600

        if (isWideScreen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumStatCard(
                    count = safeCount,
                    label = "Safe",
                    description = "Verified secure, risk-free scans.",
                    icon = Icons.Rounded.CheckCircle,
                    accentColor = safeGreen,
                    trendText = if (safeCount > 0) "↑ Safe +${12 + (safeCount % 5)}%" else "Clear",
                    progress = if (totalCount > 0) safeCount.toFloat() / totalCount else 1.0f,
                    delayMs = 0,
                    modifier = Modifier.weight(1f)
                )
                PremiumStatCard(
                    count = suspiciousCount,
                    label = "Suspicious",
                    description = "Potential spam or weak phishing.",
                    icon = Icons.Rounded.Warning,
                    accentColor = warningOrange,
                    trendText = if (suspiciousCount > 0) "↓ Scam -${5 + (suspiciousCount % 3)}%" else "Clear",
                    progress = if (totalCount > 0) suspiciousCount.toFloat() / totalCount else 0.0f,
                    delayMs = 100,
                    modifier = Modifier.weight(1f)
                )
                PremiumStatCard(
                    count = dangerCount,
                    label = "Dangerous",
                    description = "High-risk threats blocked.",
                    icon = Icons.Rounded.GppBad,
                    accentColor = dangerRed,
                    trendText = if (dangerCount > 0) "↑ High Alert" else "No threats",
                    progress = if (totalCount > 0) dangerCount.toFloat() / totalCount else 0.0f,
                    delayMs = 200,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumStatCard(
                    count = safeCount,
                    label = "Safe Messages",
                    description = "Verified secure, risk-free scans.",
                    icon = Icons.Rounded.CheckCircle,
                    accentColor = safeGreen,
                    trendText = if (safeCount > 0) "↑ Safe +${12 + (safeCount % 5)}%" else "Clear",
                    progress = if (totalCount > 0) safeCount.toFloat() / totalCount else 1.0f,
                    delayMs = 0
                )
                PremiumStatCard(
                    count = suspiciousCount,
                    label = "Suspicious Messages",
                    description = "Potential spam or weak phishing.",
                    icon = Icons.Rounded.Warning,
                    accentColor = warningOrange,
                    trendText = if (suspiciousCount > 0) "↓ Scam -${5 + (suspiciousCount % 3)}%" else "Clear",
                    progress = if (totalCount > 0) suspiciousCount.toFloat() / totalCount else 0.0f,
                    delayMs = 100
                )
                PremiumStatCard(
                    count = dangerCount,
                    label = "Dangerous Messages",
                    description = "High-risk threats blocked.",
                    icon = Icons.Rounded.GppBad,
                    accentColor = dangerRed,
                    trendText = if (dangerCount > 0) "↑ High Alert" else "No threats",
                    progress = if (totalCount > 0) dangerCount.toFloat() / totalCount else 0.0f,
                    delayMs = 200
                )
            }
        }

        // C. Today's Security Insight Card
        val insightText = when {
            dangerCount > 0 -> "Two suspicious messages require attention."
            dangerCount == 0 && suspiciousCount > 0 -> "$suspiciousCount suspicious messages require attention."
            safeCount > 0 -> "Excellent! Your recent activity looks safe."
            else -> "No dangerous messages detected today."
        }

        AnimatedSecurityCard(delayMs = 300) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, primaryBlue.copy(alpha = 0.3f)),
                
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .premiumShadow(isDark, 18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(primaryBlue.copy(alpha = if (isDark) 0.16f else 0.08f), CircleShape)
                            .border(BorderStroke(1.dp, primaryBlue.copy(alpha = 0.25f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Today's Security Insight",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryBlue,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = insightText,
                            style = TextStyle(
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )
                        )
                    }
                }
            }
        }

        // D. Overall Protection Status Card
        val overallStatusText = when {
            dangerCount > 0 -> "High Risk"
            suspiciousCount > 0 -> "Needs Attention"
            todayCount == 0 -> "Good Protection"
            else -> "Excellent Protection"
        }
        
        val overallStatusColor = when {
            dangerCount > 0 -> dangerRed
            suspiciousCount > 0 -> warningOrange
            todayCount == 0 -> primaryBlue
            else -> safeGreen
        }
        
        val overallStatusIcon = when {
            dangerCount > 0 -> Icons.Rounded.GppBad
            suspiciousCount > 0 -> Icons.Rounded.Warning
            todayCount == 0 -> Icons.Rounded.Shield
            else -> Icons.Rounded.VerifiedUser
        }
        
        val overallStatusDesc = when {
            dangerCount > 0 -> "Immediate attention required! High-risk threats detected on device."
            suspiciousCount > 0 -> "Review suspicious activity immediately. Shield is active."
            todayCount == 0 -> "No analyses run today. Run a fresh analysis to ensure complete security."
            else -> "Your system is secure. ThreatShield AI is monitoring active channels."
        }

        AnimatedSecurityCard(delayMs = 400) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorderColor),
                
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .premiumShadow(isDark, 18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(overallStatusColor.copy(alpha = if (isDark) 0.16f else 0.08f), RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, overallStatusColor.copy(alpha = 0.2f)), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = overallStatusIcon,
                            contentDescription = null,
                            tint = overallStatusColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = overallStatusText,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = overallStatusDesc,
                            style = TextStyle(
                                fontSize = 11.5.sp,
                                color = textSecondary,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProtectionScoreCard(
    dangerCount: Int,
    suspiciousCount: Int,
    safeCount: Int,
    primaryBlue: Color,
    successGreen: Color,
    warningOrange: Color,
    dangerRed: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorderColor: Color
) {
    val isDark = LocalIsDark.current
    
    // Dynamic Score Calculation
    val scoreVal = maxOf(30, 100 - (dangerCount * 15) - (suspiciousCount * 8))
    
    val scoreColor = when {
        scoreVal >= 90 -> successGreen
        scoreVal >= 70 -> warningOrange
        else -> dangerRed
    }
    
    val scoreLabel = when {
        scoreVal >= 90 -> "Excellent"
        scoreVal >= 70 -> "Warning"
        else -> "Needs Attention"
    }
    
    val scoreDesc = when {
        scoreVal >= 90 -> "Your device currently appears safe."
        scoreVal >= 70 -> "Some suspicious activities detected on device."
        else -> "High-risk threats detected. Please review immediately."
    }

    // Dynamic animated score progress bar
    val targetScoreProgress = scoreVal / 100f
    var isFirstLaunchScore by rememberSaveable { mutableStateOf(true) }
    var lastSeenScoreProgress by rememberSaveable { mutableFloatStateOf(0f) }
    val scoreAnimatedValueAnim = remember { Animatable(lastSeenScoreProgress) }
    
    LaunchedEffect(targetScoreProgress, isFirstLaunchScore) {
        if (isFirstLaunchScore) {
            isFirstLaunchScore = false
            delay(100)
            scoreAnimatedValueAnim.animateTo(targetScoreProgress, tween(1000, easing = EaseOutCubic))
        } else {
            scoreAnimatedValueAnim.animateTo(targetScoreProgress, tween(1000, easing = EaseOutCubic))
        }
        lastSeenScoreProgress = targetScoreProgress
    }
    val scoreAnimatedValue = scoreAnimatedValueAnim.value

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorderColor),
        
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .premiumShadow(isDark, 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Protection Score",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$scoreVal",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = scoreColor
                        )
                    )
                    Text(
                        text = "/100",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(scoreColor.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                            .border(BorderStroke(1.dp, scoreColor.copy(alpha = 0.2f)), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = scoreLabel,
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Premium custom thick progress bar track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = if (isDark) Color(0xFF252932) else Color(0xFFDCE3EE),
                        shape = RoundedCornerShape(100.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(scoreAnimatedValue)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(scoreColor, scoreColor.copy(alpha = 0.7f))
                            ),
                            shape = RoundedCornerShape(100.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = scoreDesc,
                style = TextStyle(
                    fontSize = 11.5.sp,
                    color = textSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun SecurityTipCard(
    tipText: String,
    onLearnMore: () -> Unit,
    onNextTip: () -> Unit,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorderColor: Color
) {
    val isDark = LocalIsDark.current
    val isHindi = tipText.any { it.code in 0x0900..0x097F }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .premiumShadow(isDark, 24.dp, elevation = if (isDark) 0.dp else 3.dp)
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryBlue.copy(alpha = if (isDark) 0.03f else 0.01f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color(0xFFFDE68A)), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isHindi) "आज का सुरक्षा सुझाव" else "Today's Security Tip",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.2).sp
                        )
                    )
                    Text(
                        text = if (isHindi) "दैनिक सुरक्षा अंतर्दृष्टि" else "Daily security insight",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Text Tip content animated implicitly on tip index shift
            AnimatedContent(
                targetState = tipText,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "tip_transition"
            ) { targetTip ->
                Text(
                    text = targetTip,
                    style = TextStyle(
                        fontSize = 13.5.sp,
                        color = textPrimary.copy(alpha = 0.9f),
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Space-Between Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Next Tip Button
                Surface(
                    onClick = onNextTip,
                    shape = RoundedCornerShape(12.dp),
                    color = textSecondary.copy(alpha = if (isDark) 0.12f else 0.06f),
                    border = BorderStroke(1.dp, textSecondary.copy(alpha = 0.2f)),
                    modifier = Modifier.clickable(onClick = onNextTip)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "अगला सुझाव" else "Next Tip",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary.copy(alpha = 0.8f)
                            )
                        )
                        Icon(
                            imageVector = Icons.Rounded.NavigateNext,
                            contentDescription = null,
                            tint = textPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Learn More Button
                Surface(
                    onClick = onLearnMore,
                    shape = RoundedCornerShape(12.dp),
                    color = primaryBlue.copy(alpha = if (isDark) 0.15f else 0.08f),
                    border = BorderStroke(1.dp, primaryBlue.copy(alpha = 0.2f)),
                    modifier = Modifier.clickable(onClick = onLearnMore)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "और जानें" else "Learn More",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryBlue
                            )
                        )
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentScanListItem(
    item: MessageAnalysis,
    isHindi: Boolean = false,
    onClick: () -> Unit,
    successGreen: Color,
    warningOrange: Color,
    dangerRed: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorderColor: Color
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDark = LocalIsDark.current
    
    val statusColor = when (item.status) {
        "Safe" -> successGreen
        "Suspicious" -> warningOrange
        else -> dangerRed
    }

    val statusIcon = when (item.status) {
        "Safe" -> Icons.Rounded.Shield
        "Suspicious" -> Icons.Rounded.Warning
        else -> Icons.Rounded.GppBad
    }

    val badgeBg = statusColor.copy(alpha = if (isDark) 0.15f else 0.08f)
    val badgeBorder = statusColor.copy(alpha = if (isDark) 0.3f else 0.15f)

    val parts = item.text.split(":", limit = 2)
    val titleText = if (parts.size > 1) parts[0].trim() else if (isHindi) "एनालिसिस परिणाम" else "Analysis Result"
    val bodyText = if (parts.size > 1) parts[1].trim() else item.text.trim()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorderColor),
        
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .premiumShadow(isDark, 24.dp, elevation = if (isDark) 0.dp else 3.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(statusColor.copy(alpha = if (isDark) 0.12f else 0.06f), RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, statusColor.copy(alpha = if (isDark) 0.3f else 0.15f)), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleText,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        letterSpacing = (-0.2).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = bodyText,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = textSecondary,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, badgeBorder), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.getLocalizedStatus(isHindi),
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = statusColor,
                                letterSpacing = 0.2.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = DateTimeUtils.getRelativeTime(context, item.timestamp, isHindi),
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Open Analysis details",
                    tint = textSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ViewAllButtonRow(onClick: () -> Unit, primaryBlue: Color, cardBorderColor: Color) {
    val isDark = LocalIsDark.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
            .border(
                1.dp,
                cardBorderColor,
                RoundedCornerShape(100.dp)
            )
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "View All Activity",
            style = TextStyle(
                color = primaryBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = primaryBlue,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
fun EmptyStateCompact(
    primaryBlue: Color, 
    textPrimary: Color, 
    textSecondary: Color, 
    onAction: () -> Unit
) {
    val isDark = LocalIsDark.current
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorderColor = MaterialTheme.colorScheme.outlineVariant

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorderColor),
        
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .premiumShadow(isDark, 24.dp, elevation = if (isDark) 0.dp else 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(primaryBlue.copy(alpha = 0.05f), CircleShape)
                )
                Icon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    contentDescription = null,
                    tint = primaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "No recent activity",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    fontSize = 13.5.sp
                )
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Analyze a message to test protection status.",
                style = TextStyle(
                    color = textSecondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Start Analysis",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold, 
                        color = Color.White,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun PremiumComingSoonSectionRedesigned(
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorderColor: Color
) {
    val isDark = LocalIsDark.current
    
    val infiniteTransition = rememberInfiniteTransition(label = "coming_soon_breathing_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Coming Soon",
            style = PremiumTypography.SectionTitle.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
        )
        Text(
            text = "More advanced protection shields are in production.",
            style = TextStyle(
                color = textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ComingSoonCard(
                modifier = Modifier.weight(1f),
                title = "URL Analyzer",
                description = "Analyze links in real time",
                icon = Icons.Rounded.Link,
                badgeText = "WEB",
                primaryBlue = primaryBlue,
                glowAlpha = glowAlpha,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                cardBg = cardBg,
                cardBorderColor = cardBorderColor
            )
            
            ComingSoonCard(
                modifier = Modifier.weight(1f),
                title = "Image Analyzer",
                description = "Extract & analyze screenshots",
                icon = Icons.Rounded.Image,
                badgeText = "VISION",
                primaryBlue = primaryBlue,
                glowAlpha = glowAlpha,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                cardBg = cardBg,
                cardBorderColor = cardBorderColor
            )
        }
    }
}

@Composable
fun ComingSoonCard(
    modifier: Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    badgeText: String,
    primaryBlue: Color,
    glowAlpha: Float,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorderColor: Color
) {
    val isDark = LocalIsDark.current
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = tween(150, easing = EaseOutCubic),
        label = "coming_soon_press"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorderColor),
        
        modifier = modifier
            .scale(scale)
            .premiumShadow(isDark, 24.dp, elevation = if (isDark) 0.dp else 2.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = {}
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(modifier = Modifier.size(70.dp).align(Alignment.TopEnd)) {
                drawCircle(
                    color = primaryBlue.copy(alpha = 0.03f * glowAlpha),
                    radius = size.width,
                    center = Offset(size.width, 0f)
                )
            }

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val iconColor = when (title) {
                        "URL Analyzer" -> Color(0xFF8B5CF6)
                        "Image Analyzer" -> Color(0xFFEC4899)
                        else -> primaryBlue
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(iconColor.copy(alpha = if (isDark) 0.16f else 0.08f), RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, iconColor.copy(alpha = 0.2f)), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                    }
                    
                    Box(
                        modifier = Modifier
                            .background(
                                color = primaryBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = primaryBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = TextStyle(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryBlue,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = description,
                    style = TextStyle(
                        fontSize = 10.5.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    chipText: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorderColor = MaterialTheme.colorScheme.outlineVariant
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(100, easing = EaseOutCubic),
        label = "premium_action_press"
    )

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorderColor),
        interactionSource = interactionSource,
        modifier = modifier
            .height(130.dp)
            .scale(scale)
            .premiumShadow(isDark, 24.dp, elevation = if (isDark) 0.dp else 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            iconColor.copy(alpha = if (isDark) 0.08f else 0.04f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = iconColor.copy(alpha = if (isDark) 0.18f else 0.1f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = iconColor.copy(alpha = if (isDark) 0.3f else 0.15f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = textSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        color = textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        lineHeight = 15.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun DailyChallengeCard(
    isCompleted: Boolean,
    streakCount: Int,
    isHindi: Boolean,
    cardBg: Color,
    cardBorderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    primaryBlue: Color,
    successGreen: Color,
    onButtonClick: () -> Unit
) {
    val isDark = LocalIsDark.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "challenge_card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .scale(scale)
            .premiumShadow(isDark, 24.dp, elevation = if (isDark) 0.dp else 3.dp)
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Rounded.VerifiedUser else Icons.Rounded.WorkspacePremium,
                        contentDescription = null,
                        tint = if (isCompleted) successGreen else primaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "आज का स्कैम चैलेंज" else "Today's Scam Challenge",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.3).sp
                        )
                    )
                }

                if (streakCount > 0) {
                    Row(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFF5E3A), Color(0xFFFF9500))
                                ),
                                shape = RoundedCornerShape(100.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$streakCount ${if (isHindi) "दिन" else "d"}",
                            style = TextStyle(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isCompleted) {
                    if (isHindi) "✅ चैलेंज पूरा हुआ!" else "✅ Challenge Completed"
                } else {
                    if (isHindi) "क्या आप आज के घोटाले की पहचान कर सकते हैं?" else "Can you identify today's scam?"
                },
                style = TextStyle(
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) successGreen else textPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isCompleted) {
                    if (isHindi) "नया चैलेंज अनलॉक होने के लिए कल वापस आएं।" else "Come back tomorrow for a new challenge."
                } else {
                    if (isHindi) "एक त्वरित चुनौती में अपनी घोटाले की जागरूकता का परीक्षण करें।" else "Test your scam awareness in one quick challenge."
                },
                style = TextStyle(
                    fontSize = 12.5.sp,
                    color = textSecondary,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) successGreen.copy(alpha = 0.1f) else primaryBlue,
                    contentColor = if (isCompleted) successGreen else Color.White
                ),
                border = if (isCompleted) BorderStroke(1.dp, successGreen.copy(alpha = 0.3f)) else null,
                contentPadding = PaddingValues()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Rounded.AutoAwesome else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = if (isCompleted) successGreen else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCompleted) {
                            if (isHindi) "परिणाम देखें" else "See Result"
                        } else {
                            if (isHindi) "चैलेंज शुरू करें" else "Start Challenge"
                        },
                        style = TextStyle(
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumSquareActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tintColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String = "",
    badgeColor: Color? = null
) {
    val isDark = LocalIsDark.current
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorderColor = MaterialTheme.colorScheme.outlineVariant
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(100),
        label = "action_card_scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .premiumShadow(isDark, 24.dp, elevation = if (isDark) 0.dp else 3.dp)
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(tintColor.copy(alpha = if (isDark) 0.16f else 0.08f), RoundedCornerShape(10.dp))
                        .border(BorderStroke(1.dp, tintColor.copy(alpha = 0.2f)), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tintColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.2).sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = textSecondary,
                            lineHeight = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (badgeText.isNotEmpty()) {
                val effectiveBadgeColor = badgeColor ?: tintColor
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 12.dp)
                        .background(
                            color = effectiveBadgeColor.copy(alpha = if (isDark) 0.2f else 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            width = 0.8.dp,
                            color = effectiveBadgeColor.copy(alpha = if (isDark) 0.4f else 0.25f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = TextStyle(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = effectiveBadgeColor,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }
    }
}


