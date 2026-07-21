package com.skyorigin.threatshieldai

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonScamExamplesScreen(
    viewModel: ScamLensViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val isHindi = viewModel.currentLanguage == "hi"
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Search and filter states
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") } // "All", "Bookmarked", or 15 category strings
    val bookmarkedIds by viewModel.bookmarkedScamIds

    // Expanded card states (tracks which scam item ID is expanded)
    var expandedScamId by remember { mutableStateOf<Int?>(null) }

    // Filtered scams list
    val filteredScams = remember(searchQuery, selectedCategory, bookmarkedIds, isHindi) {
        ScamExamplesData.scams.filter { scam ->
            // Category filter
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Bookmarked" -> bookmarkedIds.contains(scam.id)
                else -> scam.category == selectedCategory
            }

            // Search query filter
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                val title = if (isHindi) scam.titleHi else scam.titleEn
                val msg = if (isHindi) scam.messageHi else scam.messageEn
                val danger = if (isHindi) scam.dangerHi else scam.dangerEn
                val cat = scam.category
                title.contains(searchQuery, ignoreCase = true) ||
                        msg.contains(searchQuery, ignoreCase = true) ||
                        danger.contains(searchQuery, ignoreCase = true) ||
                        cat.contains(searchQuery, ignoreCase = true)
            }

            matchesCategory && matchesQuery
        }
    }

    // Scroll state for list
    val listState = rememberLazyListState()

    // Smooth scroll to top when category changes
    LaunchedEffect(selectedCategory) {
        if (filteredScams.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "सामान्य स्कैम उदाहरण" else "Common Scams",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (isDark) Color.White else PremiumColors.TextDark
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("scams_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = if (isHindi) "पीछे जाएं" else "Back",
                            tint = if (isDark) Color.White else PremiumColors.TextDark
                        )
                    }
                },
                actions = {
                    // Small top indicator showing active bookmarks
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(
                                color = if (bookmarkedIds.isNotEmpty()) PremiumColors.PrimaryAccent.copy(alpha = 0.1f)
                                else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (bookmarkedIds.isNotEmpty()) PremiumColors.PrimaryAccent.copy(alpha = 0.2f)
                                else Color.Transparent,
                                shape = CircleShape
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .clickable {
                                selectedCategory = if (selectedCategory == "Bookmarked") "All" else "Bookmarked"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (bookmarkedIds.isNotEmpty()) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                contentDescription = "Bookmarks",
                                tint = if (bookmarkedIds.isNotEmpty()) PremiumColors.PrimaryAccent else PremiumColors.SubtitleGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${bookmarkedIds.size}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (bookmarkedIds.isNotEmpty()) PremiumColors.PrimaryAccent else PremiumColors.SubtitleGray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDark) Color(0xFF0B0F19) else Color(0xFFF9FAFB)
                )
            )
        },
        containerColor = if (isDark) Color(0xFF0B0F19) else Color(0xFFF9FAFB)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("scam_search_bar"),
                placeholder = {
                    Text(
                        text = if (isHindi) "100+ घोटालों में खोजें..." else "Search 100+ scams...",
                        color = if (isDark) Color(0xFF64748B) else PremiumColors.SubtitleGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search Icon",
                        tint = if (isDark) Color(0xFF64748B) else PremiumColors.SubtitleGray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Clear Search",
                                tint = if (isDark) Color(0xFF64748B) else PremiumColors.SubtitleGray
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremiumColors.PrimaryAccent,
                    unfocusedBorderColor = if (isDark) Color(0xFF1E293B) else PremiumColors.SubtleBorderLight,
                    focusedContainerColor = if (isDark) Color(0xFF111827) else Color.White,
                    unfocusedContainerColor = if (isDark) Color(0xFF111827) else Color.White,
                    focusedTextColor = if (isDark) Color.White else PremiumColors.TextDark,
                    unfocusedTextColor = if (isDark) Color.White else PremiumColors.TextDark
                ),
                shape = RoundedCornerShape(16.dp)
            )

            // 2. Category Row (Horizontal Scroll)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Chip
                item {
                    CategoryChip(
                        name = if (isHindi) "सभी स्कैम" else "All Scams",
                        isSelected = selectedCategory == "All",
                        onClick = { selectedCategory = "All" }
                    )
                }

                // Bookmarked Chip
                item {
                    CategoryChip(
                        name = if (isHindi) "बुकमार्क किए गए (${bookmarkedIds.size})" else "Bookmarked (${bookmarkedIds.size})",
                        isSelected = selectedCategory == "Bookmarked",
                        onClick = { selectedCategory = "Bookmarked" },
                        icon = Icons.Rounded.Bookmark
                    )
                }

                // 15 Specific categories
                items(ScamExamplesData.categories) { cat ->
                    val localizedCat = when (cat) {
                        "Phishing" -> if (isHindi) "फ़िशिंग" else "Phishing"
                        "OTP Fraud" -> if (isHindi) "ओटीपी धोखाधड़ी" else "OTP Fraud"
                        "UPI Scam" -> if (isHindi) "यूपीआई स्कैम" else "UPI Scam"
                        "QR Code Scam" -> if (isHindi) "क्यूआर कोड स्कैम" else "QR Code Scam"
                        "Fake Bank Call" -> if (isHindi) "फर्जी बैंक कॉल" else "Fake Bank Call"
                        "Fake KYC" -> if (isHindi) "फर्जी केवाईसी" else "Fake KYC"
                        "Fake Delivery" -> if (isHindi) "फर्जी डिलीवरी" else "Fake Delivery"
                        "Job Scam" -> if (isHindi) "नौकरी घोटाला" else "Job Scam"
                        "Investment Scam" -> if (isHindi) "निवेश घोटाला" else "Investment Scam"
                        "Lottery Scam" -> if (isHindi) "लॉटरी घोटाला" else "Lottery Scam"
                        "Tech Support Scam" -> if (isHindi) "तकनीकी सहायता घोटाला" else "Tech Support Scam"
                        "WhatsApp Scam" -> if (isHindi) "व्हाट्सएप स्कैम" else "WhatsApp Scam"
                        "Telegram Scam" -> if (isHindi) "टेलीग्राम स्कैम" else "Telegram Scam"
                        "Instagram Scam" -> if (isHindi) "इंस्टाग्राम स्कैम" else "Instagram Scam"
                        "Fake Customer Care" -> if (isHindi) "फर्जी कस्टमर केयर" else "Fake Customer Care"
                        else -> cat
                    }

                    CategoryChip(
                        name = localizedCat,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Main List
            if (filteredScams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SearchOff,
                            contentDescription = "No matches",
                            tint = if (isDark) Color(0xFF334155) else Color(0xFFD1D5DB),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = if (isHindi) "कोई स्कैम नहीं मिला" else "No scam examples found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) Color.White else PremiumColors.TextDark
                        )
                        Text(
                            text = if (isHindi) "कृपया कोई अन्य खोज शब्द या श्रेणी चुनें।" else "Try adjusting your search filters or clear your text query.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) Color(0xFF64748B) else PremiumColors.SubtitleGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredScams, key = { it.id }) { scam ->
                        val isExpanded = expandedScamId == scam.id
                        val isBookmarked = bookmarkedIds.contains(scam.id)

                        ScamItemCard(
                            scam = scam,
                            isExpanded = isExpanded,
                            isBookmarked = isBookmarked,
                            isHindi = isHindi,
                            isDark = isDark,
                            onToggleExpand = {
                                expandedScamId = if (isExpanded) null else scam.id
                            },
                            onToggleBookmark = {
                                viewModel.toggleScamBookmark(scam.id)
                            },
                            onShare = {
                                shareScamDetails(context, scam, isHindi)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDark.current
    val backgroundColor = if (isSelected) {
        PremiumColors.PrimaryAccent
    } else {
        if (isDark) Color(0xFF1E293B) else Color.White
    }

    val contentColor = if (isSelected) {
        Color.White
    } else {
        if (isDark) Color(0xFFCBD5E1) else PremiumColors.TextDark
    }

    val borderColor = if (isSelected) {
        Color.Transparent
    } else {
        if (isDark) Color(0xFF334155) else PremiumColors.SubtleBorderLight
    }

    Surface(
        onClick = onClick,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(vertical = 4.dp)
            .height(38.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScamItemCard(
    scam: ScamExample,
    isExpanded: Boolean,
    isBookmarked: Boolean,
    isHindi: Boolean,
    isDark: Boolean,
    onToggleExpand: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = if (isHindi) scam.titleHi else scam.titleEn
    val scenario = if (isHindi) scam.messageHi else scam.messageEn
    val danger = if (isHindi) scam.dangerHi else scam.dangerEn
    val safeResponse = if (isHindi) scam.safeResponseHi else scam.safeResponseEn
    val redFlagsList = if (isHindi) scam.redFlagsHi else scam.redFlagsEn

    val difficultyColor = when (scam.difficulty) {
        "Easy" -> PremiumColors.Safe
        "Medium" -> PremiumColors.Warning
        else -> PremiumColors.Danger
    }

    val difficultyText = when (scam.difficulty) {
        "Easy" -> if (isHindi) "आसान पहचान" else "Easy to Spot"
        "Medium" -> if (isHindi) "मध्यम खतरा" else "Medium Risk"
        else -> if (isHindi) "कठिन / गंभीर" else "Sophisticated / Hard"
    }

    val localizedCategoryName = when (scam.category) {
        "Phishing" -> if (isHindi) "फ़िशिंग" else "Phishing"
        "OTP Fraud" -> if (isHindi) "ओटीपी धोखाधड़ी" else "OTP Fraud"
        "UPI Scam" -> if (isHindi) "यूपीआई स्कैम" else "UPI Scam"
        "QR Code Scam" -> if (isHindi) "क्यूआर कोड स्कैम" else "QR Code Scam"
        "Fake Bank Call" -> if (isHindi) "फर्जी बैंक कॉल" else "Fake Bank Call"
        "Fake KYC" -> if (isHindi) "फर्जी केवाईसी" else "Fake KYC"
        "Fake Delivery" -> if (isHindi) "फर्जी डिलीवरी" else "Fake Delivery"
        "Job Scam" -> if (isHindi) "नौकरी घोटाला" else "Job Scam"
        "Investment Scam" -> if (isHindi) "निवेश घोटाला" else "Investment Scam"
        "Lottery Scam" -> if (isHindi) "लॉटरी घोटाला" else "Lottery Scam"
        "Tech Support Scam" -> if (isHindi) "तकनीकी सहायता" else "Tech Support"
        "WhatsApp Scam" -> if (isHindi) "व्हाट्सएप स्कैम" else "WhatsApp Scam"
        "Telegram Scam" -> if (isHindi) "टेलीग्राम स्कैम" else "Telegram Scam"
        "Instagram Scam" -> if (isHindi) "इंस्टाग्राम स्कैम" else "Instagram Scam"
        "Fake Customer Care" -> if (isHindi) "फर्जी कस्टमर केयर" else "Fake Customer Care"
        else -> scam.category
    }

    // Interactive card animation
    val cardBgColor = if (isDark) Color(0xFF111827) else Color.White
    val cardBorderColor = if (isExpanded) {
        PremiumColors.PrimaryAccent.copy(alpha = 0.5f)
    } else {
        if (isDark) Color(0xFF1E293B) else PremiumColors.SubtleBorderLight
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .testTag("scam_card_${scam.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, cardBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Category badge + Difficulty
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                Box(
                    modifier = Modifier
                        .background(
                            color = PremiumColors.PrimaryAccent.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = localizedCategoryName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PremiumColors.PrimaryAccent
                    )
                }

                // Difficulty Rating Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color = difficultyColor, shape = CircleShape)
                    )
                    Text(
                        text = difficultyText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = difficultyColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scam Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                ),
                color = if (isDark) Color.White else PremiumColors.TextDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Short Preview Scenario
            Text(
                text = scenario,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) Color(0xFF94A3B8) else PremiumColors.SubtitleGray,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            // Dynamic Detail Expansion (Using standard Compose transition for smooth and robust animation)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    Divider(
                        color = if (isDark) Color(0xFF1E293B) else PremiumColors.SubtleBorderLight,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // 1. Scenario Sub-Card (Pretends to show the real scam text)
                    Text(
                        text = if (isHindi) "घोटाला संदेश / परिदृश्य:" else "SCAM SCENARIO / MESSAGE:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = if (isDark) Color(0xFF94A3B8) else PremiumColors.SubtitleGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color(0xFF334155) else Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ChatBubbleOutline,
                                contentDescription = "Scam Scenario",
                                tint = PremiumColors.Danger,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = scenario,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 20.sp
                                ),
                                color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF374151)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Why it is dangerous
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Dangerous,
                            contentDescription = "Danger Icon",
                            tint = PremiumColors.Danger,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Column {
                            Text(
                                text = if (isHindi) "यह खतरनाक क्यों है?" else "WHY IT IS DANGEROUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = PremiumColors.Danger
                            )
                            Text(
                                text = danger,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                color = if (isDark) Color(0xFFCBD5E1) else PremiumColors.TextDark,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Red Flags
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = PremiumColors.Warning.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = PremiumColors.Warning.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = "Red Flags",
                                tint = PremiumColors.Warning,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isHindi) "चेतावनी के संकेत (Red Flags):" else "RED FLAGS TO WATCH OUT:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = PremiumColors.Warning
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        redFlagsList.forEach { flag ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "🚩",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = flag,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                                    color = if (isDark) Color(0xFFE2E8F0) else PremiumColors.TextDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Safe Response
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = PremiumColors.Safe.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = PremiumColors.Safe.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Safe Response",
                                tint = PremiumColors.Safe,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isHindi) "सुरक्षित कार्रवाई / प्रतिक्रिया:" else "HOW TO STAY SAFE:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = PremiumColors.Safe
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = safeResponse,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                lineHeight = 19.sp
                            ),
                            color = if (isDark) Color(0xFFCBD5E1) else PremiumColors.TextDark,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Buttons (Bookmark + Share + Expand/Collapse Indicator)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary action buttons (Bookmark & Share)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bookmark Button
                    IconButton(
                        onClick = {
                            onToggleBookmark()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (isBookmarked) PremiumColors.PrimaryAccent.copy(alpha = 0.12f)
                                else if (isDark) Color(0xFF1E293B) else Color(0xFFF3F4F6),
                                shape = CircleShape
                            )
                            .testTag("bookmark_button_${scam.id}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark",
                            tint = if (isBookmarked) PremiumColors.PrimaryAccent else if (isDark) Color(0xFF94A3B8) else PremiumColors.SubtitleGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF3F4F6),
                                shape = CircleShape
                            )
                            .testTag("share_button_${scam.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = if (isDark) Color(0xFF94A3B8) else PremiumColors.SubtitleGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Expand/Collapse text indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable { onToggleExpand() }
                ) {
                    Text(
                        text = if (isExpanded) {
                            if (isHindi) "कम विवरण दिखाएं" else "Show Less"
                        } else {
                            if (isHindi) "पूरा विवरण देखें" else "Read Details"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        ),
                        color = PremiumColors.PrimaryAccent
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse Indicator",
                        tint = PremiumColors.PrimaryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Share helper function using native Android chooser sheet
private fun shareScamDetails(context: android.content.Context, scam: ScamExample, isHindi: Boolean) {
    val title = if (isHindi) scam.titleHi else scam.titleEn
    val category = if (isHindi) {
        when (scam.category) {
            "Phishing" -> "फ़िशिंग"
            "OTP Fraud" -> "ओटीपी धोखाधड़ी"
            "UPI Scam" -> "यूपीआई स्कैम"
            "QR Code Scam" -> "क्यूआर कोड स्कैम"
            "Fake Bank Call" -> "फर्जी बैंक कॉल"
            "Fake KYC" -> "फर्जी केवाईसी"
            "Fake Delivery" -> "फर्जी डिलीवरी"
            "Job Scam" -> "नौकरी घोटाला"
            "Investment Scam" -> "निवेश घोटाला"
            "Lottery Scam" -> "लॉटरी घोटाला"
            "Tech Support Scam" -> "तकनीकी सहायता घोटाला"
            "WhatsApp Scam" -> "व्हाट्सएप स्कैम"
            "Telegram Scam" -> "टेलीग्राम स्कैम"
            "Instagram Scam" -> "इंस्टाग्राम स्कैम"
            "Fake Customer Care" -> "फर्जी कस्टमर केयर"
            else -> scam.category
        }
    } else scam.category

    val scenario = if (isHindi) scam.messageHi else scam.messageEn
    val danger = if (isHindi) scam.dangerHi else scam.dangerEn
    val safeResponse = if (isHindi) scam.safeResponseHi else scam.safeResponseEn
    val redFlags = if (isHindi) {
        scam.redFlagsHi.joinToString("\n") { "🚩 $it" }
    } else {
        scam.redFlagsEn.joinToString("\n") { "🚩 $it" }
    }

    val headerText = if (isHindi) "⚠️ थ्रेटशील्ड एआई - सामान्य स्कैम अलर्ट! ⚠️" else "⚠️ ThreatShield AI - Common Scam Alert! ⚠️"
    val categoryLabel = if (isHindi) "श्रेणी" else "Category"
    val diffLabel = if (isHindi) "पहचान स्तर" else "Difficulty Level"
    val scenarioLabel = if (isHindi) "स्कैम संदेश / परिदृश्य" else "Scam Message / Scenario"
    val dangerLabel = if (isHindi) "यह खतरनाक क्यों है" else "Why it is Dangerous"
    val flagsLabel = if (isHindi) "चेतावनी के प्रमुख संकेत" else "Critical Red Flags"
    val safetyLabel = if (isHindi) "सुरक्षित रहने के लिए निर्देश" else "How to Stay Safe"
    val footerText = if (isHindi) "सुरक्षित रहें, थ्रेटशील्ड एआई डाउनलोड करें।" else "Stay safe! Download ThreatShield AI to analyze scams in real-time."

    val difficultyText = when (scam.difficulty) {
        "Easy" -> if (isHindi) "आसान पहचान" else "Easy to Spot"
        "Medium" -> if (isHindi) "मध्यम खतरा" else "Medium Risk"
        else -> if (isHindi) "कठिन / गंभीर" else "Hard / Sophisticated"
    }

    val shareContent = """
        $headerText
        
        $title
        • $categoryLabel: $category
        • $diffLabel: $difficultyText
        
        ---
        
        [$scenarioLabel]
        "$scenario"
        
        ---
        
        • $dangerLabel:
        $danger
        
        • $flagsLabel:
        $redFlags
        
        • $safetyLabel:
        $safeResponse
        
        ---
        $footerText
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, shareContent)
    }

    context.startActivity(Intent.createChooser(intent, if (isHindi) "इसके माध्यम से साझा करें" else "Share via"))
}
