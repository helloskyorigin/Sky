package com.example

enum class DecisionAction {
    SAFE,
    DANGER,
    SUSPICIOUS,
    NOT_SURE
}

data class Decision(
    val action: DecisionAction,
    val score: Int,
    val confidence: Int,
    val category: String, // "Safe", "Suspicious", "Danger"
    val reason: String,
    val matchedRules: List<String>
)

object DecisionManager {
    private const val HIGH_CONFIDENCE_THRESHOLD = 80

    /**
     * Step 1: Evaluate the results from local offline rule engines.
     * Determines if we can make an immediate Safe or Danger decision without contacting any external API.
     */
    fun evaluateOffline(
        safeResult: EngineResult,
        dangerResult: EngineResult,
        reviewResult: ReviewEngineResult
    ): DecisionAction {
        // Danger engine high confidence check
        if (dangerResult.confidence >= HIGH_CONFIDENCE_THRESHOLD) {
            return DecisionAction.DANGER
        }

        // Safe engine high confidence check
        if (safeResult.confidence >= HIGH_CONFIDENCE_THRESHOLD) {
            return DecisionAction.SAFE
        }

        // If review is recommended or either engine matched rule but with lower confidence, request fallback/review
        if (reviewResult.reviewRecommended || safeResult.matchedRules.isNotEmpty() || dangerResult.matchedRules.isNotEmpty()) {
            return DecisionAction.NOT_SURE
        }

        return DecisionAction.NOT_SURE
    }

    /**
     * Step 2: Evaluate URL reputation results if urls are found.
     * If URL reputation is confirmed malicious, we bypass AI and decide DANGER.
     */
    fun evaluateUrlReputation(urlConfirmedMalicious: Boolean): DecisionAction {
        return if (urlConfirmedMalicious) {
            DecisionAction.DANGER
        } else {
            DecisionAction.NOT_SURE
        }
    }

    /**
     * Step 3: Map final output based on AI result (the ultimate fallback).
     */
    fun evaluateAiResult(aiResult: GeminiResult): DecisionAction {
        return when (aiResult.status.lowercase()) {
            "safe" -> DecisionAction.SAFE
            "danger" -> DecisionAction.DANGER
            else -> DecisionAction.SUSPICIOUS
        }
    }
}
