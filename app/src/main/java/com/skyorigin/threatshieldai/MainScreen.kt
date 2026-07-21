package com.skyorigin.threatshieldai

import androidx.compose.animation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import com.skyorigin.threatshieldai.ui.theme.premiumShadow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skyorigin.threatshieldai.ui.theme.blueGlow
import android.os.Build
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource

@Composable
fun MainScreen(
    mainNavController: NavController,
    sharedViewModel: ScamLensViewModel
) {
    val bottomNavController = rememberNavController()

    var showFeedbackDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    LaunchedEffect(currentRoute) {
        if (currentRoute == "dashboard" && sharedViewModel.shouldShowFeedbackPopup()) {
            showFeedbackDialog = true
        }
    }

    LaunchedEffect(sharedViewModel.requestedTabRoute) {
        val targetRoute = sharedViewModel.requestedTabRoute
        if (targetRoute != null) {
            bottomNavController.navigate(targetRoute) {
                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            sharedViewModel.requestedTabRoute = null
        }
    }


    if (showFeedbackDialog) {
        FeedbackDialog(
            viewModel = sharedViewModel,
            onDismiss = { showFeedbackDialog = false }
        )
    }
    
    val isDark = com.skyorigin.threatshieldai.ui.theme.LocalIsDark.current
    val currentThemeMode = sharedViewModel.currentThemeMode

    Scaffold(
        bottomBar = {
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

            val glassContainerColor = when (currentThemeMode) {
                ThemeMode.DARK -> Color(0xCC0B0F14)
                ThemeMode.LIGHT -> Color(0xFFFFFFFF) // Pure white for light mode
                ThemeMode.SYSTEM -> {
                    if (isDark) Color(0xCC0B0F14) else Color(0xFFFFFFFF)
                }
            }

            val subtleBorderColor = if (isDark) {
                Color(0xFF2E3748).copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh // #D9DEE7 divider
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp)
                        .shadow(
                            elevation = if (isDark) 16.dp else 0.dp, // No shadow for light mode bottom nav if we want iOS style or keep it very subtle
                            shape = RoundedCornerShape(28.dp),
                            clip = false,
                            ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0xFF0F172A).copy(alpha = 0.04f),
                            spotColor = if (isDark) Color.Black.copy(alpha = 0.7f) else Color(0xFF0F172A).copy(alpha = 0.12f)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    color = glassContainerColor,
                    border = BorderStroke(1.dp, subtleBorderColor)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val containerWidth = maxWidth
                        val tabWidth = containerWidth / 5

                        val selectedIndex = when (currentRoute) {
                            "dashboard" -> 0
                            "history" -> 1
                            "scan" -> 2
                            "learn" -> 3
                            "settings" -> 4
                            else -> 0
                        }

                        val pillWidth = 56.dp
                        val pillHeight = 32.dp

                        val targetXOffset = (tabWidth * selectedIndex) + (tabWidth - pillWidth) / 2
                        val animatedXOffset by animateDpAsState(
                            targetValue = targetXOffset,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "pill_offset"
                        )

                        // Center content of 76dp bar vertically
                        val pillYOffset = 12.dp

                        // Flat, gradient-free premium M3 active pill background
                        Box(
                            modifier = Modifier
                                .offset(x = animatedXOffset, y = pillYOffset)
                                .width(pillWidth)
                                .height(pillHeight)
                                .background(
                                    brush = if (isDark) {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF3B82F6).copy(alpha = 0.25f),
                                                Color(0xFF1D4ED8).copy(alpha = 0.15f)
                                            )
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF2563EB).copy(alpha = 0.12f),
                                                Color(0xFF3B82F6).copy(alpha = 0.06f)
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isDark) Color(0xFF3B82F6).copy(alpha = 0.35f) else Color(0xFF2563EB).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        )

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                NavigationItem("dashboard", "Home", Icons.Rounded.Home),
                                NavigationItem("history", "History", Icons.Rounded.History),
                                NavigationItem("scan", "Analysis", Icons.Rounded.Shield),
                                NavigationItem("learn", "Learn", Icons.Rounded.School),
                                NavigationItem("settings", "Settings", Icons.Rounded.Settings)
                            ).forEachIndexed { index, item ->
                                val isSelected = index == selectedIndex

                                val animatedContentColor by animateColorAsState(
                                    targetValue = if (isSelected) {
                                        if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
                                    } else {
                                        if (isDark) Color(0xFF94A3B8) else Color(0xFF7C8798) // #7C8798 for unselected
                                    },
                                    animationSpec = tween(250),
                                    label = "tab_color"
                                )

                                val animatedLabelColor by animateColorAsState(
                                    targetValue = if (isSelected) {
                                        if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
                                    } else {
                                        if (isDark) Color(0xFF94A3B8) else Color(0xFF7C8798) // #7C8798 for unselected
                                    },
                                    animationSpec = tween(250),
                                    label = "label_color"
                                )

                                val scale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.05f else 1.0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "tab_scale"
                                )

                                val iconScale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.15f else 1.0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "icon_scale"
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable(
                                            onClick = {
                                                if (currentRoute != item.route) {
                                                    bottomNavController.navigate(item.route) {
                                                        popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = androidx.compose.foundation.LocalIndication.current
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .graphicsLayer(
                                                scaleX = scale,
                                                scaleY = scale
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 56.dp, height = 32.dp)
                                                .graphicsLayer(
                                                    scaleX = iconScale,
                                                    scaleY = iconScale
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.label,
                                                tint = animatedContentColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = item.label,
                                            color = animatedLabelColor,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 10.sp,
                                                letterSpacing = 0.1.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { 80 }, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -80 }, animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -80 }, animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { 80 }, animationSpec = tween(300)) }
        ) {
            composable("dashboard") {
                DashboardScreen(
                    viewModel = sharedViewModel,
                    onNavigateToScan = {
                        bottomNavController.navigate("scan") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToResult = { analysis ->
                        sharedViewModel.currentAnalysisResult = analysis
                        mainNavController.navigate("result")
                    },
                    onNavigateToHistory = {
                        bottomNavController.navigate("history") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToDailyChallenge = {
                        bottomNavController.navigate("learn") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = {
                        bottomNavController.navigate("settings") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToInventory = {
                        mainNavController.navigate("inventory")
                    }
                )
            }
            composable("scan") {
                HomeScreen(
                    viewModel = sharedViewModel,
                    onNavigateToSettings = {
                        bottomNavController.navigate("settings") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToHistory = {
                        bottomNavController.navigate("history") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToLoading = { text ->
                        sharedViewModel.prepareForScan(text)
                        mainNavController.navigate("loading")
                    }
                )
            }
            composable("history") {
                HistoryScreen(
                    viewModel = sharedViewModel,
                    onBack = {
                        bottomNavController.navigate("dashboard") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToResult = { analysis ->
                        sharedViewModel.currentAnalysisResult = analysis
                        mainNavController.navigate("result")
                    }
                )
            }
            composable("learn") {
                LearnScreen(
                    viewModel = sharedViewModel,
                    onNavigateToChallenge = { mainNavController.navigate("daily_challenge") },
                    onNavigateToAcademy = { mainNavController.navigate("scam_academy") },
                    onNavigateToDailyTip = { mainNavController.navigate("daily_safety_tip") },
                    onNavigateToQuiz = { mainNavController.navigate("quick_quiz") },
                    onNavigateToCommonScams = { mainNavController.navigate("common_scam_examples") },
                    onNavigateToDictionary = { mainNavController.navigate("cyber_dictionary") },
                    onNavigateBack = {
                        bottomNavController.navigate("dashboard") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable("settings") {
                val context = androidx.compose.ui.platform.LocalContext.current
                SettingsScreen(
                    viewModel = sharedViewModel,
                    onNavigateToAbout = { mainNavController.navigate("about") },
                    onNavigateToAppearance = { mainNavController.navigate("appearance") },
                    onNavigateToHistory = {
                        bottomNavController.navigate("history") {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToDoc = { doc ->
                        LegalConstants.openLegalPortal(context)
                    },
                    onNavigateToFaq = { mainNavController.navigate("faq") }
                )
            }
        }
    }
}

private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)


