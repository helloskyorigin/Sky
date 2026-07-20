package com.example

data class MessageAnalysis(
    val text: String,
    val date: String,
    val status: String,
    val score: Int,
    val summary: String = "",
    val reasons: List<String> = emptyList(),
    val links: List<String> = emptyList(),
    val explain15: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val scamType: String = "",
    val urlStatuses: List<String> = emptyList(),
    val advice: List<String> = emptyList(),
    val confidence: Int = 0,
    val signals: List<String> = emptyList()
)

// Extension functions for multi-lingual and formatting support across screens

fun MessageAnalysis.getTextVerdict(): String {
    if (links.isEmpty()) {
        return when (status.lowercase()) {
            "danger" -> "Danger"
            "warning" -> "Warning"
            "suspicious" -> "Suspicious"
            else -> "Safe"
        }
    }
    val metadata = urlStatuses.firstOrNull { it.startsWith("METADATA:") }
    if (metadata != null) {
        try {
            val json = org.json.JSONObject(metadata.substring("METADATA:".length))
            return json.optString("text_verdict", "Safe")
        } catch (e: Exception) {}
    }
    return when {
        score > 75 -> "Danger"
        score > 45 -> "Warning"
        score > 20 -> "Suspicious"
        else -> "Safe"
    }
}

fun MessageAnalysis.getUrlVerdict(): String {
    if (links.isEmpty()) return "Safe"
    val metadata = urlStatuses.firstOrNull { it.startsWith("METADATA:") }
    if (metadata != null) {
        try {
            val json = org.json.JSONObject(metadata.substring("METADATA:".length))
            return json.optString("url_verdict", "Safe")
        } catch (e: Exception) {}
    }
    var hasDanger = false
    var hasUnknown = false
    var hasSuspicious = false
    urlStatuses.forEach { statusStr ->
        if (!statusStr.startsWith("METADATA:")) {
            try {
                val json = org.json.JSONObject(statusStr)
                val risk = json.optString("risk_level", "safe").lowercase()
                if (risk == "danger") hasDanger = true
                if (risk == "unknown" || risk == "failed" || risk == "unverified") hasUnknown = true
                if (risk == "suspicious") hasSuspicious = true
            } catch (e: Exception) {}
        }
    }
    return when {
        hasDanger -> "Danger"
        hasUnknown -> "Unknown"
        hasSuspicious -> "Suspicious"
        else -> "Safe"
    }
}

fun MessageAnalysis.getLocalizedStatus(isHindi: Boolean): String {
    return if (isHindi) {
        when (status.lowercase()) {
            "safe" -> "Safe"
            "suspicious" -> "Suspicious"
            "warning" -> "Warning"
            "danger", "unsafe" -> "Danger"
            else -> status
        }
    } else {
        status
    }
}

fun MessageAnalysis.getRiskLevelLabel(isHindi: Boolean): String {
    return if (isHindi) {
        when (status.lowercase()) {
            "safe" -> "Low Risk"
            "suspicious" -> "Medium Risk"
            "warning" -> "High Risk"
            "danger", "unsafe" -> "Severe Risk"
            else -> "Unknown Risk"
        }
    } else {
        when (status.lowercase()) {
            "safe" -> "Low Risk"
            "suspicious" -> "Medium Risk"
            "warning" -> "High Risk"
            "danger", "unsafe" -> "Severe Risk"
            else -> "Unknown Risk"
        }
    }
}

fun MessageAnalysis.getConfidenceLabel(isHindi: Boolean): String {
    val conf = if (confidence > 0) confidence else score
    return if (isHindi) {
        when {
            conf >= 80 -> "High (${conf}%)"
            conf >= 50 -> "Medium (${conf}%)"
            else -> "Low (${conf}%)"
        }
    } else {
        when {
            conf >= 80 -> "High (${conf}%)"
            conf >= 50 -> "Medium (${conf}%)"
            else -> "Low (${conf}%)"
        }
    }
}

fun MessageAnalysis.getLocalizedSummary(isHindi: Boolean): String {
    if (summary.isNotEmpty()) return summary
    return if (isHindi) {
        when (status.lowercase()) {
            "safe" -> "यह Message Safe प्रतीत होता है।"
            "suspicious" -> "इस Message में कुछ Suspicious Patterns हैं।"
            "warning" -> "यह Message खतरनाक हो सकता है।"
            "danger", "unsafe" -> "इस Message में Scam के स्पष्ट लक्षण हैं!"
            else -> "Analysis पूरा हुआ।"
        }
    } else {
        when (status.lowercase()) {
            "safe" -> "This message appears to be safe."
            "suspicious" -> "This message has some suspicious patterns."
            "warning" -> "This message might be dangerous."
            "danger", "unsafe" -> "This message shows clear signs of a scam!"
            else -> "Analysis complete."
        }
    }
}

fun MessageAnalysis.getLocalizedExplain15(isHindi: Boolean): String {
    if (explain15.isNotEmpty()) return explain15
    return if (isHindi) {
        when (status.lowercase()) {
            "safe" -> "कोई suspicious activity नहीं मिली। आप safely आगे बढ़ सकते हैं।"
            "suspicious" -> "सावधान रहें। Sender की identity verify किए बिना कोई action न लें।"
            "warning" -> "सावधान! इसमें खतरनाक Link या Pattern हो सकते हैं।"
            "danger", "unsafe" -> "सावधान! यह एक Scam है। किसी भी Link पर click या share न करें।"
            else -> ""
        }
    } else {
        when (status.lowercase()) {
            "safe" -> "No suspicious activity found. You can proceed safely."
            "suspicious" -> "Proceed with caution. Do not take action without verifying the sender."
            "warning" -> "Warning! This may contain dangerous links or patterns."
            "danger", "unsafe" -> "Danger! This is a scam. Do not click any links or share information."
            else -> ""
        }
    }
}

fun MessageAnalysis.getMappedSignals(isHindi: Boolean): List<String> {
    if (signals.isNotEmpty()) return signals
    return reasons
}

fun MessageAnalysis.getLocalAdvice(isHindi: Boolean): List<String> {
    if (advice.isNotEmpty()) return advice
    return if (isHindi) {
        when (status.lowercase()) {
            "safe" -> listOf("Safe sharing practices अपनाएं।", "Suspicious messages से सावधान रहें।")
            "suspicious" -> listOf("Sender की identity verify करें।", "कोई भी personal details share न करें।", "Official Helpline से संपर्क करें।")
            "warning" -> listOf("सावधान रहें, Link पर Click न करें।", "Sender को Block करें।")
            "danger", "unsafe" -> listOf("इस Sender को तुरंत Block करें।", "Link पर click न करें।", "OTP या bank details कभी share न करें।")
            else -> emptyList()
        }
    } else {
        when (status.lowercase()) {
            "safe" -> listOf("Practice safe sharing habits.", "Stay alert for unsolicited requests.")
            "suspicious" -> listOf("Verify sender credentials.", "Do not disclose personal data.", "Check official help channels.")
            "warning" -> listOf("Be very careful. Do not click links.", "Consider blocking the sender.")
            "danger", "unsafe" -> listOf("Block this sender immediately.", "Do not click any links.", "Never share OTP or banking details.")
            else -> emptyList()
        }
    }
}
