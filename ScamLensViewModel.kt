package com.example

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class ScamLensViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ScamLensViewModel"
    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val preferencesRepo = UserPreferencesRepository(application)
    private val database = AppDatabase.getDatabase(application)
    private val scanHistoryDao = database.scanHistoryDao()
    val inventoryDao = database.inventoryDao()

    var userInputText by mutableStateOf("")

    val appOpenCountState = mutableStateOf(0)
    val usedDaysState = mutableStateOf<Set<String>>(emptySet())
    val lastFeedbackDismissedTimestampState = mutableStateOf(0L)
    val lastFeedbackSubmittedTimestampState = mutableStateOf(0L)
    val hasRatedPlayStoreState = mutableStateOf(false)
    val totalScansCompletedCountState = mutableStateOf(0)
    val todayScanCountState = mutableStateOf(0)
    val todayDateState = mutableStateOf("")
    val lastResetDateState = mutableStateOf("")

    private val _remainingScans = mutableStateOf(3)
    val remainingScansState: androidx.compose.runtime.State<Int> = mutableStateOf(3) // Temporarily unlimited scans for testing
    var remainingScans: Int
        get() = 3 // Temporarily unlimited scans for testing
        set(value) {
            // Ignored for now
        }

    private val _currentThemeMode = mutableStateOf(
        ThemeMode.valueOf(prefs.getString("theme", ThemeMode.LIGHT.name) ?: ThemeMode.LIGHT.name)
    )
    val currentThemeModeState: androidx.compose.runtime.State<ThemeMode> = _currentThemeMode

    var currentThemeMode: ThemeMode
        get() = _currentThemeMode.value
        set(value) {
            _currentThemeMode.value = value
            viewModelScope.launch {
                preferencesRepo.setTheme(value.name)
            }
        }

    private val _notifOnboardingShown = mutableStateOf(prefs.getBoolean("notif_onboarding_shown", false))
    val notifOnboardingShownState: androidx.compose.runtime.State<Boolean> = _notifOnboardingShown
    var notifOnboardingShown: Boolean
        get() = _notifOnboardingShown.value
        set(value) {
            _notifOnboardingShown.value = value
            viewModelScope.launch {
                preferencesRepo.setNotifOnboardingShown(value)
            }
        }

    val analysesHistory = mutableStateListOf<MessageAnalysis>()
    var currentAnalysisResult by mutableStateOf<MessageAnalysis?>(null)
    var urlScanProgressState by mutableStateOf<UrlScanProgress?>(null)

    var shouldFocusInput by mutableStateOf(false)
    var requestedTabRoute by mutableStateOf<String?>(null)

    sealed interface ScanState {
        object Idle : ScanState
        data class Scanning(val progress: UrlScanProgress? = null) : ScanState
        data class Success(val result: MessageAnalysis) : ScanState
        data class Failed(val error: String) : ScanState
    }

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    fun resetScanState() {
        cancelAnalysis()
        _scanState.value = ScanState.Idle
        userInputText = ""
        currentAnalysisResult = null
        urlScanProgressState = null
        shouldFocusInput = true
        requestedTabRoute = "scan"
    }

    fun prepareForScan(text: String) {
        cancelAnalysis()
        _scanState.value = ScanState.Idle
        userInputText = text
        currentAnalysisResult = null
        urlScanProgressState = null
    }


    // Language and Onboarding states
    private val _currentLanguage = mutableStateOf<String?>(prefs.getString("language", "en"))
    val currentLanguageState: androidx.compose.runtime.State<String?> = _currentLanguage
    var currentLanguage: String?
        get() = _currentLanguage.value
        set(value) {
            _currentLanguage.value = value
            viewModelScope.launch {
                preferencesRepo.setLanguage(value)
            }
        }

    private val _onboardingCompleted = mutableStateOf(prefs.getBoolean("onboarding_completed", false))
    var onboardingCompleted: Boolean
        get() = _onboardingCompleted.value
        set(value) {
            _onboardingCompleted.value = value
            viewModelScope.launch {
                preferencesRepo.setOnboardingCompleted(value)
            }
        }

    private val _legalConsentAccepted = mutableStateOf(prefs.getBoolean("legal_consent_accepted", false))
    var legalConsentAccepted: Boolean
        get() = _legalConsentAccepted.value
        set(value) {
            _legalConsentAccepted.value = value
            viewModelScope.launch {
                preferencesRepo.setLegalConsentAccepted(value)
            }
        }

    var isHistoryLoading by mutableStateOf(false)

    // Daily Challenge States
    var currentChallengeDay by mutableStateOf(prefs.getInt("challenge_day", 1))
    var challengeCompletedToday by mutableStateOf(prefs.getBoolean("challenge_completed_today", false))
    var selectedOptionIndex by mutableStateOf(
        if (prefs.contains("selected_option_index")) prefs.getInt("selected_option_index", -1) else -1
    )
    var challengeStreak by mutableStateOf(prefs.getInt("challenge_streak", 0))
    var totalCompleted by mutableStateOf(prefs.getInt("total_completed", 0))
    var longestStreak by mutableStateOf(prefs.getInt("longest_streak", 0))
    var totalXp by mutableStateOf(prefs.getInt("total_xp", 0))
    var lastQuizDate by mutableStateOf<String?>(null)

    val currentChallenge: DailyChallenge
        get() = getChallengeForDay(currentChallengeDay)

    var analysisJob: kotlinx.coroutines.Job? = null

    init {
        // Run migration and collection flows
        viewModelScope.launch {
            try {
                val isMigrated = prefs.getBoolean("v2_migration_done", false)
                if (!isMigrated) {
                    if (prefs.contains("onboarding_completed") || prefs.contains("theme") || prefs.contains("language")) {
                        val onboarding = prefs.getBoolean("onboarding_completed", false)
                        preferencesRepo.setOnboardingCompleted(onboarding)

                        val themeStr = prefs.getString("theme", "LIGHT") ?: "LIGHT"
                        preferencesRepo.setTheme(themeStr)

                        val lang = prefs.getString("language", "en")
                        preferencesRepo.setLanguage(lang)

                        val consent = prefs.getBoolean("legal_consent_accepted", false)
                        preferencesRepo.setLegalConsentAccepted(consent)

                        val notifShow = prefs.getBoolean("notif_onboarding_shown", false)
                        preferencesRepo.setNotifOnboardingShown(notifShow)

                        val chDay = prefs.getInt("challenge_day", 1)
                        preferencesRepo.setChallengeDay(chDay)

                        val chComp = prefs.getBoolean("challenge_completed_today", false)
                        preferencesRepo.setChallengeCompletedToday(chComp)

                        val selOpt = prefs.getInt("selected_option_index", -1)
                        preferencesRepo.setSelectedOptionIndex(selOpt)

                        val streak = prefs.getInt("challenge_streak", 0)
                        preferencesRepo.setChallengeStreak(streak)
                    }

                    // Migrate history list from SharedPreferences to Room
                    try {
                        val oldHistory = loadHistoryFromSharedPrefs()
                        oldHistory.forEach { scan ->
                            scanHistoryDao.insertScan(scan.toEntity())
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error migrating old history list to Room: ", e)
                    }

                    prefs.edit().putBoolean("v2_migration_done", true).apply()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Migration exception: ", e)
            }

            // Keep local VM states in sync with DataStore changes reactively
            launch {
                preferencesRepo.onboardingCompletedFlow.collect { completed ->
                    _onboardingCompleted.value = completed
                }
            }
            launch {
                preferencesRepo.languageFlow.collect { lang ->
                    _currentLanguage.value = lang
                }
            }
            launch {
                preferencesRepo.themeFlow.collect { themeStr ->
                    _currentThemeMode.value = ThemeMode.valueOf(themeStr)
                }
            }
            launch {
                preferencesRepo.legalConsentAcceptedFlow.collect { accepted ->
                    _legalConsentAccepted.value = accepted
                }
            }
            launch {
                preferencesRepo.notifOnboardingShownFlow.collect { shown ->
                    _notifOnboardingShown.value = shown
                }
            }
            launch {
                preferencesRepo.challengeDayFlow.collect { day ->
                    currentChallengeDay = day
                }
            }
            launch {
                preferencesRepo.challengeCompletedTodayFlow.collect { comp ->
                    challengeCompletedToday = comp
                }
            }
            launch {
                preferencesRepo.selectedOptionIndexFlow.collect { index ->
                    selectedOptionIndex = index
                }
            }
            launch {
                preferencesRepo.challengeStreakFlow.collect { streak ->
                    challengeStreak = streak
                }
            }
            launch {
                preferencesRepo.totalCompletedFlow.collect { total ->
                    totalCompleted = total
                }
            }
            launch {
                preferencesRepo.longestStreakFlow.collect { longest ->
                    longestStreak = longest
                }
            }
            launch {
                preferencesRepo.totalXpFlow.collect { xp ->
                    totalXp = xp
                }
            }
            launch {
                preferencesRepo.appOpenCountFlow.collect { count ->
                    appOpenCountState.value = count
                }
            }
            launch {
                preferencesRepo.usedDaysFlow.collect { days ->
                    usedDaysState.value = days
                }
            }
            launch {
                preferencesRepo.lastFeedbackDismissedTimestampFlow.collect { timestamp ->
                    lastFeedbackDismissedTimestampState.value = timestamp
                }
            }
            launch {
                preferencesRepo.lastFeedbackSubmittedTimestampFlow.collect { timestamp ->
                    lastFeedbackSubmittedTimestampState.value = timestamp
                }
            }
            launch {
                preferencesRepo.hasRatedPlayStoreFlow.collect { rated ->
                    hasRatedPlayStoreState.value = rated
                }
            }
            launch {
                preferencesRepo.remainingScansFlow.collect { scans ->
                    _remainingScans.value = scans
                }
            }
            launch {
                preferencesRepo.totalScansCompletedCountFlow.collect { count ->
                    totalScansCompletedCountState.value = count
                }
            }
            launch {
                preferencesRepo.todayScanCountFlow.collect { count ->
                    todayScanCountState.value = count
                }
            }
            launch {
                preferencesRepo.todayDateFlow.collect { dStr ->
                    todayDateState.value = dStr
                }
            }
            launch {
                preferencesRepo.lastResetDateFlow.collect { rStr ->
                    lastResetDateState.value = rStr
                }
            }
            launch {
                checkAndResetDailyLimit(application)
                while (true) {
                    kotlinx.coroutines.delay(5000)
                    checkAndResetDailyLimit(application)
                }
            }
            launch {
                try {
                    val opens = preferencesRepo.appOpenCountFlow.first()
                    preferencesRepo.setAppOpenCount(opens + 1)
                    val todayStr = getTodayDateString()
                    preferencesRepo.addUsedDay(todayStr)
                } catch (e: Exception) {
                    Log.e(TAG, "Error recording app open or day", e)
                }
            }
            launch {
                preferencesRepo.lastQuizDateFlow.collect { lastDate ->
                    lastQuizDate = lastDate
                    val todayStr = getTodayString()
                    if (lastDate != todayStr) {
                        // It's a new day! If completed yesterday, advance day.
                        if (challengeCompletedToday) {
                            currentChallengeDay += 1
                            challengeCompletedToday = false
                            selectedOptionIndex = -1
                            preferencesRepo.setChallengeDay(currentChallengeDay)
                            preferencesRepo.setChallengeCompletedToday(false)
                            preferencesRepo.setSelectedOptionIndex(-1)
                        }
                        
                        // Check if streak is broken
                        val yesterdayStr = getYesterdayString()
                        if (lastDate != yesterdayStr && challengeStreak > 0) {
                            challengeStreak = 0
                            preferencesRepo.setChallengeStreak(0)
                        }
                    }
                }
            }

            // Load and reactively collect history from Room
            launch {
                isHistoryLoading = true
                scanHistoryDao.getAllHistory().collect { entities ->
                    val domainList = entities.map { it.toDomain() }
                    
                    analysesHistory.clear()
                    analysesHistory.addAll(domainList)
                    
                    isHistoryLoading = false
                }
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            scanHistoryDao.clearAll()
            analysesHistory.clear()
            try {
                preferencesRepo.setTotalScansCompletedCount(0)
            } catch (e: Exception) {
                Log.e(TAG, "Error resetting total scans completed count", e)
            }
        }
    }

    private fun getDefaultScans(): List<MessageAnalysis> {
        return listOf(
            MessageAnalysis(
                text = "+91 98765 43210: Congratulations! You won a $1,000 Walmart Gift Card. Click here to claim your reward instantly: http://scamlink.com/reward",
                date = "2m ago",
                status = "Danger",
                score = 88,
                summary = "Deceptive reward scam pretending to offer gift cards.",
                reasons = listOf("Contains phishing link", "Unsolicited promotional sender"),
                explain15 = "Fake prize notification. Do not click the link."
            ),
            MessageAnalysis(
                text = "Bank Offer: Update your secure banking profile to avoid suspension of your debit card. Click here: http://sbi-secure-update.net/login",
                date = "1h ago",
                status = "Suspicious",
                score = 62,
                summary = "Suspicious bank alert claiming account suspension.",
                reasons = listOf("Urgently asks to update details", "Link points to non-official bank domain"),
                explain15 = "Urgent card block threat. Do not share credentials."
            )
        )
    }

    fun addAnalysisResult(result: MessageAnalysis) {
        analysesHistory.add(0, result)
        viewModelScope.launch {
            scanHistoryDao.insertScan(result.toEntity())
            try {
                val currentCount = preferencesRepo.totalScansCompletedCountFlow.first()
                preferencesRepo.setTotalScansCompletedCount(currentCount + 1)
                
                // Increment Daily Scan count
                val todayStr = getTodayDateString()
                val storedTodayDate = preferencesRepo.todayDateFlow.first()
                if (storedTodayDate != todayStr) {
                    preferencesRepo.setTodayDate(todayStr)
                    preferencesRepo.setTodayScanCount(1)
                    preferencesRepo.setLastResetDate(todayStr)
                } else {
                    val todayCount = preferencesRepo.todayScanCountFlow.first()
                    preferencesRepo.setTodayScanCount(todayCount + 1)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error incrementing scan count", e)
            }
        }
    }

    fun deleteAnalysisResult(result: MessageAnalysis) {
        analysesHistory.remove(result)
        viewModelScope.launch {
            scanHistoryDao.deleteScanByText(result.text)
        }
    }

    fun cancelAnalysis() {
        analysisJob?.cancel()
        analysisJob = null
    }

    fun performRealAnalysis(
        context: Context,
        text: String,
        isHindi: Boolean
    ) {
        // Guard against empty message
        val normalized = MessageNormalizer.normalize(text)
        if (normalized.isEmpty()) {
            _scanState.value = ScanState.Failed("API_ERROR")
            return
        }

        urlScanProgressState = null
        cancelAnalysis()
        _scanState.value = ScanState.Scanning(null)

        analysisJob = viewModelScope.launch {
            try {
                val result = SecurityAnalysisEngine.performHybridAnalysis(context, text, isHindi) { progress ->
                    urlScanProgressState = progress
                    _scanState.value = ScanState.Scanning(progress)
                }
                val analysis = MessageAnalysis(
                    text = text,
                    date = "Just now",
                    status = result.verdict,
                    score = result.riskScore,
                    summary = result.summary,
                    reasons = result.textSignals,
                    links = result.urlsFound.map { it.originalUrl },
                    explain15 = result.finalReason,
                    timestamp = System.currentTimeMillis(),
                    scamType = result.scamType,
                    urlStatuses = result.urlsFound.map { urlResult ->
                        org.json.JSONObject().apply {
                            put("original_url", urlResult.originalUrl)
                            put("normalized_url", urlResult.normalizedUrl)
                            put("expanded_url", urlResult.expandedUrl ?: "")
                            put("web_risk_verdict", urlResult.webRiskVerdict)
                            put("phishtank_verdict", urlResult.phishtankVerdict)
                            put("urlhaus_verdict", urlResult.urlhausVerdict)
                            put("risk_level", urlResult.riskLevel)
                            put("threat_type", urlResult.threatType ?: "")
                            put("scan_time", urlResult.scanTime)
                            put("confidence", urlResult.confidence)
                            put("final_url_verdict", urlResult.finalUrlVerdict)
                            put("web_risk_status", urlResult.webRiskStatus)
                            put("phishtank_status", urlResult.phishtankStatus)
                            put("urlhaus_status", urlResult.urlhausStatus)
                        }.toString()
                    } + ("METADATA:" + org.json.JSONObject().apply {
                        put("text_verdict", result.textVerdict)
                        put("url_verdict", result.urlVerdict)
                        put("processing_time", result.processingTime)
                    }.toString()),
                    advice = result.advice,
                    confidence = result.confidence,
                    signals = result.textSignals
                )
                if (remainingScans > 0) {
                    remainingScans -= 1
                }
                _scanState.value = ScanState.Success(analysis)
            } catch (e: CancellationException) {
                throw e
            } catch (e: InternetConnectionException) {
                _scanState.value = ScanState.Failed("INTERNET_DISCONNECTED")
            } catch (e: ServiceUnavailableException) {
                _scanState.value = ScanState.Failed("SERVICE_UNAVAILABLE")
            } catch (e: ConnectionLostException) {
                _scanState.value = ScanState.Failed("CONNECTION_LOST")
            } catch (e: ApiTimeoutException) {
                _scanState.value = ScanState.Failed("TIMEOUT")
            } catch (e: ApiErrorException) {
                _scanState.value = ScanState.Failed("API_ERROR")
            } catch (e: Exception) {
                _scanState.value = ScanState.Failed("API_ERROR")
            }
        }
    }

    fun completeChallenge(selectedIndex: Int) {
        if (challengeCompletedToday) return

        this.selectedOptionIndex = selectedIndex
        this.challengeCompletedToday = true
        
        challengeStreak += 1
        
        if (challengeStreak > longestStreak) {
            longestStreak = challengeStreak
        }
        
        totalCompleted += 1
        totalXp += 50 // Award 50 XP
        
        val todayStr = getTodayString()
        lastQuizDate = todayStr

        viewModelScope.launch {
            preferencesRepo.setSelectedOptionIndex(selectedIndex)
            preferencesRepo.setChallengeCompletedToday(true)
            preferencesRepo.setChallengeStreak(challengeStreak)
            preferencesRepo.setLongestStreak(longestStreak)
            preferencesRepo.setTotalCompleted(totalCompleted)
            preferencesRepo.setTotalXp(totalXp)
            preferencesRepo.setLastQuizDate(todayStr)
        }
    }

    fun moveToNextDayChallenge() {
        currentChallengeDay += 1
        challengeCompletedToday = false
        selectedOptionIndex = -1
        
        viewModelScope.launch {
            preferencesRepo.setChallengeDay(currentChallengeDay)
            preferencesRepo.setChallengeCompletedToday(false)
            preferencesRepo.setSelectedOptionIndex(-1)
        }
    }

    private fun getChallengeForDay(day: Int): DailyChallenge {
        val index = (day - 1) % challengesList.size
        return challengesList[index]
    }

    private fun getTodayString(): String {
        val calendar = java.util.Calendar.getInstance()
        return "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH)}-${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
    }

    private fun getYesterdayString(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        return "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH)}-${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
    }

    private fun loadHistoryFromSharedPrefs(): List<MessageAnalysis> {
        val list = mutableListOf<MessageAnalysis>()
        try {
            val jsonStr = prefs.getString("history", null) ?: return emptyList()
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                
                val reasonsList = mutableListOf<String>()
                val reasonsArray = obj.optJSONArray("reasons")
                if (reasonsArray != null) {
                    for (j in 0 until reasonsArray.length()) {
                        reasonsList.add(reasonsArray.getString(j))
                    }
                }
                
                val linksList = mutableListOf<String>()
                val linksArray = obj.optJSONArray("links")
                if (linksArray != null) {
                    for (j in 0 until linksArray.length()) {
                        linksList.add(linksArray.getString(j))
                    }
                }
                
                list.add(
                    MessageAnalysis(
                        text = obj.getString("text"),
                        date = obj.getString("date"),
                        status = obj.getString("status"),
                        score = obj.getInt("score"),
                        summary = obj.optString("summary", ""),
                        reasons = reasonsList,
                        links = linksList,
                        explain15 = obj.optString("explain15", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    private fun getTodayDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }

    fun checkAndResetDailyLimit(context: Context? = null) {
        viewModelScope.launch {
            try {
                val todayStr = getTodayDateString()
                val storedTodayDate = preferencesRepo.todayDateFlow.first()
                if (storedTodayDate != todayStr) {
                    preferencesRepo.setTodayDate(todayStr)
                    preferencesRepo.setTodayScanCount(0)
                    preferencesRepo.setLastResetDate(todayStr)
                    
                    context?.let {
                        NotificationHelper.showDailyScansResetNotification(it)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking and resetting daily scan limit: ", e)
            }
        }
    }

    fun dismissFeedbackLater() {
        viewModelScope.launch {
            preferencesRepo.setLastFeedbackDismissedTimestamp(System.currentTimeMillis())
        }
    }

    fun shouldShowFeedbackPopup(): Boolean {
        if (hasRatedPlayStoreState.value) return false

        val now = System.currentTimeMillis()
        val lastSuccess = lastFeedbackSubmittedTimestampState.value
        if (lastSuccess > 0 && (now - lastSuccess < 90L * 24 * 60 * 60 * 1000)) {
            return false
        }

        val lastLater = lastFeedbackDismissedTimestampState.value
        if (lastLater > 0 && (now - lastLater < 14L * 24 * 60 * 60 * 1000)) {
            return false
        }

        val scansCondition = totalScansCompletedCountState.value >= 5 || analysesHistory.size >= 5
        val daysCondition = usedDaysState.value.size >= 3

        return scansCondition || daysCondition
    }

    fun submitUserFeedback(
        rating: Int,
        category: String?,
        message: String?,
        onComplete: (Boolean) -> Unit
    ) {
        val app = getApplication<Application>()
        val feedbackId = java.util.UUID.randomUUID().toString()
        val appVersion = getAppVersion(app)
        val androidVersion = android.os.Build.VERSION.RELEASE ?: "Unknown"
        val deviceModel = android.os.Build.MODEL ?: "Unknown"
        val manufacturer = android.os.Build.MANUFACTURER ?: "Unknown"
        val lang = currentLanguage ?: "en"
        val theme = currentThemeMode.name
        val scansCount = totalScansCompletedCountState.value
        val opensCount = appOpenCountState.value
        
        val entity = FeedbackEntity(
            feedbackId = feedbackId,
            rating = rating,
            category = category,
            message = message,
            appVersion = appVersion,
            androidVersion = androidVersion,
            deviceModel = deviceModel,
            manufacturer = manufacturer,
            language = lang,
            theme = theme,
            totalScans = scansCount,
            appOpenCount = opensCount,
            createdAt = System.currentTimeMillis()
        )
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val database = AppDatabase.getDatabase(app)
            database.feedbackDao().insertFeedback(entity)
            
            FeedbackSyncWorker.enqueue(app)
            
            preferencesRepo.setLastFeedbackSubmittedTimestamp(System.currentTimeMillis())
            if (rating == 5) {
                preferencesRepo.setHasRatedPlayStore(true)
            }
            
            launch(kotlinx.coroutines.Dispatchers.Main) {
                onComplete(true)
            }
        }
    }

    // Inventory Management Integration
    val allInventoryItems: kotlinx.coroutines.flow.Flow<List<InventoryItemEntity>> = inventoryDao.getAllItems()

    fun insertInventoryItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            inventoryDao.insertItem(item)
        }
    }

    fun updateInventoryItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            inventoryDao.updateItem(item)
        }
    }

    fun deleteInventoryItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            inventoryDao.deleteItem(item)
        }
    }

    fun clearAllInventory() {
        viewModelScope.launch {
            inventoryDao.clearAll()
        }
    }
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

private val challengesList = listOf(
    DailyChallenge(
        id = "CH_01",
        category = "PHISHING",
        difficulty = "Easy",
        difficultyHi = "Easy",
        scamTypeLabel = "Phishing Link",
        scamTypeLabelHi = "Phishing Link",
        question = LocalizedText(
            en = "You receive an SMS: 'Dear SBI user, your NetBanking account will be blocked today. Please click http://sbi-verify.org/login to re-verify your KYC immediately.' Is this a scam?",
            hi = "आपको एक SMS मिलता है: 'प्रिय SBI User, आपका NetBanking Account आज block हो जाएगा। कृपया अपनी KYC को तुरंत Verify करने के लिए http://sbi-verify.org/login पर click करें।' क्या यह एक Scam है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "This is a typical phishing scam. Banks will never ask you to click a non-official link (like sbi-verify.org) to update KYC or netbanking.",
            hi = "यह एक typical Phishing Scam है। Banks कभी भी आपसे KYC या NetBanking update करने के लिए किसी non-official Link (जैसे sbi-verify.org) पर click करने के लिए नहीं कहेंगे।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Phishing scams make up over 40% of all reported digital frauds in India annually.",
            hi = "क्या आप जानते हैं? भारत में हर साल report होने वाले कुल digital frauds में Phishing Scams का हिस्सा 40% से अधिक है।"
        )
    ),
    DailyChallenge(
        id = "CH_02",
        category = "LOTTERY",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Fake Reward",
        scamTypeLabelHi = "Fake Reward",
        question = LocalizedText(
            en = "An SMS states: 'Congratulations! You have won a cash reward of ₹50,000. Send ₹500 processing fees to claim it.' Is this a scam?",
            hi = "एक SMS में लिखा है: 'बधाई हो! आपने ₹50,000 का Cash Reward जीता है। इसे प्राप्त करने के लिए ₹500 Processing Fees भेजें।' क्या यह एक Scam है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any reward that asks you to pay money upfront in order to receive your prize is a scam.",
            hi = "कोई भी Reward जो आपको अपना prize प्राप्त करने के लिए upfront payment या Processing Fees देने के लिए कहे, वह Scam है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Official sweepstakes and lotteries never require winners to pay fees upfront.",
            hi = "क्या आप जानते हैं? Official sweepstakes और lotteries में कभी भी winners को upfront payment करने की आवश्यकता नहीं होती है।"
        )
    )
,
    DailyChallenge(
        id = "CH_03",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 3: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 3: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_04",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 4: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 4: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_05",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 5: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 5: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_06",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 6: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 6: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_07",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 7: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 7: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_08",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 8: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 8: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_09",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 9: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 9: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_10",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 10: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 10: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_11",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 11: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 11: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_12",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 12: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 12: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_13",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 13: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 13: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_14",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 14: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 14: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_15",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 15: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 15: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_16",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 16: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 16: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_17",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 17: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 17: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_18",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 18: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 18: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_19",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 19: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 19: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_20",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 20: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 20: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_21",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 21: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 21: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_22",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 22: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 22: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_23",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 23: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 23: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_24",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 24: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 24: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_25",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 25: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 25: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_26",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 26: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 26: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_27",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 27: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 27: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_28",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 28: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 28: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_29",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 29: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 29: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    ),
    DailyChallenge(
        id = "CH_30",
        category = "SCAM",
        difficulty = "Medium",
        difficultyHi = "Medium",
        scamTypeLabel = "Suspicious Message",
        scamTypeLabelHi = "संदिग्ध संदेश",
        question = LocalizedText(
            en = "Message 30: 'Your account is locked. Click here to unlock.' Is this a scam?",
            hi = "संदेश 30: 'आपका अकाउंट लॉक हो गया है। अनलॉक करने के लिए यहां क्लिक करें।' क्या यह एक स्कैम है?"
        ),
        options = listOf(
            LocalizedText(en = "Yes, it's a scam", hi = "हाँ, यह एक Scam है"),
            LocalizedText(en = "No, it's safe", hi = "नहीं, यह Safe है")
        ),
        correctOptionIndex = 0,
        explanation = LocalizedText(
            en = "Any message asking you to click a link to unlock your account is highly suspicious.",
            hi = "कोई भी संदेश जो आपको अपना अकाउंट अनलॉक करने के लिए लिंक पर क्लिक करने के लिए कहता है, वह अत्यधिक संदिग्ध है।"
        ),
        didYouKnow = LocalizedText(
            en = "Did you know? Always verify account statuses directly through the official app or website.",
            hi = "क्या आप जानते हैं? हमेशा आधिकारिक ऐप या वेबसाइट के माध्यम से सीधे अकाउंट की स्थिति की पुष्टि करें।"
        )
    )
)
