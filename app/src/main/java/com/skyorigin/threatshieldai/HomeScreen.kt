package com.skyorigin.threatshieldai

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QuickAction(
    val label: String,
    val sampleText: String,
    val icon: ImageVector,
    val color: Color
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
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val bgColor = if (isDark) Color(0xFF0F172A) else Color.White
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val primaryBlue = Color(0xFF2563EB)

    val userInputText = viewModel.userInputText
    var inputText by remember { mutableStateOf(userInputText) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var isFocused by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var showLimitReachedDialog by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var showServiceUnavailableDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userInputText) {
        if (userInputText != inputText) {
            inputText = userInputText
        }
    }

    LaunchedEffect(viewModel.shouldFocusInput) {
        if (viewModel.shouldFocusInput) {
            delay(400)
            focusRequester.requestFocus()
            keyboardController?.show()
            viewModel.shouldFocusInput = false
        }
    }

    val isHindi = false

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    
    val safeAction = QuickAction(
        label = if (isHindi) "Safe" else "Safe", 
        sampleText = "Hi, the meeting has been moved to 3 PM today. Please confirm if the new time works for you. See you at the office.", 
        icon = Icons.Rounded.CheckCircle, 
        color = Color(0xFF10B981)
    )
    val suspiciousAction = QuickAction(
        label = if (isHindi) "Suspicious" else "Suspicious", 
        sampleText = "Hi, we found an issue with your account. Please contact our support team soon to confirm your details and avoid disruption.", 
        icon = Icons.Rounded.Warning, 
        color = Color(0xFFF59E0B)
    )
    val dangerAction = QuickAction(
        label = if (isHindi) "Danger" else "Danger", 
        sampleText = "Urgent: Your bank account will be blocked today. Verify your OTP and banking details immediately using this link: http://claim-prize-now.net", 
        icon = Icons.Rounded.Error, 
        color = Color(0xFFEF4444)
    )

    val templateChips = listOf(
        QuickAction(if (isHindi) "UPI Scam" else "UPI Scam", "You have received a pending refund request of ₹4,999 from Google Pay. Click here to claim your money back instantly: https://gpay-refund-portal.in", Icons.Rounded.QrCodeScanner, Color(0xFF10B981)),
        QuickAction(if (isHindi) "Bank Scam" else "Bank Scam", "Update your secure banking profile to avoid suspension of your debit card. Click here: http://sbi-secure-update.net/login", Icons.Rounded.AccountBalance, Color(0xFF3B82F6)),
        QuickAction(if (isHindi) "Lottery Scam" else "Lottery Scam", "Congratulations! You won ₹50,000 Cash Prize. Click here to claim: https://threat-shield-scam-reward.net/claim before tonight.", Icons.Rounded.CardGiftcard, Color(0xFFEF4444)),
        QuickAction(if (isHindi) "OTP Scam" else "OTP Scam", "Alert: A password reset request has been received. Please share the 6-digit OTP code with our helpdesk executive to verify.", Icons.Rounded.VpnKey, Color(0xFFF59E0B))
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ThreatShield AI",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.3).sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 2. Protection Status Hero
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Shield,
                            contentDescription = "Protected",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Protection Active",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = if (isHindi) "Scams से सुरक्षा चालू है।" else "Your device is being monitored for scams.",
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Primary Scan Input Area
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, if (isFocused) primaryBlue else cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Paste Suspicious Message",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textPrimary
                    )
                    Text(
                        text = if (isHindi) "किसी suspicious message को यहां paste करके check करें।" else "SMS, WhatsApp, Email, or Social Media",
                        fontSize = 12.sp,
                        color = textSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(bgColor, RoundedCornerShape(16.dp))
                            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { newValue ->
                                val acceptedValue = if (newValue.length > 500) {
                                    newValue.substring(0, 500)
                                } else {
                                    newValue
                                }
                                inputText = acceptedValue
                                viewModel.userInputText = acceptedValue
                            },
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = textPrimary,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .focusRequester(focusRequester)
                                .onFocusChanged { isFocused = it.isFocused },
                            cursorBrush = SolidColor(primaryBlue)
                        )
                        
                        if (inputText.isEmpty()) {
                            Text(
                                text = if (isHindi) "यहाँ message paste करें..." else "Paste message here...",
                                color = textSecondary.copy(alpha = 0.6f),
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val meaningfulLength = inputText.trim().length
                        if (meaningfulLength < 15) {
                            Text(
                                text = if (isHindi) "Analysis के लिए कम से कम 15 characters लिखें।" else "Enter at least 15 characters to analyze.",
                                color = if (inputText.isNotBlank()) Color(0xFFEF4444) else textSecondary.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Text(
                            text = "${inputText.length}/500",
                            color = textSecondary.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val remainingScans = viewModel.remainingScansState.value
                    val isLimitReached = remainingScans <= 0
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                try {
                                    val clipData = clipboard.primaryClip
                                    if (clipData != null && clipData.itemCount > 0) {
                                        val text = clipData.getItemAt(0).text?.toString()
                                        if (!text.isNullOrBlank()) {
                                            val acceptedVal = if (text.length > 500) text.substring(0, 500) else text
                                            inputText = acceptedVal
                                            viewModel.userInputText = acceptedVal
                                        }
                                    }
                                } catch (e: Exception) {}
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Rounded.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp), tint = textSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Paste", fontSize = 14.sp, color = textSecondary, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                if (isLimitReached) {
                                    showLimitReachedDialog = true
                                } else {
                                    scope.launch {
                                        isAnalyzing = true
                                        focusManager.clearFocus()
                                        
                                        if (!NetworkUtils.isInternetAvailable(context)) {
                                            isAnalyzing = false
                                            viewModel.showNoInternetDialog = true
                                            return@launch
                                        }
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
                            enabled = (inputText.trim().length in 15..500) && !isAnalyzing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("Analyze Message", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Sample Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(safeAction, suspiciousAction, dangerAction).forEach { action ->
                    Surface(
                        onClick = {
                            inputText = action.sampleText
                            viewModel.userInputText = action.sampleText
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = action.color.copy(alpha = 0.1f),
                        modifier = Modifier.weight(1f).height(72.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(action.icon, contentDescription = null, tint = action.color, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(action.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = action.color)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Template / Shortcut Chips
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                    Text(
                        text = "Quick Analysis Templates",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textPrimary,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    templateChips.forEach { chip ->
                        Surface(
                            onClick = {
                                inputText = chip.sampleText
                                viewModel.userInputText = chip.sampleText
                            },
                            shape = RoundedCornerShape(100.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, cardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(chip.icon, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(chip.label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Recent Analysis / Result Preview
            val recentAnalysis = viewModel.analysesHistory.firstOrNull()
            if (recentAnalysis != null) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Recent Analysis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textPrimary,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                    
                    val riskColor = when (recentAnalysis.status.lowercase()) {
                        "safe" -> Color(0xFF10B981)
                        "suspicious" -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    }
                    
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToHistory() }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(riskColor.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${recentAnalysis.score}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = riskColor
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = recentAnalysis.status,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = riskColor
                                    )
                                    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                                    val dateStr = dateFormat.format(Date(recentAnalysis.timestamp))
                                    Text(
                                        text = dateStr,
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recentAnalysis.text,
                                    fontSize = 13.sp,
                                    color = textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showLimitReachedDialog) {
        val activity = LocalContext.current as? android.app.Activity
        AlertDialog(
            onDismissRequest = { showLimitReachedDialog = false },
            title = { Text(if (isHindi) "और स्कैन चाहिए?" else "Need More Scans?", fontWeight = FontWeight.Bold) },
            text = { Text(if (isHindi) "आज की फ्री लिमिट पूरी हो गई है। +2 मैसेज स्कैन अनलॉक करने के लिए एक छोटा स्पॉन्सर्ड वीडियो देखें।" else "You've reached today's free limit. Watch one short sponsored video to instantly unlock +2 Message Scans.") },
            confirmButton = {
                TextButton(onClick = {
                    showLimitReachedDialog = false
                    activity?.let {
                        RewardedAdManager.showRewardedAd(
                            activity = it,
                            onRewardEarned = {
                                viewModel.remainingScans += 2
                            },
                            onAdDismissed = { rewardEarned ->
                                // ad dismissed
                            },
                            onAdFailedToShow = {
                                android.widget.Toast.makeText(context, "Failed to load ad. Try again.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }) {
                    Text(if (isHindi) "वीडियो देखें (+2 स्कैन)" else "Watch Ad to Get +2 Scans", color = primaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLimitReachedDialog = false }) {
                    Text(if (isHindi) "बाद में" else "Maybe Later", color = textSecondary)
                }
            }
        )
    }
    
    if (showServiceUnavailableDialog) {
        AlertDialog(
            onDismissRequest = { showServiceUnavailableDialog = false },
            title = { Text("Service Unavailable", fontWeight = FontWeight.Bold) },
            text = { Text(if (isHindi) "Scanning service अभी unavailable है। कृपया बाद में प्रयास करें।" else "The scanning service is temporarily unavailable. Please try again later.") },
            confirmButton = {
                TextButton(onClick = { showServiceUnavailableDialog = false }) {
                    Text("OK", color = primaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
