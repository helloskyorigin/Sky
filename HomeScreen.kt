package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.PremiumColors
import com.example.ui.theme.premiumShadow
import androidx.compose.ui.layout.ContentScale

data class QuickAction(
    val label: String,
    val sampleText: String,
    val icon: ImageVector,
    val color: Color
)

data class SampleMessage(
    val id: String,
    val title: String,
    val severity: String,
    val message: String
)

val sampleMessages = listOf(
    SampleMessage(
        id = "safe_msg",
        title = "Safe",
        severity = "Safe",
        message = "Flipkart: Your order #9082347 has been delivered successfully. Thank you for shopping with us!"
    ),
    SampleMessage(
        id = "suspicious_msg",
        title = "Suspicious",
        severity = "Suspicious",
        message = "Special marketing discount: Receive free coupons for your next dining experience. Visit: https://bit.ly/dining-deals"
    ),
    SampleMessage(
        id = "danger_msg",
        title = "Danger",
        severity = "Danger",
        message = "SBI ALERT: Your netbanking account has been suspended due to unauthorized login. Please verify your details immediately at https://secure-sbi-login.com to unlock your card."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ScamLensViewModel,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToLoading: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val isDark = LocalIsDark.current
    val primaryBlue = PremiumColors.PrimaryAccent
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val bgColor = MaterialTheme.colorScheme.background
    
    // Glassmorphism inspired premium surfaces
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    val userInputText = viewModel.userInputText
    var inputText by remember { mutableStateOf(userInputText) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(userInputText) {
        if (userInputText != inputText) {
            inputText = userInputText
        }
    }

    LaunchedEffect(viewModel.shouldFocusInput) {
        if (viewModel.shouldFocusInput) {
            delay(400) // Wait for bottom tab navigation transition to settle
            focusRequester.requestFocus()
            keyboardController?.show()
            viewModel.shouldFocusInput = false
        }
    }

    var isFocused by remember { mutableStateOf(false) }
    var showTooLongDialog by remember { mutableStateOf(false) }
    var showLimitReachedDialog by remember { mutableStateOf(false) }
    var showRewardFailedMessage by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var showServiceUnavailableDialog by remember { mutableStateOf(false) }
    var showClearBottomSheet by remember { mutableStateOf(false) }
    var isExampleExpanded by remember { mutableStateOf(false) }

    // Dynamic scale and height configurations for smart auto-expanding input
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxInputHeight = screenHeight * 0.35f

    // Animations
    var isFirstLaunchHome by rememberSaveable { mutableStateOf(true) }
    var lastFade by rememberSaveable { mutableFloatStateOf(0f) }
    val fadeAnim = remember { Animatable(lastFade) }
    LaunchedEffect(isFirstLaunchHome) {
        if (isFirstLaunchHome) {
            isFirstLaunchHome = false
            fadeAnim.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
        } else {
            fadeAnim.snapTo(1f)
        }
        lastFade = 1f
    }

    // Clipboard checking logic
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var clipboardText by remember { mutableStateOf<String?>(null) }
    
    // Check clipboard text on screen load and when app returns to foreground
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                try {
                    val clipData = clipboard.primaryClip
                    if (clipData != null && clipData.itemCount > 0) {
                        val text = clipData.getItemAt(0).text?.toString()
                        if (!text.isNullOrBlank()) {
                            clipboardText = text
                        }
                    } else {
                        clipboardText = null
                    }
                } catch (e: Exception) {
                    clipboardText = null
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Also add a listener for changes while the app is in foreground
    DisposableEffect(clipboard) {
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            try {
                val clipData = clipboard.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    val text = clipData.getItemAt(0).text?.toString()
                    if (!text.isNullOrBlank()) {
                        clipboardText = text
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        clipboard.addPrimaryClipChangedListener(listener)
        onDispose {
            clipboard.removePrimaryClipChangedListener(listener)
        }
    }

    // Realistic Quick Action sample payloads
    val quickActionsList = remember {
        listOf(
            QuickAction(
                label = "UPI Scam",
                sampleText = "You have received a pending refund request of ₹4,999 from Google Pay. Click here to claim your money back instantly: https://gpay-refund-portal.in",
                icon = Icons.Rounded.QrCodeScanner,
                color = Color(0xFF10B981)
            ),
            QuickAction(
                label = "Bank Scam",
                sampleText = "Update your secure banking profile to avoid suspension of your debit card. Click here: http://sbi-secure-update.net/login",
                icon = Icons.Rounded.AccountBalance,
                color = Color(0xFF3B82F6)
            ),
            QuickAction(
                label = "Lottery Scam",
                sampleText = "Congratulations! You won ₹50,000 Cash Prize. Click here to claim: https://threat-shield-scam-reward.net/claim before tonight.",
                icon = Icons.Rounded.CardGiftcard,
                color = Color(0xFFEF4444)
            ),
            QuickAction(
                label = "Courier Scam",
                sampleText = "Your DHL package could not be delivered due to incorrect street number. Pay ₹35 to update: https://dhl-address-portal.com",
                icon = Icons.Rounded.LocalShipping,
                color = Color(0xFFEC4899)
            ),
            QuickAction(
                label = "Job Scam",
                sampleText = "Earn up to ₹8,000 daily working from home by rating maps. Secure registration. WhatsApp now: https://wa.me/919988776655",
                icon = Icons.Rounded.Work,
                color = Color(0xFF8B5CF6)
            ),
            QuickAction(
                label = "OTP Scam",
                sampleText = "Alert: A password reset request has been received. Please share the 6-digit OTP code with our helpdesk executive to verify.",
                icon = Icons.Rounded.VpnKey,
                color = Color(0xFFF59E0B)
            ),
            QuickAction(
                label = "Investment Scam",
                sampleText = "Double your wealth in 2 hours with our automated Crypto AI trading bot. Join the VIP group: https://t.me/crypto_double_vip",
                icon = Icons.Rounded.TrendingUp,
                color = Color(0xFF06B6D4)
            ),
            QuickAction(
                label = "Gift Scam",
                sampleText = "You have been selected to receive a free ₹10,000 Amazon Gift Voucher. Click here to verify your details and claim your reward today: https://amazon-voucher-claim.net",
                icon = Icons.Rounded.CardGiftcard,
                color = Color(0xFFF43F5E)
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize().alpha(fadeAnim.value),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.ic_official_logo),
                            contentDescription = "ThreatShield AI Logo",
                            modifier = Modifier.height(32.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ThreatShield AI",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = if (viewModel.currentLanguage == "hi") "AI Message Scanner" else "AI Message Scanner",
                                fontSize = 12.sp,
                                color = textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    val shareIconColor = if (inputText.isNotBlank()) primaryBlue else textSecondary.copy(alpha = 0.5f)
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val shareText = "ThreatShield AI Message Preview:\n\n$inputText\n\nPlease review this message for scam indicators."
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Message").apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(shareIntent)
                            }
                        },
                        enabled = inputText.isNotBlank()
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = "Share", tint = shareIconColor)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // ===================================
                // 2. PREMIUM INPUT CARD (ChatGPT style)
                // ===================================
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) cardBg.copy(alpha = 0.85f) else Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isFocused) primaryBlue.copy(alpha = 0.6f) else cardBorder.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumShadow(isDark, 28.dp)
                        .then(
                            if (isFocused) {
                                Modifier.border(
                                    2.dp,
                                    primaryBlue.copy(alpha = 0.2f),
                                    RoundedCornerShape(28.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Card Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(primaryBlue.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Shield,
                                        contentDescription = "Shield Icon",
                                        tint = primaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Paste Suspicious Message",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "Paste SMS, WhatsApp, Telegram, Email or Social Media",
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                }
                            }
                            
                            // CLEAR BUTTON (Trash Icon with confirmation dialog sheet)
                            AnimatedVisibility(
                                visible = inputText.isNotEmpty(),
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                IconButton(
                                    onClick = { showClearBottomSheet = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (isDark) Color(0xFF334155).copy(alpha = 0.4f) else Color(0xFFF1F5F9),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Clear",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // SMART EXPANDING INPUT
                        val scrollState = rememberScrollState()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = maxInputHeight)
                                .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
                        ) {
                            BasicTextField(
                                value = inputText,
                                onValueChange = {
                                    inputText = it
                                    viewModel.userInputText = it
                                    if (it.length > 1000) {
                                        showTooLongDialog = true
                                    }
                                },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = textPrimary
                                ),
                                cursorBrush = SolidColor(primaryBlue),
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .fillMaxWidth()
                                    .verticalScroll(scrollState)
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                                    .onFocusChanged { state ->
                                        isFocused = state.isFocused
                                    }
                            )

                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Type or paste a message here...",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        color = textSecondary.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                                )
                            }
                        }

                        // Try Sample Messages Section
                        val isHindiMode = viewModel.currentLanguage == "hi"
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindiMode) "सैंपल संदेश आज़माएं" else "Try Sample Messages",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = if (isHindiMode) "मैसेज भरने के लिए किसी सैंपल पर टैप करें" else "Tap any sample to instantly fill.",
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            }
                            
                            // Random Sample Button
                            TextButton(
                                onClick = {
                                    val randomSample = sampleMessages.random()
                                    inputText = randomSample.message
                                    viewModel.userInputText = randomSample.message
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Shuffle,
                                    contentDescription = "Random",
                                    modifier = Modifier.size(14.dp),
                                    tint = primaryBlue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindiMode) "रैंडम" else "Random",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sampleMessages.forEach { sample ->
                                val (badgeColor, bgColor) = when (sample.severity.lowercase()) {
                                    "safe" -> Color(0xFF10B981) to Color(0xFF10B981).copy(alpha = 0.08f)
                                    "suspicious" -> Color(0xFFF59E0B) to Color(0xFFF59E0B).copy(alpha = 0.08f)
                                    else -> Color(0xFFEF4444) to Color(0xFFEF4444).copy(alpha = 0.08f)
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(bgColor, RoundedCornerShape(12.dp))
                                        .border(1.dp, badgeColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            inputText = sample.message
                                            viewModel.userInputText = sample.message
                                        }
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(badgeColor, CircleShape)
                                        )
                                        Text(
                                            text = if (isHindiMode) {
                                                when (sample.severity.lowercase()) {
                                                    "safe" -> "सुरक्षित"
                                                    "suspicious" -> "संदेहास्पद"
                                                    else -> "खतरा"
                                                }
                                            } else {
                                                sample.title
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = cardBorder.copy(alpha = 0.5f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // BOTTOM ACTION BAR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // LEFT: CHARACTER COUNTER
                            val trimmedInput = inputText.trim()
                            val trimmedCount = trimmedInput.length
                            val isHindi = viewModel.currentLanguage == "hi"

                            val countColor = when {
                                trimmedCount < 25 -> {
                                    if (trimmedCount > 0) Color(0xFFEF4444) else textSecondary.copy(alpha = 0.8f)
                                }
                                trimmedCount > 1000 -> Color.Red
                                trimmedCount > 850 -> Color(0xFFF59E0B)
                                else -> Color(0xFF10B981) // Green for valid
                            }

                            val counterText = if (trimmedCount < 25) {
                                "$trimmedCount / 25"
                            } else {
                                "$trimmedCount / 1000 ✓"
                            }

                            Text(
                                text = counterText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = countColor
                            )

                            // CENTER: PASTE BUTTON (Pill-shaped)
                            // We make it more permissive: even if clipboardText is null, we check the real clipboard on click
                            val pasteEnabled = true 
                            val pasteInteractionSource = remember { MutableInteractionSource() }
                            val isPastePressed by pasteInteractionSource.collectIsPressedAsState()
                            val pasteScale by animateFloatAsState(if (isPastePressed) 0.94f else 1f, label = "pasteScale")

                            Button(
                                onClick = {
                                    try {
                                        val clipData = clipboard.primaryClip
                                        if (clipData != null && clipData.itemCount > 0) {
                                            val text = clipData.getItemAt(0).text?.toString()
                                            if (!text.isNullOrBlank()) {
                                                inputText = text
                                                viewModel.userInputText = text
                                                if (text.length > 1000) {
                                                    showTooLongDialog = true
                                                }
                                                Toast.makeText(context, "Pasted successfully", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Failed to paste", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = pasteEnabled,
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue.copy(alpha = 0.12f),
                                    contentColor = primaryBlue,
                                    disabledContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                    disabledContentColor = textSecondary.copy(alpha = 0.4f)
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                interactionSource = pasteInteractionSource,
                                modifier = Modifier
                                    .scale(pasteScale)
                                    .height(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentPaste,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Paste",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // RIGHT: PREMIUM SCAN MESSAGE BUTTON (Pill-shaped, gradient with smooth animations)
                            val remainingScans = viewModel.remainingScansState.value
                            val isLimitReached = remainingScans <= 0

                            val analyzeEnabled = trimmedCount in 25..1000 && !isAnalyzing
                            val analyzeInteractionSource = remember { MutableInteractionSource() }
                            val isAnalyzePressed by analyzeInteractionSource.collectIsPressedAsState()
                            val analyzeScale by animateFloatAsState(if (isAnalyzePressed) 0.94f else 1f, label = "analyzeScale")

                            val animatedAlpha by animateFloatAsState(
                                targetValue = if (analyzeEnabled) 1f else 0.5f,
                                animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic),
                                label = "analyzeAlpha"
                            )

                            val animatedScale by animateFloatAsState(
                                targetValue = if (analyzeEnabled && !isLimitReached) 1.02f else 1.0f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
                                label = "analyzeScaleSpring"
                            )

                            val analyzeBg = if (analyzeEnabled && !isLimitReached) {
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                                )
                            } else {
                                SolidColor(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                            }

                            val coroutineScope = rememberCoroutineScope()

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val limitText = if (isHindi) "AI स्कैन शेष\n$remainingScans / 3" else "AI Scans Remaining\n$remainingScans / 3"
                                Text(
                                    text = limitText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isLimitReached) Color(0xFFEF4444) else textSecondary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(end = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )

                                Box(
                                    modifier = Modifier
                                        .scale(analyzeScale * animatedScale)
                                        .alpha(animatedAlpha)
                                        .height(40.dp)
                                        .then(
                                            if (analyzeEnabled && !isLimitReached) {
                                                Modifier.background(analyzeBg, RoundedCornerShape(100.dp))
                                            } else {
                                                Modifier.background(
                                                    if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                                                    RoundedCornerShape(100.dp)
                                                )
                                            }
                                        )
                                        .clickable(enabled = analyzeEnabled && !isAnalyzing) {
                                            if (isLimitReached) {
                                                showLimitReachedDialog = true
                                            } else {
                                                coroutineScope.launch {
                                                    isAnalyzing = true
                                                    focusManager.clearFocus()
                                                    
                                                    val hasInternet = SecurityAnalysisEngine.isInternetAvailable(context)
                                                    if (!hasInternet) {
                                                        isAnalyzing = false
                                                        showNoInternetDialog = true
                                                        return@launch
                                                    }

                                                    try {
                                                        SecurityAnalysisEngine.checkApiHealth(context)
                                                        isAnalyzing = false
                                                        onNavigateToLoading(inputText.trim())
                                                    } catch (e: InternetConnectionException) {
                                                        isAnalyzing = false
                                                        showNoInternetDialog = true
                                                    } catch (e: Exception) {
                                                        isAnalyzing = false
                                                        showServiceUnavailableDialog = true
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (isAnalyzing) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isHindi) "विश्लेषण..." else "Analyzing...",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        } else {
                                            Icon(
                                                imageVector = if (isLimitReached) Icons.Rounded.Lock else Icons.Rounded.Shield,
                                                contentDescription = null,
                                                tint = if (isLimitReached) {
                                                    if (isDark) Color(0xFFEF4444) else Color(0xFFDC2626)
                                                } else if (analyzeEnabled) {
                                                    Color.White
                                                } else {
                                                    textSecondary.copy(alpha = 0.5f)
                                                },
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isLimitReached) {
                                                    if (isHindi) "सीमित" else "Limit Reached"
                                                } else {
                                                    if (isHindi) "स्कैन संदेश" else "Scan Message"
                                                },
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isLimitReached) {
                                                    if (isDark) Color(0xFFEF4444) else Color(0xFFDC2626)
                                                } else if (analyzeEnabled) {
                                                    Color.White
                                                } else {
                                                    textSecondary.copy(alpha = 0.6f)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ===================================
                // 5. QUICK ACTIONS
                // ===================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Quick Scan Templates",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textPrimary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickActionsList.forEach { action ->
                            Surface(
                                onClick = {
                                    inputText = action.sampleText
                                    viewModel.userInputText = action.sampleText
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(action.color.copy(alpha = 0.12f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = action.icon,
                                            contentDescription = null,
                                            tint = action.color,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = action.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ===================================
                // 3. EXAMPLE SECTION
                // ===================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Header with Collapse/Expand toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExampleExpanded = !isExampleExpanded }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Example Analysis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = textPrimary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Icon(
                            imageVector = if (isExampleExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = "Toggle Example",
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedVisibility(
                        visible = isExampleExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF131C2E) else Color(0xFFF8FAFC)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFFDBEAFE)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val exampleText = "⚠️ Congratulations!\nYou have won ₹50,000.\nClaim now:\nhttps://fake-link.example\nExpires today."
                                    inputText = exampleText
                                    viewModel.userInputText = exampleText
                                    Toast.makeText(context, "Example loaded!", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Example Scam Message",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
                                        )
                                    }

                                    Surface(
                                        color = Color(0xFFEF4444).copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Phishing,
                                                contentDescription = null,
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "PHISHING",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFEF4444)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "⚠ Congratulations!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "You have won ₹50,000.",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textPrimary,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .background(
                                                Color(0xFFEF4444).copy(alpha = 0.08f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Link,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "https://fake-link.example",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFEF4444),
                                            style = TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Expires today.",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textPrimary,
                                        lineHeight = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tap to load into editor",
                                        fontSize = 11.sp,
                                        color = primaryBlue.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "This is only an example.",
                                        fontSize = 11.sp,
                                        color = textSecondary.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ===================================
                // 8. COMING SOON CARDS
                // ===================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Advanced Shields",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textPrimary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ComingSoonCard(
                            title = "URL Scanner",
                            icon = Icons.Rounded.Link,
                            iconColor = Color(0xFF8B5CF6),
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        ComingSoonCard(
                            title = "Image Scanner",
                            icon = Icons.Rounded.Image,
                            iconColor = Color(0xFFEC4899),
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Inline Character Length Warning
                val isHindi = viewModel.currentLanguage == "hi"
                val trimmedCount = inputText.trim().length
                if (inputText.isNotEmpty() && trimmedCount < 25) {
                    val warningText = if (isHindi) {
                        "सटीक AI विश्लेषण के लिए कम से कम 25 अक्षर दर्ज करें।"
                    } else {
                        "Please enter at least 25 characters for an accurate AI analysis."
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDark) Color(0xFF3B82F6).copy(alpha = 0.08f) else Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF3B82F6).copy(alpha = 0.2f) else Color(0xFFBFDBFE)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .animateContentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = warningText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E3A8A),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
                ) {
                    Icon(Icons.Rounded.PrivacyTip, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Your data is analyzed securely on this device.", fontSize = 12.sp, color = textSecondary)
                }
            }
        }
    }

    if (showTooLongDialog) {
        val isHindi = viewModel.currentLanguage == "hi"
        AlertDialog(
            onDismissRequest = { showTooLongDialog = false },
            title = {
                Text(
                    text = if (isHindi) "Message बहुत लंबा है" else "Message Too Long",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = if (isHindi) {
                        "यह Message बहुत लंबा है। कृपया एक बार में अधिकतम 1000 Characters तक का Message Scan करें।"
                    } else {
                        "This message is too long. Please scan up to 1000 characters at a time."
                    },
                    color = textPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showTooLongDialog = false }
                ) {
                    Text(if (isHindi) "Edit करें" else "Edit Message")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        inputText = ""
                        viewModel.userInputText = ""
                        showTooLongDialog = false
                    }
                ) {
                    Text(if (isHindi) "Cancel" else "Cancel", color = textSecondary)
                }
            },
            containerColor = cardBg,
            titleContentColor = textPrimary,
            textContentColor = textPrimary
        )
    }

    if (showLimitReachedDialog) {
        val isHindi = viewModel.currentLanguage == "hi"
        val activity = context as? android.app.Activity
        AlertDialog(
            onDismissRequest = { showLimitReachedDialog = false },
            title = {
                Text(
                    text = if (isHindi) "🛡 AI स्कैन सीमा समाप्त" else "🛡 AI Scan Limit Reached",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = if (isHindi) {
                        "आपने अपने वर्तमान AI स्कैन का उपयोग कर लिया है।\n\n3 अतिरिक्त AI स्कैन तुरंत अनलॉक करने के लिए एक छोटा वीडियो विज्ञापन देखें।"
                    } else {
                        "You have used your current AI scans.\n\nWatch one short rewarded video to instantly unlock 3 additional AI scans."
                    },
                    color = textPrimary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLimitReachedDialog = false
                        if (activity != null) {
                            AdMobManager.showRewardedAd(
                                activity = activity,
                                onRewardEarned = {
                                    viewModel.remainingScans += 3
                                    showSuccessAnimation = true
                                },
                                onAdDismissed = { rewardEarned ->
                                    if (!rewardEarned) {
                                        showRewardFailedMessage = true
                                    }
                                },
                                onAdFailedToShow = {
                                    showRewardFailedMessage = true
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isHindi) "🎁 विज्ञापन देखें और अनलॉक करें" else "🎁 Watch Ad & Unlock", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showLimitReachedDialog = false }) {
                    Text(if (isHindi) "शायद बाद में" else "Maybe Later", color = textSecondary)
                }
            },
            containerColor = cardBg,
            titleContentColor = textPrimary,
            textContentColor = textPrimary
        )
    }

    if (showSuccessAnimation) {
        val isHindi = viewModel.currentLanguage == "hi"
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1500)
            showSuccessAnimation = false
        }
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = if (isHindi) "✅ 3 AI स्कैन अनलॉक" else "✅ 3 AI Scans Unlocked",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = if (isHindi) {
                        "आपके AI स्कैन सफलतापूर्वक जोड़ दिए गए हैं।\nसुरक्षित रूप से स्कैन करना जारी रखें।"
                    } else {
                        "Your AI scans have been added successfully.\nContinue scanning safely."
                    },
                    color = textPrimary
                )
            },
            confirmButton = { },
            containerColor = cardBg,
            titleContentColor = textPrimary,
            textContentColor = textPrimary
        )
    }

    if (showRewardFailedMessage) {
        val isHindi = viewModel.currentLanguage == "hi"
        AlertDialog(
            onDismissRequest = { showRewardFailedMessage = false },
            title = { Text(if (isHindi) "इनाम नहीं मिला" else "Reward not earned", color = textPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(if (isHindi) "अधिक स्कैन अनलॉक करने के लिए कृपया पूरा वीडियो देखें।" else "Please watch the full video to unlock more scans.", color = textPrimary) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { showRewardFailedMessage = false }) {
                    Text(if (isHindi) "ठीक है" else "OK")
                }
            },
            containerColor = cardBg,
            titleContentColor = textPrimary,
            textContentColor = textPrimary
        )
    }

    if (showNoInternetDialog) {
        val isHindi = viewModel.currentLanguage == "hi"
        AlertDialog(
            onDismissRequest = { showNoInternetDialog = false },
            icon = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Rounded.SignalWifiOff,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = if (isHindi) "कोई इंटरनेट कनेक्शन नहीं" else "No Internet Connection",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = if (isHindi) {
                        "थ्रेटशील्ड एआई को संदेशों का विश्लेषण करने के लिए इंटरनेट कनेक्शन की आवश्यकता है। कृपया अपना कनेक्शन जांचें और पुनः प्रयास करें।"
                    } else {
                        "ThreatShield AI requires an internet connection to analyze messages. Please check your connection and try again."
                    },
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hasInternet = SecurityAnalysisEngine.isInternetAvailable(context)
                        if (hasInternet) {
                            showNoInternetDialog = false
                            scope.launch {
                                isAnalyzing = true
                                try {
                                    SecurityAnalysisEngine.checkApiHealth(context)
                                    isAnalyzing = false
                                    onNavigateToLoading(inputText.trim())
                                } catch (e: Exception) {
                                    isAnalyzing = false
                                    showServiceUnavailableDialog = true
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isHindi) "पुनः प्रयास करें" else "Retry")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showNoInternetDialog = false }
                ) {
                    Text(if (isHindi) "रद्द करें" else "Cancel", color = textSecondary)
                }
            },
            containerColor = cardBg,
            titleContentColor = textPrimary,
            textContentColor = textPrimary
        )
    }

    if (showServiceUnavailableDialog) {
        val isHindi = viewModel.currentLanguage == "hi"
        AlertDialog(
            onDismissRequest = { showServiceUnavailableDialog = false },
            icon = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Rounded.CloudQueue,
                    contentDescription = null,
                    tint = Color(0xFFEAB308),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = if (isHindi) "सेवा अस्थायी रूप से अनुपलब्ध है" else "Service Temporarily Unavailable",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = if (isHindi) {
                        "हमें खेद है। हमारी एआई विश्लेषण सेवा अस्थायी रूप से अनुपलब्ध है। हमारी टीम इसे ठीक करने के लिए काम कर रही है। कृपया कुछ मिनटों में पुनः प्रयास करें।"
                    } else {
                        "We're sorry. Our AI analysis service is temporarily unavailable. Our team is working to restore it. Please try again in a few minutes."
                    },
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isAnalyzing = true
                            try {
                                val isHealthy = SecurityAnalysisEngine.checkApiHealth(context)
                                isAnalyzing = false
                                if (isHealthy) {
                                    showServiceUnavailableDialog = false
                                    onNavigateToLoading(inputText.trim())
                                }
                            } catch (e: Exception) {
                                isAnalyzing = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isHindi) "पुनः प्रयास करें" else "Retry")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showServiceUnavailableDialog = false }
                ) {
                    Text(if (isHindi) "ठीक है" else "OK", color = textSecondary)
                }
            },
            containerColor = cardBg,
            titleContentColor = textPrimary,
            textContentColor = textPrimary
        )
    }

    if (showClearBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showClearBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
            contentColor = textPrimary,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFEF4444).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Text(
                    text = "Clear Current Message?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textPrimary
                )
                
                Text(
                    text = "This will erase the current message in the scanner. This action cannot be undone.",
                    fontSize = 14.sp,
                    color = textSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel
                    OutlinedButton(
                        onClick = { showClearBottomSheet = false },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = textSecondary
                        ),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    // Clear Text
                    Button(
                        onClick = {
                            inputText = ""
                            viewModel.userInputText = ""
                            showClearBottomSheet = false
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Clear Text", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ComingSoonCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier
            .premiumShadow(isDark, 20.dp)
            .height(130.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                    }
                    
                    // Sparkle effect Icon
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = iconColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(title, color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Version 2.0", color = textSecondary.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
            
            // Blurred lock badge on top of the card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.05f))
                    .padding(8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Surface(
                    color = (if (isDark) Color(0xFF1E293B) else Color.White).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, (if (isDark) Color(0xFF334155) else Color(0xFFDCE3EE)).copy(alpha = 0.5f)),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Coming Soon",
                            tint = textSecondary,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "COMING SOON",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
