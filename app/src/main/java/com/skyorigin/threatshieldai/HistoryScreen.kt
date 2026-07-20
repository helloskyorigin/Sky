package com.skyorigin.threatshieldai

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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: ScamLensViewModel,
    onBack: () -> Unit = {},
    onNavigateToResult: (MessageAnalysis) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isHindi = false
    val isDark = LocalIsDark.current
    val primaryBlue = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB)
    val bgColor = if (isDark) Color(0xFF090B12) else Color(0xFFF8FAFC)
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val cardBorder = if (isDark) Color(0x1AFFFFFF) else Color(0xFFE2E8F0)

    LaunchedEffect(Unit) {
        AnalyticsManager.getInstance(context).logHistoryOpened()
        AnalyticsManager.getInstance(context).logScreenView("history")
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars), // Respect bottom system navigation bar completely
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "एनालिसिस इतिहास" else "Analysis History",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = if (isHindi) "होम पर वापस जाएं" else "Back to Home",
                            tint = textColorPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor),
                windowInsets = WindowInsets.statusBars // Respect status bar completely
            )
        }
    ) { innerPadding ->
        val history = viewModel.analysesHistory
        var selectedFilter by remember { mutableStateOf("All") }

        val filteredHistory = when (selectedFilter) {
            "Safe" -> history.filter { it.status.lowercase() == "safe" }
            "Suspicious" -> history.filter { it.status.lowercase() == "suspicious" || it.status.lowercase() == "warning" }
            "Danger" -> history.filter { it.status.lowercase() == "danger" || it.status.lowercase() == "unsafe" }
            else -> history
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("history_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Protection Status Badge
            item {
                val protectionStatus = ProtectionStatusHelper.calculateStatus(history)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "वर्तमान सुरक्षा स्थिति" else "Current Protection Status",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorSecondary
                        )
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(
                                color = protectionStatus.color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = protectionStatus.color.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(protectionStatus.color, CircleShape)
                            )
                            Text(
                                text = if (isHindi) {
                                    when (protectionStatus) {
                                        ProtectionStatus.EXCELLENT -> "उत्कृष्ट"
                                        ProtectionStatus.GOOD -> "अच्छा"
                                        ProtectionStatus.NEEDS_ATTENTION -> "ध्यान दें"
                                        ProtectionStatus.HIGH_RISK -> "उच्च जोखिम"
                                        ProtectionStatus.UNKNOWN -> "कोई स्कैन डेटा नहीं"
                                    }
                                } else {
                                    when (protectionStatus) {
                                        ProtectionStatus.EXCELLENT -> "Excellent"
                                        ProtectionStatus.GOOD -> "Good"
                                        ProtectionStatus.NEEDS_ATTENTION -> "Attention"
                                        ProtectionStatus.HIGH_RISK -> "High Risk"
                                        ProtectionStatus.UNKNOWN -> "No Scan Data"
                                    }
                                },
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                            )
                        }
                    }
                }
            }

            // 2. Compact statistics summary cards
            item {
                val totalScans = history.size
                val scamCount = history.count { it.status.lowercase() == "danger" || it.status.lowercase() == "unsafe" }
                val safeCount = history.count { it.status.lowercase() == "safe" }
                val suspiciousCount = history.count { it.status.lowercase() == "suspicious" }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statItems = listOf(
                        Triple("Total", totalScans, primaryBlue),
                        Triple("Scam", scamCount, Color(0xFFEF4444)),
                        Triple("Safe", safeCount, Color(0xFF10B981)),
                        Triple("Suspicious", suspiciousCount, Color(0xFFF59E0B))
                    )
                    statItems.forEach { (label, value, color) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isDark) 0.dp else 1.dp, RoundedCornerShape(12.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
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
                                        color = textColorSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Filter Chips
            if (history.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Safe", "Suspicious", "Danger").forEach { filter ->
                            val isSelected = selectedFilter == filter
                            val chipBg = if (isSelected) primaryBlue else cardBg
                            val chipText = if (isSelected) Color.White else textColorSecondary
                            val chipBorder = if (isSelected) primaryBlue else cardBorder
                            
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = chipBg,
                                border = BorderStroke(1.dp, chipBorder),
                                modifier = Modifier
                                    .clickable { selectedFilter = filter }
                                    .shadow(if (isSelected && !isDark) 2.dp else 0.dp, RoundedCornerShape(20.dp))
                            ) {
                                Text(
                                    text = if (isHindi) {
                                        when (filter) {
                                            "All" -> "सभी"
                                            "Safe" -> "Safe"
                                            "Suspicious" -> "Suspicious"
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
            }

            // 4. List Items, Loading State, or Empty State
            if (viewModel.isHistoryLoading) {
                items(5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerEffect()
                    )
                }
            } else if (filteredHistory.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_official_logo),
                                contentDescription = "Empty History Logo",
                                modifier = Modifier.fillMaxSize().alpha(0.15f),
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(primaryBlue)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = if (isHindi) "कोई एनालिसिस इतिहास नहीं है।" else "No analysis history yet.",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = if (isHindi) "अपना सुरक्षा इतिहास बनाने के लिए स्कैनिंग शुरू करें।" else "Start scanning to build your security history.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = textColorSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                items(filteredHistory, key = { it.timestamp }) { item ->
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = {
                                Text(
                                    text = if (isHindi) "इतिहास प्रविष्टि हटाएं?" else "Delete History Entry?",
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                            },
                            text = {
                                Text(
                                    text = if (isHindi) "क्या आप निश्चित रूप से इस एनालिसिस रिकॉर्ड को अपने device से हटाना चाहते हैं?" else "Are you sure you want to remove this analysis record from your device?",
                                    color = textColorSecondary,
                                    fontSize = 14.sp
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        viewModel.deleteAnalysisResult(item)
                                        showDeleteDialog = false
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                                ) {
                                    Text(if (isHindi) "हटाएं" else "Delete", fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDeleteDialog = false },
                                    colors = ButtonDefaults.textButtonColors(contentColor = textColorSecondary)
                                ) {
                                    Text(if (isHindi) "रद्द करें" else "Cancel")
                                }
                            },
                            containerColor = cardBg,
                            shape = RoundedCornerShape(20.dp)
                        )
                    }

                    HistoryCardItem(
                        item = item,
                        isHindi = isHindi,
                        onClick = {
                            AnalyticsManager.getInstance(context).logHistoryItemViewed(item.status)
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

@Composable
fun HistoryCardItem(
    item: MessageAnalysis,
    isHindi: Boolean = false,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDark = LocalIsDark.current
    
    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val cardBorder = if (isDark) Color(0x1AFFFFFF) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    
    val dangerRed = Color(0xFFEF4444)
    val warningOrange = Color(0xFFF59E0B)
    val successGreen = Color(0xFF10B981)

    val statusColor = when (item.status.lowercase()) {
        "danger", "unsafe", "high risk" -> dangerRed
        "suspicious", "warning", "medium risk" -> warningOrange
        else -> successGreen
    }
    
    val statusText = when (item.status.lowercase()) {
        "danger", "unsafe", "high risk" -> if (isHindi) "Danger" else "Danger"
        "suspicious", "warning", "medium risk" -> if (isHindi) "Suspicious" else "Suspicious"
        else -> if (isHindi) "Safe" else "Safe"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(elevation = if (isDark) 0.dp else 2.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .testTag("history_item_card_${item.score}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Message Text (Truncated gracefully with clear ellipses)
            Text(
                text = item.text,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bottom row: Score & Date on left, Status & Delete on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Info block
                Column {
                    Text(
                        text = DateTimeUtils.formatHistoryTimestamp(context, item.timestamp),
                        fontSize = 12.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isHindi) "Risk Score: ${item.score}%" else "Match Score: ${item.score}%",
                        fontSize = 12.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Status badge & Delete Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isDark) Color(0xFF2D1B1B) else Color(0xFFFEF2F2), CircleShape)
                            .testTag("delete_history_item_${item.score}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = dangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
