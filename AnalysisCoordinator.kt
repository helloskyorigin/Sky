package com.example

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
            val aiResult = SecurityAnalysisEngine.analyzeMessageWithGroq(text, isHindi)

            MessageAnalysis(
                text = text,
                date = "", // formatted in UI
                status = mapAiStatus(aiResult.status),
                score = aiResult.riskScore,
                summary = aiResult.summary,
                reasons = aiResult.redFlags,
                links = urls,
                explain15 = aiResult.explain15,
                timestamp = System.currentTimeMillis(),
                scamType = aiResult.scamType,
                urlStatuses = urlStatuses,
                advice = aiResult.advice,
                confidence = aiResult.confidence,
                signals = aiResult.signals
            )
        } catch (e: Exception) {
            Log.e(TAG, "AI Engine failed", e)
            MessageAnalysis(
                text = text,
                date = "",
                status = "Suspicious",
                score = 50,
                summary = if (isHindi) "Scan fail रहा" else "Scan Failed",
                reasons = listOf(e.message ?: "Connection Error"),
                links = urls,
                explain15 = if (isHindi) "हम Analysis नहीं कर सके। कृपया अपना Internet Connection जांचें।" 
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
            else -> "Suspicious"
        }
    }
}
