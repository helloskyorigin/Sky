package com.skyorigin.threatshieldai

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LANGUAGE = stringPreferencesKey("language")
        val THEME = stringPreferencesKey("theme")
        val LEGAL_CONSENT_ACCEPTED = booleanPreferencesKey("legal_consent_accepted")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_DISPLAY_NAME = stringPreferencesKey("user_display_name")
        val USER_AVATAR_PRESET = intPreferencesKey("user_avatar_preset")
        val USER_COUNTRY = stringPreferencesKey("user_country")
        val IS_GUEST = booleanPreferencesKey("is_guest")
        val NOTIF_ONBOARDING_SHOWN = booleanPreferencesKey("notif_onboarding_shown")
        val CHALLENGE_DAY = intPreferencesKey("challenge_day")
        val CHALLENGE_COMPLETED_TODAY = booleanPreferencesKey("challenge_completed_today")
        val SELECTED_OPTION_INDEX = intPreferencesKey("selected_option_index")
        val CHALLENGE_STREAK = intPreferencesKey("challenge_streak")
        val LAST_NOTIFICATION_DATE = stringPreferencesKey("last_notification_date")
        val TOTAL_COMPLETED = intPreferencesKey("total_completed")
        val LAST_CHALLENGE_DATE = stringPreferencesKey("last_challenge_date")
        val LONGEST_STREAK = intPreferencesKey("longest_streak")
        val TOTAL_XP = intPreferencesKey("total_xp")
        val APP_OPEN_COUNT = intPreferencesKey("app_open_count")
        val USED_DAYS = stringSetPreferencesKey("used_days")
        val LAST_FEEDBACK_DISMISSED_TIMESTAMP = longPreferencesKey("last_feedback_dismissed_timestamp")
        val LAST_FEEDBACK_SUBMITTED_TIMESTAMP = longPreferencesKey("last_feedback_submitted_timestamp")
        val HAS_RATED_PLAY_STORE = booleanPreferencesKey("has_rated_play_store")
        val TOTAL_SCANS_COMPLETED_COUNT = intPreferencesKey("total_scans_completed_count")
        val FIRST_RUN = booleanPreferencesKey("first_run")
        val TODAY_DATE = stringPreferencesKey("today_date")
        val TODAY_SCAN_COUNT = intPreferencesKey("today_scan_count")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
        val REMAINING_SCANS = intPreferencesKey("remaining_scans")
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    val languageFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "en"
    }

    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME] ?: "DARK"
    }

    val legalConsentAcceptedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LEGAL_CONSENT_ACCEPTED] ?: false
    }

    val userEmailFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL] ?: ""
    }

    val userDisplayNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_DISPLAY_NAME] ?: ""
    }

    val userAvatarPresetFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[USER_AVATAR_PRESET] ?: 0
    }

    val userCountryFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_COUNTRY] ?: ""
    }

    val isGuestFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_GUEST] ?: false
    }

    val notifOnboardingShownFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIF_ONBOARDING_SHOWN] ?: false
    }

    val challengeDayFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CHALLENGE_DAY] ?: 1
    }

    val challengeCompletedTodayFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[CHALLENGE_COMPLETED_TODAY] ?: false
    }

    val selectedOptionIndexFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_OPTION_INDEX] ?: -1
    }

    val challengeStreakFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CHALLENGE_STREAK] ?: 0
    }

    val lastNotificationDateFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_NOTIFICATION_DATE]
    }

    val totalCompletedFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TOTAL_COMPLETED] ?: 0
    }

    val lastChallengeDateFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_CHALLENGE_DATE]
    }

    val longestStreakFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LONGEST_STREAK] ?: 0
    }

    val totalXpFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TOTAL_XP] ?: 0
    }

    val appOpenCountFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[APP_OPEN_COUNT] ?: 0
    }

    val usedDaysFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[USED_DAYS] ?: emptySet()
    }

    val lastFeedbackDismissedTimestampFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[LAST_FEEDBACK_DISMISSED_TIMESTAMP] ?: 0L
    }

    val lastFeedbackSubmittedTimestampFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[LAST_FEEDBACK_SUBMITTED_TIMESTAMP] ?: 0L
    }

    val hasRatedPlayStoreFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[HAS_RATED_PLAY_STORE] ?: false
    }

    val totalScansCompletedCountFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TOTAL_SCANS_COMPLETED_COUNT] ?: 0
    }

    val todayDateFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TODAY_DATE] ?: ""
    }

    val todayScanCountFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TODAY_SCAN_COUNT] ?: 0
    }

    val lastResetDateFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LAST_RESET_DATE] ?: ""
    }

    val remainingScansFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMAINING_SCANS] ?: 2
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setLanguage(language: String?) {
        context.dataStore.edit { preferences ->
            if (language != null) {
                preferences[LANGUAGE] = language
            } else {
                preferences.remove(LANGUAGE)
            }
        }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }

    suspend fun setLegalConsentAccepted(accepted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LEGAL_CONSENT_ACCEPTED] = accepted
        }
    }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun setUserDisplayName(displayName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_DISPLAY_NAME] = displayName
        }
    }

    suspend fun setUserAvatarPreset(preset: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_AVATAR_PRESET] = preset
        }
    }

    suspend fun setUserCountry(country: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_COUNTRY] = country
        }
    }

    suspend fun setIsGuest(isGuest: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_GUEST] = isGuest
        }
    }

    suspend fun setNotifOnboardingShown(shown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIF_ONBOARDING_SHOWN] = shown
        }
    }

    suspend fun setChallengeDay(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[CHALLENGE_DAY] = day
        }
    }

    suspend fun setChallengeCompletedToday(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CHALLENGE_COMPLETED_TODAY] = completed
        }
    }

    suspend fun setSelectedOptionIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_OPTION_INDEX] = index
        }
    }

    suspend fun saveChallengeSubmission(
        selectedIndex: Int,
        completed: Boolean,
        streak: Int,
        longest: Int,
        completedCount: Int,
        xp: Int,
        challengeDate: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_OPTION_INDEX] = selectedIndex
            preferences[CHALLENGE_COMPLETED_TODAY] = completed
            preferences[CHALLENGE_STREAK] = streak
            preferences[LONGEST_STREAK] = longest
            preferences[TOTAL_COMPLETED] = completedCount
            preferences[TOTAL_XP] = xp
            preferences[LAST_CHALLENGE_DATE] = challengeDate
        }
    }

    suspend fun resetChallengeForNewDay(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[CHALLENGE_DAY] = day
            preferences[CHALLENGE_COMPLETED_TODAY] = false
            preferences[SELECTED_OPTION_INDEX] = -1
        }
    }

    suspend fun setChallengeStreak(streak: Int) {
        context.dataStore.edit { preferences ->
            preferences[CHALLENGE_STREAK] = streak
        }
    }

    suspend fun setLastNotificationDate(dateStr: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_NOTIFICATION_DATE] = dateStr
        }
    }

    suspend fun setTotalCompleted(total: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_COMPLETED] = total
        }
    }

    suspend fun setLastChallengeDate(dateStr: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_CHALLENGE_DATE] = dateStr
        }
    }

    suspend fun setLongestStreak(streak: Int) {
        context.dataStore.edit { preferences ->
            preferences[LONGEST_STREAK] = streak
        }
    }

    suspend fun setTotalXp(xp: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_XP] = xp
        }
    }

    suspend fun setAppOpenCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[APP_OPEN_COUNT] = count
        }
    }

    suspend fun addUsedDay(day: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[USED_DAYS] ?: emptySet()
            preferences[USED_DAYS] = current + day
        }
    }

    suspend fun setLastFeedbackDismissedTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_FEEDBACK_DISMISSED_TIMESTAMP] = timestamp
        }
    }

    suspend fun setLastFeedbackSubmittedTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_FEEDBACK_SUBMITTED_TIMESTAMP] = timestamp
        }
    }

    suspend fun setHasRatedPlayStore(rated: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_RATED_PLAY_STORE] = rated
        }
    }

    suspend fun setTotalScansCompletedCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_SCANS_COMPLETED_COUNT] = count
        }
    }

    suspend fun setTodayDate(dateStr: String) {
        context.dataStore.edit { preferences ->
            preferences[TODAY_DATE] = dateStr
        }
    }

    suspend fun setTodayScanCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[TODAY_SCAN_COUNT] = count
        }
    }

    suspend fun setLastResetDate(dateStr: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_RESET_DATE] = dateStr
        }
    }

    suspend fun setRemainingScans(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMAINING_SCANS] = count
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
