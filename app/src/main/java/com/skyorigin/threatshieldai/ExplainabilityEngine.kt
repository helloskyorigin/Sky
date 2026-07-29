package com.skyorigin.threatshieldai

import org.json.JSONArray
import org.json.JSONObject

data class ExplainabilityResult(
    val summary: String,
    val whyFlagged: List<String>
)

object ExplainabilityEngine {

    val VALID_WHY_REASONS = listOf(
        "Bank impersonation detected",
        "Government impersonation detected",
        "Brand impersonation detected",
        "Fake delivery notification",
        "Credential theft attempt",
        "Password requested",
        "OTP requested",
        "Card details requested",
        "Personal information requested",
        "Urgency tactic detected",
        "Fear tactic detected",
        "Reward or prize bait",
        "Suspicious shortened URL",
        "Suspicious domain",
        "Look-alike domain",
        "Official domain detected",
        "Known malicious URL",
        "URL not found in checked threat databases",
        "No suspicious language detected",
        "Informational notification",
        "No sensitive information requested"
    )

    fun generateExplanation(
        verdict: String,
        riskScore: Int,
        confidence: Int,
        extractedUrls: List<String>,
        urlResults: List<UrlThreatResult>,
        detectedIndicators: List<String>,
        aiAnalysisReason: String
    ): ExplainabilityResult {
        val whyList = mutableListOf<String>()

        val normVerdict = verdict.uppercase()
        val isDanger = normVerdict == "DANGER"
        val isSuspicious = normVerdict == "SUSPICIOUS" || normVerdict == "WARNING"
        val isSafe = normVerdict == "SAFE"

        val indicatorsLower = detectedIndicators.map { it.lowercase() }
        val allContext = (detectedIndicators.joinToString(" ") + " " + aiAnalysisReason).lowercase()

        val hasMaliciousUrl = urlResults.any {
            it.riskLevel.equals("MALICIOUS", ignoreCase = true) ||
            it.webRiskVerdict.equals("MALICIOUS", ignoreCase = true) ||
            it.phishtankVerdict.equals("MALICIOUS", ignoreCase = true) ||
            it.urlhausVerdict.equals("MALICIOUS", ignoreCase = true)
        }
        val hasSuspiciousUrl = urlResults.any {
            it.riskLevel.equals("SUSPICIOUS", ignoreCase = true) ||
            it.webRiskVerdict.equals("SUSPICIOUS", ignoreCase = true)
        }

        if (isDanger) {
            if (hasMaliciousUrl && extractedUrls.isNotEmpty()) {
                whyList.add("Known malicious URL")
            } else if (hasSuspiciousUrl && extractedUrls.isNotEmpty()) {
                whyList.add("Suspicious domain")
            }

            if (allContext.contains("bank impersonation") || indicatorsLower.any { it.contains("bank impersonat") }) {
                whyList.add("Bank impersonation detected")
            } else if (allContext.contains("government impersonation") || indicatorsLower.any { it.contains("government impersonat") }) {
                whyList.add("Government impersonation detected")
            } else if (indicatorsLower.any { it.contains("brand impersonat") }) {
                whyList.add("Brand impersonation detected")
            } else if (indicatorsLower.any { it.contains("parcel") || it.contains("courier") || it.contains("delivery scam") }) {
                whyList.add("Fake delivery notification")
            }

            if (allContext.contains("otp requested") || indicatorsLower.any { it.contains("otp requested") || it.contains("otp trap") }) {
                whyList.add("OTP requested")
            }
            if (allContext.contains("password requested") || indicatorsLower.any { it.contains("password requested") }) {
                whyList.add("Password requested")
            }
            if (allContext.contains("card details requested") || indicatorsLower.any { it.contains("card details") }) {
                whyList.add("Card details requested")
            }
            // Require explicit evidence for credential theft attempt
            if ((allContext.contains("credential") && (allContext.contains("harvest") || allContext.contains("theft") || allContext.contains("steal"))) || 
                indicatorsLower.any { it.contains("credential harvesting") || it.contains("credential theft") }) {
                whyList.add("Credential theft attempt")
            }
            // Require explicit evidence for urgency tactic
            if ((allContext.contains("urgency") && (allContext.contains("pressure") || allContext.contains("threat") || allContext.contains("block"))) || 
                indicatorsLower.any { it.contains("urgency tactic") || it.contains("urgent threat") }) {
                whyList.add("Urgency tactic detected")
            }
            if (allContext.contains("fear tactic") || indicatorsLower.any { it.contains("fear tactic") }) {
                whyList.add("Fear tactic detected")
            }
            if (allContext.contains("reward bait") || allContext.contains("lottery scam") || indicatorsLower.any { it.contains("lottery scam") || it.contains("prize bait") }) {
                whyList.add("Reward or prize bait")
            }

            if (whyList.isEmpty()) {
                whyList.add("No suspicious language detected")
            }
        } else if (isSuspicious) {
            val isCheckedAndClean = urlResults.isNotEmpty() && urlResults.all { 
                it.webRiskVerdict.equals("NO_KNOWN_THREAT", ignoreCase = true) && it.webRiskStatus.equals("OK", ignoreCase = true)
            }
            
            if (hasSuspiciousUrl && extractedUrls.isNotEmpty()) {
                whyList.add("Suspicious domain")
            } else if (extractedUrls.isNotEmpty() && !isCheckedAndClean) {
                whyList.add("URL not found in checked threat databases")
            } else if (extractedUrls.isNotEmpty() && isCheckedAndClean) {
                whyList.add("Official domain detected")
            }

            if (indicatorsLower.any { it.contains("urgency tactic") || it.contains("urgent pressure") }) {
                whyList.add("Urgency tactic detected")
            }
            if (indicatorsLower.any { it.contains("personal information requested") }) {
                whyList.add("Personal information requested")
            }
            if (extractedUrls.isNotEmpty() && indicatorsLower.any { it.contains("shortened url") }) {
                whyList.add("Suspicious shortened URL")
            }

            if (whyList.isEmpty()) {
                whyList.add("Informational notification")
                whyList.add("No sensitive information requested")
            }
        } else if (isSafe) {
            whyList.add("No suspicious language detected")
            whyList.add("Informational notification")
            whyList.add("No sensitive information requested")
            if (extractedUrls.isNotEmpty()) {
                whyList.add("Official domain detected")
            }
        } else {
            if (extractedUrls.isNotEmpty()) {
                whyList.add("Official domain detected")
            }
            whyList.add("No sensitive information requested")
        }

        if (extractedUrls.isEmpty()) {
            val urlSpecificReasons = setOf(
                "Suspicious shortened URL",
                "Suspicious domain",
                "Look-alike domain",
                "Official domain detected",
                "Known malicious URL",
                "URL not found in checked threat databases"
            )
            whyList.removeAll(urlSpecificReasons)
        }

        val finalWhyList = whyList.distinct().take(5)

        val summaryStr = when {
            isDanger -> {
                if (hasMaliciousUrl) {
                    "This message contains a known malicious link verified by threat databases. Do not click the link or provide any personal information."
                } else if (finalWhyList.contains("Bank impersonation detected")) {
                    "This message impersonates a banking institution to trick recipients into revealing sensitive financial details."
                } else {
                    "This message appears to impersonate a trusted organization and attempts to pressure the recipient into revealing sensitive information."
                }
            }
            isSuspicious -> {
                "Caution is advised because some suspicious indicators were detected in this message, but the evidence is not fully conclusive."
            }
            isSafe -> {
                "This message appears to be a legitimate notification. No convincing signs of phishing, impersonation or credential theft were detected."
            }
            else -> {
                "There is insufficient context in this message to determine a conclusive security rating. Caution is recommended."
            }
        }

        return ExplainabilityResult(
            summary = summaryStr,
            whyFlagged = finalWhyList
        )
    }

    fun parseJsonResponse(jsonStr: String): ExplainabilityResult? {
        return try {
            val obj = JSONObject(jsonStr)
            val summary = obj.optString("summary", "").trim()
            val whyArray = obj.optJSONArray("whyFlagged")
            val whyList = mutableListOf<String>()
            if (whyArray != null) {
                for (i in 0 until whyArray.length()) {
                    val item = whyArray.getString(i).trim()
                    if (item.isNotEmpty() && item in VALID_WHY_REASONS) {
                        whyList.add(item)
                    }
                }
            }
            if (summary.isNotEmpty()) {
                ExplainabilityResult(summary, whyList.take(5))
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
