#!/bin/bash
cat << 'INNER_EOF' > app/src/main/java/com/example/HomeScreen.kt
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.PremiumColors

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

    val isDark = LocalIsDark.current
    val primaryBlue = PremiumColors.PrimaryAccent
    val textPrimary = if (isDark) Color.White else Color(0xFF111111)
    val textSecondary = if (isDark) Color(0xFFA1A1AA) else Color(0xFF6E6E73)
    val bgColor = MaterialTheme.colorScheme.background
    val cardBg = if (isDark) Color(0xFF171A20) else Color.White
    val cardBorder = if (isDark) Color(0xFF252932) else Color(0xFFE5E7EB)

    val userInputText = viewModel.userInputText
    var inputText by remember { mutableStateOf(userInputText) }

    LaunchedEffect(userInputText) {
        if (userInputText != inputText) {
            inputText = userInputText
        }
    }

    var isFocused by remember { mutableStateOf(false) }

    // Animations
    val fadeAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
    }

    Scaffold(
        modifier = modifier.fillMaxSize().alpha(fadeAnim.value),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Scanner",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Analyze suspicious messages",
                            fontSize = 13.sp,
                            color = primaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    val shareIconColor = if (inputText.isNotBlank()) primaryBlue else textSecondary.copy(alpha = 0.5f)
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val shareText = "Scam Shield AI Message Preview:\n\n$inputText\n\nPlease review this message for scam indicators."
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Message")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Main Hero Input Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, if (isFocused) primaryBlue else cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 220.dp, max = 260.dp)
                    ) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Paste your suspicious SMS, WhatsApp, Telegram or Email message here...\n\nExample:\nCongratulations! You won $50,000.\nClick here to claim your prize.",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    color = textSecondary.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                            )
                        }
                        BasicTextField(
                            value = inputText,
                            onValueChange = {
                                inputText = it
                                viewModel.userInputText = it
                            },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = textPrimary
                            ),
                            cursorBrush = SolidColor(primaryBlue),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .androidx.compose.ui.focus.onFocusChanged { state ->
                                    isFocused = state.isFocused
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = cardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${inputText.length} characters",
                            fontSize = 12.sp,
                            color = textSecondary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (inputText.isNotEmpty()) {
                                TextButton(
                                    onClick = {
                                        inputText = ""
                                        viewModel.userInputText = ""
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Clear", color = textSecondary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            
                            FilledTonalButton(
                                onClick = {
                                    try {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clipData = clipboard.primaryClip
                                        if (clipData != null && clipData.itemCount > 0) {
                                            val text = clipData.getItemAt(0).text?.toString()
                                            if (!text.isNullOrBlank()) {
                                                inputText = text
                                                viewModel.userInputText = text
                                                Toast.makeText(context, "Pasted successfully", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Failed to paste", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = primaryBlue.copy(alpha = 0.15f),
                                    contentColor = primaryBlue
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Rounded.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Paste", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sample Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SampleChip("Bank Scam", "Update your secure banking profile to avoid suspension of your debit card. Click here: http://sbi-secure-update.net/login", Modifier.weight(1f)) { sample ->
                    inputText = sample
                    viewModel.userInputText = sample
                }
                SampleChip("Lottery Scam", "Congratulations! You won a $1,000 Walmart Gift Card. Click here to claim your reward instantly: http://scamlink.com/reward", Modifier.weight(1f)) { sample ->
                    inputText = sample
                    viewModel.userInputText = sample
                }
                SampleChip("Safe Message", "System Notification: Your weekly security backup was completed successfully.", Modifier.weight(1f)) { sample ->
                    inputText = sample
                    viewModel.userInputText = sample
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Analyze Button
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scaleByPress by animateFloatAsState(if (isPressed) 0.96f else 1f)

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        focusManager.clearFocus()
                        onNavigateToLoading(inputText)
                    }
                },
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .scale(scaleByPress),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue,
                    contentColor = Color.White,
                    disabledContainerColor = if (isDark) Color(0xFF2D3748) else Color(0xFFE2E8F0),
                    disabledContentColor = if (isDark) Color(0xFF4A5568) else Color(0xFF94A3B8)
                ),
                interactionSource = interactionSource
            ) {
                Icon(Icons.Rounded.Security, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Analyze Message", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Coming Soon Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ComingSoonCard("URL Scanner", Icons.Rounded.Link, Color(0xFF8B5CF6), cardBg, cardBorder, textPrimary, textSecondary, Modifier.weight(1f))
                ComingSoonCard("Image Scanner", Icons.Rounded.Image, Color(0xFFEC4899), cardBg, cardBorder, textPrimary, textSecondary, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Icon(Icons.Rounded.PrivacyTip, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Your data is analyzed securely on this device.", fontSize = 12.sp, color = textSecondary)
            }
        }
    }
}

@Composable
fun SampleChip(label: String, sampleText: String, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    val isDark = LocalIsDark.current
    Surface(
        onClick = { onClick(sampleText) },
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF252932) else Color(0xFFF3F4F6),
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) Color.White else Color(0xFF111111))
        }
    }
}

@Composable
fun ComingSoonCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Coming Soon", color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}
INNER_EOF
