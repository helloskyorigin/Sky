package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import com.example.ui.theme.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: ScamLensViewModel,
    onBack: () -> Unit = {},
    onNavigateToResult: (MessageAnalysis) -> Unit = {}
) {
    val isDark = com.example.ui.theme.LocalIsDark.current
    val primaryBlue = com.example.ui.theme.PremiumColors.PrimaryAccent
    val textDark = MaterialTheme.colorScheme.onBackground
    val textGray = if (isDark) Color(0xFF9AA0A6) else Color(0xFF6E6E73)
    val bgColor = MaterialTheme.colorScheme.background
    
    var isFirstLaunch_listAlpha by rememberSaveable { mutableStateOf(true) }
    var last_listAlpha by rememberSaveable { mutableFloatStateOf(0f) }
    val listAlpha = remember { Animatable(last_listAlpha) }
    LaunchedEffect(isFirstLaunch_listAlpha) {
        if (isFirstLaunch_listAlpha) {
            isFirstLaunch_listAlpha = false
            listAlpha.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic))
        } else {
            listAlpha.snapTo(1f)
        }
        last_listAlpha = 1f
    }

    val isHindi = viewModel.currentLanguage == "hi"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {

                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                        text = if (isHindi) "Scan History" else "Scan History",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                    )
                }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = if (isHindi) "होम पर वापस जाएं" else "Back to Home",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .alpha(listAlpha.value)
        ) {
            val history = viewModel.analysesHistory
            var selectedFilter by remember { mutableStateOf("All") }

            val filteredHistory = when (selectedFilter) {
                "Safe" -> history.filter { it.status == "Safe" }
                "Danger" -> history.filter { it.status == "Danger" || it.status == "Suspicious" }
                else -> history
            }

            Column {
                // Current Protection Status Badge
                val protectionStatus = ProtectionStatusHelper.calculateStatus(history)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "वर्तमान सुरक्षा स्थिति" else "Current Protection Status",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textGray
                        )
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(
                                color = protectionStatus.color.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = protectionStatus.color.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(protectionStatus.color, CircleShape)
                            )
                            Text(
                                text = if (isHindi) {
                                    when (protectionStatus) {
                                        ProtectionStatus.EXCELLENT -> "उत्कृष्ट"
                                        ProtectionStatus.GOOD -> "अच्छा"
                                        ProtectionStatus.NEEDS_ATTENTION -> "ध्यान दें"
                                        ProtectionStatus.HIGH_RISK -> "उच्च जोखिम"
                                        ProtectionStatus.UNKNOWN -> "अज्ञात"
                                    }
                                } else {
                                    when (protectionStatus) {
                                        ProtectionStatus.EXCELLENT -> "Excellent"
                                        ProtectionStatus.GOOD -> "Good"
                                        ProtectionStatus.NEEDS_ATTENTION -> "Attention"
                                        ProtectionStatus.HIGH_RISK -> "High Risk"
                                        ProtectionStatus.UNKNOWN -> "Unknown"
                                    }
                                },
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF1E293B)
                                )
                            )
                        }
                    }
                }

                // Compact statistics summary at top of History screen
                val totalScans = history.size
                val scamCount = history.count { it.status.lowercase() == "danger" || it.status.lowercase() == "unsafe" }
                val safeCount = history.count { it.status.lowercase() == "safe" }
                val suspiciousCount = history.count { it.status.lowercase() == "suspicious" }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val statItems = listOf(
                        Triple("Total", totalScans, primaryBlue),
                        Triple("Scam", scamCount, if (isDark) Color(0xFFEF4444) else Color(0xFFDC2626)),
                        Triple("Safe", safeCount, Color(0xFF22C55E)),
                        Triple("Suspicious", suspiciousCount, Color(0xFFF59E0B))
                    )
                    statItems.forEach { (label, value, color) ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF161E2E) else Color(0xFFF1F5F9)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF252932) else Color(0xFFE2E8F0)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = value.toString(),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = color
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isHindi) {
                                        when (label) {
                                            "Total" -> "कुल"
                                            "Scam" -> "स्कैम"
                                            "Safe" -> "सुरक्षित"
                                            "Suspicious" -> "संदिग्ध"
                                            else -> label
                                        }
                                    } else label,
                                    style = TextStyle(
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textGray
                                    )
                                )
                            }
                        }
                    }
                }

                if (history.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Safe", "Danger").forEach { filter ->
                            val isSelected = selectedFilter == filter
                            val chipBg = if (isSelected) primaryBlue else if (isDark) Color(0xFF252932) else Color(0xFFDCE3EE)
                            val chipText = if (isSelected) Color.White else if (isDark) Color(0xFFA1A1AA) else Color(0xFF6E6E73)
                            
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = chipBg,
                                modifier = Modifier
                                    .clickable { selectedFilter = filter }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isHindi) {
                                        when (filter) {
                                            "All" -> "सभी"
                                            "Safe" -> "Safe"
                                            "Danger" -> "Danger"
                                            else -> filter
                                        }
                                    } else filter,
                                    color = chipText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                if (viewModel.isHistoryLoading) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .shimmerEffect()
                        )
                    }
                }
            } else if (filteredHistory.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_official_logo),
                            contentDescription = "Empty History Logo",
                            modifier = Modifier.fillMaxSize().alpha(0.3f),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(primaryBlue)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = if (isHindi) "कोई स्कैन इतिहास नहीं है।" else "No scan history yet.",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (isHindi) "अपना सुरक्षा इतिहास बनाने के लिए स्कैनिंग शुरू करें।" else "Start scanning to build your security history.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = textGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("history_list"),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    items(filteredHistory) { item ->
                        var showDeleteDialog by remember { mutableStateOf(false) }

                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = {
                                    Text(
                                        text = if (isHindi) "Delete History Entry?" else "Delete History Entry?",
                                        fontWeight = FontWeight.Bold,
                                        color = textDark
                                    )
                                },
                                text = {
                                    Text(
                                        text = if (isHindi) "क्या आप निश्चित रूप से इस Scan Record को अपने device से हटाना चाहते हैं?" else "Are you sure you want to remove this scan record from your device?",
                                        color = textGray,
                                        fontSize = 14.sp
                                    )
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            viewModel.deleteAnalysisResult(item)
                                            showDeleteDialog = false
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = Color(0xFFEF4444)
                                        )
                                    ) {
                                        Text(if (isHindi) "Delete" else "Delete", fontWeight = FontWeight.Bold)
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showDeleteDialog = false },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = textGray
                                        )
                                    ) {
                                        Text(if (isHindi) "Cancel" else "Cancel")
                                    }
                                },
                                containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White,
                                shape = RoundedCornerShape(20.dp)
                            )
                        }

                        HistoryCardItem(
                            item = item,
                            isHindi = isHindi,
                            onClick = {
                                onNavigateToResult(item)
                            },
                            onDelete = {
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
fun HistoryCardItem(
    item: MessageAnalysis,
    isHindi: Boolean = false,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDark = com.example.ui.theme.LocalIsDark.current
    val primaryBlue = com.example.ui.theme.PremiumColors.PrimaryAccent
    val textDark = MaterialTheme.colorScheme.onBackground
    val textGray = MaterialTheme.colorScheme.onSurfaceVariant
    val dangerRed = if (isDark) Color(0xFFEF4444).copy(alpha = 0.9f) else Color(0xFFEF4444)
    val warningOrange = if (isDark) Color(0xFFF59E0B).copy(alpha = 0.9f) else Color(0xFFF59E0B)
    val successGreen = if (isDark) Color(0xFF10B981).copy(alpha = 0.9f) else Color(0xFF10B981)
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBorder = MaterialTheme.colorScheme.outlineVariant

    com.example.ui.theme.PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_card_${item.score}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.text,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textDark
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = DateTimeUtils.formatHistoryTimestamp(context, item.timestamp),
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = textGray
                        )
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(textGray.copy(alpha = 0.5f), shape = CircleShape)
                    )
                    
                    Text(
                        text = if (isHindi) "जोखिम स्कोर: ${item.score}%" else "Match Score: ${item.score}%",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = textGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            val statusColor = when (item.status) {
                "Danger" -> dangerRed
                "Suspicious" -> warningOrange
                else -> successGreen
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = item.getLocalizedStatus(isHindi),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_history_item_${item.score}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = if (isHindi) "स्कैन इतिहास प्रविष्टि हटाएं" else "Delete scan history entry",
                        tint = if (isDark) Color(0xFFEF4444).copy(alpha = 0.8f) else Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
