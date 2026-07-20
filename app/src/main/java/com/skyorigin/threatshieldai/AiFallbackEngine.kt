package com.skyorigin.threatshieldai

object AiFallbackEngine {
    /**
     * Executes the semantic AI review flow when offline rules are inconclusive.
     * Delegates to the core SecurityAnalysisEngine to preserve existing tested configurations.
     */
    suspend fun analyze(text: String, isHindi: Boolean): GeminiResult {
        return SecurityAnalysisEngine.analyzeMessageWithGroq(text, isHindi)
    }
}
