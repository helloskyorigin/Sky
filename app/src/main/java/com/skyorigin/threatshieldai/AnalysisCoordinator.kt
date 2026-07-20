package com.skyorigin.threatshieldai

import android.util.Log

object AnalysisCoordinator {
    private const val TAG = "AnalysisCoordinator"
    
    // V1 ARCHITECTURE: DeepSeek ONLY Mode
    const val AI_ONLY_MODE = true

    val safeEngine = SafeRuleEngine()
    val dangerEngine = DangerRuleEngine()
    val reviewEngine = ReviewRuleEngine()

    suspend fun analyze(text: String, isHindi: Boolean): MessageAnalysis {
        // 1. Message Normalizer
        val normalizedText = MessageNormalizer.normalize(text)

        // 2. Offline Detection Engine (Must remain for V2, but bypass evaluation in V1)
        if (!AI_ONLY_MODE) {
            safeEngine.evaluate(normalizedText)
            dangerEngine.evaluate(normalizedText)
            reviewEngine.evaluate(normalizedText)
        }

        // 3. Extract URLs for reference
        val urls = UrlDetectionEngine.extractUrls(text)
        val urlStatuses = mutableListOf<String>()

        // 4. Decision Maker - AI ONLY for V1
        return try {
            val aiResult = SecurityAnalysisEngine.performHybridAnalysis(null, text, isHindi)

            // Populate URL statuses
            val urlStatusStrings = aiResult.urlsFound.map { threatRes ->
                val json = org.json.JSONObject()
                json.put("url", threatRes.originalUrl)
                json.put("risk_level", threatRes.riskLevel)
                json.put("webrisk", threatRes.webRiskVerdict)
                json.put("phishtank", threatRes.phishtankVerdict)
                json.put("urlhaus", threatRes.urlhausVerdict)
                json.toString()
            }
            
            // Add METADATA string for text/url verdicts if needed
            val metaJson = org.json.JSONObject()
            metaJson.put("text_verdict", aiResult.textVerdict)
            metaJson.put("url_verdict", aiResult.urlVerdict)
            val fullUrlStatuses = urlStatusStrings.toMutableList()
            fullUrlStatuses.add("METADATA:" + metaJson.toString())
            
            MessageAnalysis(
                text = text,
                date = "", // formatted in UI
                status = mapAiStatus(aiResult.verdict),
                score = aiResult.riskScore,
                summary = aiResult.summary,
                reasons = aiResult.textSignals,
                links = urls,
                explain15 = aiResult.finalReason,
                timestamp = System.currentTimeMillis(),
                scamType = aiResult.scamType,
                urlStatuses = fullUrlStatuses,
                advice = aiResult.advice,
                confidence = aiResult.confidence,
                signals = aiResult.textSignals
            )
        } catch (e: Exception) {
            Log.e(TAG, "AI Engine failed", e)
            MessageAnalysis(
                text = text,
                date = "",
                status = "Suspicious",
                score = 50,
                summary = "Scan Failed",
                reasons = listOf(e.message ?: "Connection Error"),
                links = urls,
                explain15 = if (isHindi) "हम Analysis नहीं कर सके। कृपया अपना Internet connection check करें।" 
                            else "We couldn't analyze the message. Please check your internet connection.",
                timestamp = System.currentTimeMillis(),
                scamType = "System Error",
                urlStatuses = urlStatuses,
                advice = if (isHindi) listOf("Internet check करें", "बाद में प्रयास करें") 
                         else listOf("Check internet", "Try again later"),
                confidence = 0
            )
        }
    }

    private fun mapAiStatus(aiStatus: String): String {
        return when (aiStatus.uppercase()) {
            "DANGER" -> "Danger"
            "SAFE" -> "Safe"
            "UNABLE TO DETERMINE", "UNABLE_TO_DETERMINE", "INSUFFICIENT_EVIDENCE" -> "Unable to Determine"
            else -> "Suspicious"
        }
    }
}
