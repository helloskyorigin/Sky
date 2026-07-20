#!/bin/bash
cat << 'INNER_EOF' > app/src/main/java/com/example/ScamLensViewModel.kt
package com.example

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

class ScamLensViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var userInputText by mutableStateOf("")

    private val _currentThemeMode = mutableStateOf(
        ThemeMode.valueOf(prefs.getString("theme", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
    )
    val currentThemeModeState: androidx.compose.runtime.State<ThemeMode> = _currentThemeMode

    var currentThemeMode: ThemeMode
        get() = _currentThemeMode.value
        set(value) {
            _currentThemeMode.value = value
            prefs.edit().putString("theme", value.name).apply()
        }

    private val _notifOnboardingShown = mutableStateOf(prefs.getBoolean("notif_onboarding_shown", false))
    val notifOnboardingShownState: androidx.compose.runtime.State<Boolean> = _notifOnboardingShown
    var notifOnboardingShown: Boolean
        get() = _notifOnboardingShown.value
        set(value) {
            _notifOnboardingShown.value = value
            prefs.edit().putBoolean("notif_onboarding_shown", value).apply()
        }

    val analysesHistory = mutableStateListOf<MessageAnalysis>()
    var currentAnalysisResult by mutableStateOf<MessageAnalysis?>(null)

    init {
        loadHistory()
        if (analysesHistory.isEmpty()) {
            // Provide some defaults if empty
            analysesHistory.addAll(
                listOf(
                    MessageAnalysis(
                        text = "+91 98765 43210: Congratulations! You won a $1,000 Walmart Gift Card. Click here to claim your reward instantly: http://scamlink.com/reward",
                        date = "2m ago",
                        status = "Danger",
                        score = 88
                    ),
                    MessageAnalysis(
                        text = "Bank Offer: Update your secure banking profile to avoid suspension of your debit card. Click here: http://sbi-secure-update.net/login",
                        date = "1h ago",
                        status = "Suspicious",
                        score = 62
                    )
                )
            )
        }
    }

    fun addAnalysisResult(result: MessageAnalysis) {
        analysesHistory.add(0, result)
        saveHistory()
    }

    fun deleteAnalysisResult(result: MessageAnalysis) {
        analysesHistory.remove(result)
        saveHistory()
    }

    private fun loadHistory() {
        try {
            val jsonStr = prefs.getString("history", null) ?: return
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<MessageAnalysis>()
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
            analysesHistory.clear()
            analysesHistory.addAll(list)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveHistory() {
        try {
            val jsonArray = JSONArray()
            analysesHistory.forEach { analysis ->
                val obj = JSONObject()
                obj.put("text", analysis.text)
                obj.put("date", analysis.date)
                obj.put("status", analysis.status)
                obj.put("score", analysis.score)
                obj.put("summary", analysis.summary)
                obj.put("explain15", analysis.explain15)
                
                val reasonsArray = JSONArray()
                analysis.reasons.forEach { reasonsArray.put(it) }
                obj.put("reasons", reasonsArray)
                
                val linksArray = JSONArray()
                analysis.links.forEach { linksArray.put(it) }
                obj.put("links", linksArray)
                
                jsonArray.put(obj)
            }
            prefs.edit().putString("history", jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
INNER_EOF
