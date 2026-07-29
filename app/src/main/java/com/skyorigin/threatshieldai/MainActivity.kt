package com.skyorigin.threatshieldai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skyorigin.threatshieldai.ui.theme.MyApplicationTheme
import com.skyorigin.threatshieldai.ui.theme.PremiumColors
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import android.content.Intent
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.android.gms.ads.MobileAds

fun Context.setAppLocale(language: String): Context {
    val locale = java.util.Locale(language)
    java.util.Locale.setDefault(locale)
    val config = android.content.res.Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

class MainActivity : ComponentActivity() {
    private var keepSplashOnScreen = true

    private lateinit var consentManager: ConsentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        lifecycleScope.launch {
            delay(200)
            keepSplashOnScreen = false
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.setBackgroundColor(android.graphics.Color.parseColor("#000000"))
        
        try {
            consentManager = ConsentManager(this)
            consentManager.gatherConsent(this) { consentError ->
                if (consentError != null) {
                    android.util.Log.w("MainActivity", "Consent gathering failed: ${consentError.errorCode} - ${consentError.message}")
                }
                try {
                    if (consentManager.canRequestAds) {
                        AdManager.initialize(this)
                    }
                } catch (e: Throwable) {
                    android.util.Log.e("MainActivity", "Error in ConsentManager callback", e)
                }
            }
            
            if (consentManager.canRequestAds) {
                AdManager.initialize(this)
            }
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "Error initializing ConsentManager / AdManager", e)
        }

        try {
            NotificationHelper.createNotificationChannel(this)
            NotificationHelper.scheduleDailyChallengeNotification(this)
            AnalyticsManager.getInstance(this).logAppOpen()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error during initialization", e)
        }

        setContent {
            val sharedViewModel: ScamLensViewModel = viewModel()
            val themeMode by sharedViewModel.currentThemeModeState
            val currentLanguage by sharedViewModel.currentLanguageState

            val context = LocalContext.current
            val localizedContext = context

            val activityResultRegistryOwner = this@MainActivity
            val onBackPressedDispatcherOwner = this@MainActivity

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                androidx.activity.compose.LocalActivityResultRegistryOwner provides activityResultRegistryOwner,
                androidx.activity.compose.LocalOnBackPressedDispatcherOwner provides onBackPressedDispatcherOwner
            ) {
                MyApplicationTheme(themeMode = themeMode) {
                    val navController = rememberNavController()

                var currentIntent by remember { mutableStateOf(intent) }
                DisposableEffect(Unit) {
                    val listener = androidx.core.util.Consumer<Intent> { newIntent ->
                        currentIntent = newIntent
                    }
                    addOnNewIntentListener(listener)
                    onDispose {
                        removeOnNewIntentListener(listener)
                    }
                }
                LaunchedEffect(currentIntent) {
                    currentIntent?.let { intent ->
                        val navigateTo = intent.getStringExtra("navigate_to")
                        if (navigateTo != null) {
                            intent.removeExtra("navigate_to")
                            when (navigateTo) {
                                "result" -> {
                                    val timestamp = intent.getLongExtra("timestamp", 0L)
                                    intent.removeExtra("timestamp")
                                    var analysis: MessageAnalysis? = null
                                    if (timestamp != 0L) {
                                        analysis = sharedViewModel.getScanByTimestamp(timestamp)
                                    }
                                    if (analysis == null) {
                                        analysis = sharedViewModel.currentAnalysisResult
                                            ?: sharedViewModel.analysesHistory.firstOrNull()
                                    }
                                    if (analysis != null) {
                                        sharedViewModel.currentAnalysisResult = analysis
                                        navController.navigate("result") {
                                            popUpTo("main") { inclusive = false }
                                        }
                                    }
                                }
                                "daily_challenge" -> {
                                    navController.navigate("daily_challenge") {
                                        popUpTo("main") { inclusive = false }
                                    }
                                }
                                "daily_tip" -> {
                                    navController.navigate("daily_safety_tip") {
                                        popUpTo("main") { inclusive = false }
                                    }
                                }
                                "quick_quiz" -> {
                                    navController.navigate("quick_quiz") {
                                        popUpTo("main") { inclusive = false }
                                    }
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val initialDestination = remember {
                        if (intent?.hasExtra("navigate_to") == true && intent?.getStringExtra("navigate_to") != null) {
                            "main"
                        } else if (sharedViewModel.hasAcceptedTerms) {
                            "main"
                        } else {
                            "get_started"
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = initialDestination
                    ) {
                            composable(
                                route = "get_started",
                                enterTransition = {
                                    fadeIn(animationSpec = tween(300)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(300))
                                },
                                exitTransition = {
                                    fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.95f, animationSpec = tween(300))
                                }
                            ) {
                                GetStartedScreen(
                                    onComplete = {
                                        sharedViewModel.legalConsentAccepted = true
                                        sharedViewModel.onboardingCompleted = true
                                        navController.navigate("main") {
                                            popUpTo("get_started") { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = "main",
                                enterTransition = {
                                    fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 1.05f, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    fadeOut(animationSpec = tween(400))
                                },
                                popEnterTransition = {
                                    fadeIn(animationSpec = tween(400))
                                }
                            ) {
                                MainScreen(
                                    mainNavController = navController,
                                    sharedViewModel = sharedViewModel
                                )
                            }
                            composable(
                                route = "loading",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                                },
                                exitTransition = {
                                    fadeOut(animationSpec = tween(400))
                                }
                            ) {
                                AnalysisLoadingScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    textToAnalyze = sharedViewModel.userInputText,
                                    viewModel = sharedViewModel,
                                    onBack = { navController.popBackStack() },
                                    onAnalysisComplete = { newAnalysis ->
                                        sharedViewModel.addAnalysisResult(newAnalysis)
                                        sharedViewModel.currentAnalysisResult = newAnalysis
                                        sharedViewModel.userInputText = ""
                                        
                                        // Show scan completed notification
                                        NotificationHelper.showScanCompleteNotification(this@MainActivity, newAnalysis)

                                        navController.navigate("result") {
                                            popUpTo("main") { inclusive = false }
                                        }
                                    }
                                )
                            }
                            composable(
                                route = "result",
                                enterTransition = {
                                    fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    fadeOut(animationSpec = tween(400))
                                }
                            ) {
                                val isDark = com.skyorigin.threatshieldai.ui.theme.LocalIsDark.current
                                val isHindi = sharedViewModel.currentLanguage == "hi"
                                val result = sharedViewModel.currentAnalysisResult
                                if (result != null) {
                                    AnalysisResultScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        analysis = result,
                                        isHindi = isHindi,
                                        onBack = {
                                            sharedViewModel.userInputText = ""
                                            navController.popBackStack()
                                        },
                                        onAnalyzeAnother = {
                                            sharedViewModel.resetScanState()
                                            navController.popBackStack("main", inclusive = false)
                                        }
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(if (isDark) Color(0xFF020617) else Color(0xFFF8FAFC)),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp),
                                            modifier = Modifier.padding(24.dp)
                                        ) {
                                            CircularProgressIndicator(color = Color(0xFF3B82F6))
                                            Text(
                                                text = if (isHindi) "परिणाम लोड हो रहा है..." else "Loading analysis result...",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = if (isDark) Color.White else Color.Black
                                            )
                                            Button(
                                                onClick = { navController.popBackStack("main", inclusive = false) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                                            ) {
                                                Text(if (isHindi) "डैशबोर्ड पर लौटें" else "Return to Dashboard", color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                            composable(
                                route = "history",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                HistoryScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    viewModel = sharedViewModel,
                                    onBack = { navController.popBackStack() },
                                    onNavigateToResult = { item ->
                                        sharedViewModel.currentAnalysisResult = item
                                        navController.navigate("result")
                                    }
                                )
                            }
                            composable(
                                route = "about",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                AboutScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "faq",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                FaqScreen(
                                    viewModel = sharedViewModel,
                                    onBack = { navController.popBackStack() },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = "daily_challenge",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                DailyChallengeScreen(
                                    viewModel = sharedViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "scam_academy",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                ScamAcademyScreen(
                                    viewModel = sharedViewModel,
                                    onNavigateToCategory = { categoryId ->
                                        navController.navigate("scam_academy_detail/$categoryId")
                                    },
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "scam_academy_detail/{categoryId}",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) { backStackEntry ->
                                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                                ScamAcademyDetailScreen(
                                    categoryId = categoryId,
                                    viewModel = sharedViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "daily_safety_tip",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                DailySafetyTipScreen(
                                    viewModel = sharedViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "quick_quiz",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                QuickQuizScreen(
                                    viewModel = sharedViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "common_scam_examples",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                CommonScamExamplesScreen(
                                    viewModel = sharedViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "cyber_dictionary",
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) {
                                LearnPlaceholderScreen(
                                    title = if (sharedViewModel.currentLanguage == "hi") "साइबर शब्दकोश" else "Cyber Dictionary",
                                    icon = Icons.Rounded.MenuBook,
                                    color = Color(0xFF06B6D4),
                                    moduleType = "cyber_dictionary",
                                    viewModel = sharedViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "inventory"
                            ) {
                                InventoryScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    viewModel = sharedViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            // Removed webview route
                        }

                        if (sharedViewModel.showNoInternetDialog) {
                            NoInternetDialog(
                                isHindi = sharedViewModel.currentLanguage == "hi",
                                onDismiss = {
                                    sharedViewModel.showNoInternetDialog = false
                                },
                                onRetry = {
                                    val isAvailable = NetworkUtils.isInternetAvailable(context)
                                    if (isAvailable) {
                                        sharedViewModel.showNoInternetDialog = false
                                    }
                                    isAvailable
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
