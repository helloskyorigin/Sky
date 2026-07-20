package com.example

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
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.MyApplicationTheme
import android.content.Intent
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds

fun Context.setAppLocale(language: String): Context {
    val locale = java.util.Locale(language)
    java.util.Locale.setDefault(locale)
    val config = android.content.res.Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) {}
        AdMobManager.initialize(this)
        NotificationHelper.createNotificationChannel(this)
        NotificationHelper.scheduleDailyChallengeNotification(this)
        setContent {
            val sharedViewModel: ScamLensViewModel = viewModel()
            val themeMode by sharedViewModel.currentThemeModeState
            val currentLanguage by sharedViewModel.currentLanguageState

            val context = LocalContext.current
            val localizedContext = remember(currentLanguage) {
                if (currentLanguage != null && currentLanguage != "system") {
                    context.setAppLocale(currentLanguage!!)
                } else {
                    context
                }
            }

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
                        if (intent.getStringExtra("navigate_to") == "result") {
                            val text = intent.getStringExtra("text")
                            if (text != null) {
                                val date = intent.getStringExtra("date") ?: ""
                                val status = intent.getStringExtra("status") ?: ""
                                val score = intent.getIntExtra("score", 0)
                                val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())
                                val analysis = MessageAnalysis(
                                    text = text, 
                                    date = date, 
                                    status = status, 
                                    score = score,
                                    timestamp = timestamp
                                )
                                
                                sharedViewModel.currentAnalysisResult = analysis
                                if (!sharedViewModel.analysesHistory.any { it.text == text }) {
                                    sharedViewModel.addAnalysisResult(analysis)
                                }
                                
                                intent.removeExtra("navigate_to")
                                
                                navController.navigate("result") {
                                    popUpTo("main") { inclusive = false }
                                }
                            }
                        } else if (intent.getStringExtra("navigate_to") == "daily_challenge") {
                            intent.removeExtra("navigate_to")
                            navController.navigate("daily_challenge") {
                                popUpTo("main") { inclusive = false }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (intent?.getStringExtra("navigate_to") == "result" || intent?.getStringExtra("navigate_to") == "daily_challenge") "main" else "splash"
                    ) {
                            composable(
                                route = "splash",
                                exitTransition = {
                                    fadeOut(animationSpec = tween(400)) + scaleOut(targetScale = 0.95f, animationSpec = tween(400))
                                }
                            ) {
                                SplashScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onLaunchApp = {
                                        val dest = if (sharedViewModel.onboardingCompleted) {
                                            if (sharedViewModel.legalConsentAccepted) "main" else "legal_consent"
                                        } else {
                                            "language_selection"
                                        }
                                        navController.navigate(dest) {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(
                                route = "language_selection"
                            ) {
                                LanguageSelectionScreen(
                                    onLanguageSelected = { lang ->
                                        sharedViewModel.currentLanguage = lang
                                        val dest = if (sharedViewModel.onboardingCompleted && sharedViewModel.legalConsentAccepted) "main" else "legal_consent"
                                        navController.navigate(dest) {
                                            popUpTo("language_selection") { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = "legal_consent",
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
                                LegalConsentScreen(
                                    onAccept = {
                                        sharedViewModel.legalConsentAccepted = true
                                        sharedViewModel.onboardingCompleted = true
                                        navController.navigate("main") {
                                            popUpTo("legal_consent") { inclusive = true }
                                        }
                                    },
                                    onExitApp = {
                                        this@MainActivity.finish()
                                    },
                                    onNavigateToDoc = { doc ->
                                        if (doc == "contact") {
                                            LegalConstants.openContactEmail(this@MainActivity)
                                        } else {
                                            navController.navigate("webview/$doc")
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
                                val result = sharedViewModel.currentAnalysisResult
                                if (result != null) {
                                    AnalysisResultScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        analysis = result,
                                        isHindi = sharedViewModel.currentLanguage == "hi",
                                        onBack = {
                                            sharedViewModel.userInputText = ""
                                            navController.popBackStack()
                                        },
                                        onAnalyzeAnother = {
                                            sharedViewModel.resetScanState()
                                            navController.popBackStack("main", inclusive = false)
                                        }
                                    )
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
                                route = "inventory"
                            ) {
                                InventoryScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    viewModel = sharedViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "webview/{docId}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("docId") { type = androidx.navigation.NavType.StringType }
                                ),
                                enterTransition = {
                                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(400))
                                },
                                exitTransition = {
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400))
                                }
                            ) { backStackEntry ->
                                val docId = backStackEntry.arguments?.getString("docId") ?: "privacy"
                                val url = when(docId) {
                                    "privacy" -> LegalConstants.PRIVACY_POLICY_URL
                                    "terms" -> LegalConstants.TERMS_OF_USE_URL
                                    "disclaimer" -> LegalConstants.DISCLAIMER_URL
                                    "contact" -> LegalConstants.CONTACT_URL
                                    else -> LegalConstants.PRIVACY_POLICY_URL
                                }
                                val title = when(docId) {
                                    "privacy" -> "Privacy Policy"
                                    "terms" -> "Terms of Use"
                                    "disclaimer" -> "Security Disclaimer"
                                    "contact" -> "Contact Support"
                                    else -> "Document"
                                }
                                AppWebViewScreen(
                                    title = title,
                                    url = url,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
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
