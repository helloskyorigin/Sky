package com.skyorigin.threatshieldai

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.Locale

class AnalyticsManager private constructor(private val context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics? by lazy {
        try {
            FirebaseAnalytics.getInstance(context)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to initialize FirebaseAnalytics", e)
            null
        }
    }

    companion object {
        private const val TAG = "AnalyticsManager"

        @Volatile
        private var instance: AnalyticsManager? = null

        fun getInstance(context: Context): AnalyticsManager {
            return instance ?: synchronized(this) {
                instance ?: AnalyticsManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getDeviceLanguage(): String {
        return Locale.getDefault().language ?: "en"
    }

    /**
     * Helper to append general metadata to parameter bundles
     */
    private fun createBaseBundle(themeMode: String? = null): Bundle {
        return Bundle().apply {
            putString("app_version", getAppVersion())
            putString("device_language", getDeviceLanguage())
            if (themeMode != null) {
                putString("theme_mode", themeMode)
            }
        }
    }

    fun logEvent(eventName: String, params: Bundle? = null) {
        try {
            val finalParams = params ?: Bundle()
            // Add baseline parameters if not already present
            if (!finalParams.containsKey("app_version")) {
                finalParams.putString("app_version", getAppVersion())
            }
            if (!finalParams.containsKey("device_language")) {
                finalParams.putString("device_language", getDeviceLanguage())
            }
            firebaseAnalytics?.logEvent(eventName, finalParams)
            Log.d(TAG, "Logged event: $eventName, params: $finalParams")
        } catch (e: Throwable) {
            Log.e(TAG, "Error logging event: $eventName", e)
        }
    }

    fun logScreenView(screenName: String, themeMode: String? = null) {
        val bundle = createBaseBundle(themeMode).apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    // AUTHENTICATION EVENTS
    fun logFirstOpen() {
        logEvent("first_open")
    }

    fun logAppOpen() {
        logEvent("app_open")
        val prefs = context.getSharedPreferences("analytics_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("is_first_open", true)) {
            logFirstOpen()
            prefs.edit().putBoolean("is_first_open", false).apply()
        }
    }

    fun logLoginSuccess(method: String) {
        val bundle = Bundle().apply {
            putString("login_method", method)
        }
        logEvent("login_success", bundle)
    }

    fun logLoginFailed(method: String, error: String) {
        val bundle = Bundle().apply {
            putString("login_method", method)
            putString("error_message", error.take(100)) // Limit length for safety
        }
        logEvent("login_failed", bundle)
    }

    fun logLogout() {
        logEvent("logout")
    }

    // ONBOARDING EVENTS
    fun logSplashCompleted() {
        logEvent("splash_completed")
    }

    fun logTermsAccepted() {
        logEvent("terms_accepted")
    }

    fun logLanguageSelected(language: String) {
        val bundle = Bundle().apply {
            putString("language", language)
        }
        logEvent("language_selected", bundle)
    }

    fun logThemeSelected(theme: String) {
        val bundle = Bundle().apply {
            putString("theme", theme)
        }
        logEvent("theme_selected", bundle)
    }

    fun logOnboardingCompleted() {
        logEvent("onboarding_completed")
    }

    fun logOnboardingSkipped() {
        logEvent("onboarding_skipped")
    }

    // THREATSHIELD FEATURES
    fun logScanStarted(source: String) {
        val bundle = Bundle().apply {
            putString("scan_source", source) // e.g. "manual", "clipboard"
        }
        logEvent("scan_started", bundle)
    }

    fun logScanCompleted(riskScore: Int, classification: String, scamType: String? = null) {
        val bundle = Bundle().apply {
            putInt("risk_score", riskScore)
            putString("classification", classification) // e.g. "safe", "suspicious", "dangerous"
            if (scamType != null) {
                putString("scam_type", scamType)
            }
        }
        logEvent("scan_completed", bundle)
        
        // Log custom events for the outcome level as requested
        when (classification.lowercase()) {
            "safe" -> logEvent("safe_result")
            "suspicious" -> {
                logEvent("suspicious_result")
                if (scamType != null) {
                    logThreatDetected(scamType)
                }
            }
            "danger", "dangerous", "threat" -> {
                logEvent("dangerous_result")
                if (scamType != null) {
                    logThreatDetected(scamType)
                } else {
                    logEvent("threat_detected")
                }
            }
        }
    }

    fun logScanFailed(error: String) {
        val bundle = Bundle().apply {
            putString("error_message", error.take(100))
        }
        logEvent("scan_failed", bundle)
    }

    fun logThreatDetected(threatType: String) {
        val bundle = Bundle().apply {
            putString("threat_type", threatType) // Non-sensitive: e.g. "Phishing", "Spam", "Smishing"
        }
        logEvent("threat_detected", bundle)
    }

    // HISTORY
    fun logHistoryOpened() {
        logEvent("history_opened")
    }

    fun logHistoryItemViewed(classification: String) {
        val bundle = Bundle().apply {
            putString("classification", classification)
        }
        logEvent("history_item_viewed", bundle)
    }

    // LEARNING
    fun logLearnOpened() {
        logEvent("learn_opened")
    }

    fun logArticleOpened(articleId: String, category: String) {
        val bundle = Bundle().apply {
            putString("article_id", articleId)
            putString("category", category)
        }
        logEvent("article_opened", bundle)
    }

    // SETTINGS
    fun logSettingsOpened() {
        logEvent("settings_opened")
    }

    fun logRateAppClicked() {
        logEvent("rate_app_clicked")
    }

    fun logReportBugClicked() {
        logEvent("report_bug_clicked")
    }

    fun logRequestFeatureClicked() {
        logEvent("request_feature_clicked")
    }

    fun logSendFeedbackClicked() {
        logEvent("send_feedback_clicked")
    }

    fun logLegalPortalOpened() {
        logEvent("legal_portal_opened")
    }

    fun logPrivacyPolicyOpened() {
        logEvent("privacy_policy_opened")
    }

    fun logTermsOpened() {
        logEvent("terms_opened")
    }

    fun logSecurityDisclaimerOpened() {
        logEvent("security_disclaimer_opened")
    }

    fun logContactSupportOpened() {
        logEvent("contact_support_opened")
    }
}
