package com.skyorigin.threatshieldai

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ProtectionStatus(
    val titleEn: String,
    val titleHi: String,
    val descEn: String,
    val descHi: String,
    val color: Color,
    val icon: ImageVector
) {
    EXCELLENT(
        titleEn = "Excellent Protection",
        titleHi = "उत्कृष्ट सुरक्षा",
        descEn = "Your recent scans indicate a very low risk profile. Keep verifying unknown messages and stay alert.",
        descHi = "आपके हालिया स्कैन बहुत कम जोखिम का संकेत देते हैं। अनजान संदेशों की पुष्टि करते रहें और सतर्क रहें।",
        color = Color(0xFF10B981), // Emerald Green
        icon = Icons.Rounded.VerifiedUser
    ),
    GOOD(
        titleEn = "Good Protection",
        titleHi = "अच्छी सुरक्षा",
        descEn = "You're doing well. Continue verifying suspicious messages before taking action.",
        descHi = "आप अच्छा कर रहे हैं। कार्रवाई करने से पहले संदिग्ध संदेशों की पुष्टि करना जारी रखें।",
        color = Color(0xFF3B82F6), // Blue
        icon = Icons.Rounded.Security
    ),
    NEEDS_ATTENTION(
        titleEn = "Needs Attention",
        titleHi = "ध्यान देने की आवश्यकता है",
        descEn = "Several suspicious messages have been detected. Review recommendations carefully.",
        descHi = "कई संदिग्ध संदेशों का पता चला है। सिफारिशों की सावधानीपूर्वक समीक्षा करें।",
        color = Color(0xFFF59E0B), // Amber
        icon = Icons.Rounded.Warning
    ),
    HIGH_RISK(
        titleEn = "High Risk",
        titleHi = "उच्च जोखिम",
        descEn = "Multiple dangerous messages have been detected. Exercise extreme caution before opening links or sharing information.",
        descHi = "कई खतरनाक संदेशों का पता चला है। लिंक खोलने या जानकारी साझा करने से पहले अत्यधिक सावधानी बरतें।",
        color = Color(0xFFEF4444), // Red
        icon = Icons.Rounded.GppMaybe
    ),
    UNKNOWN(
        titleEn = "No Scan Data",
        titleHi = "कोई स्कैन डेटा नहीं",
        descEn = "Complete your first scan to generate your protection profile.",
        descHi = "अपना सुरक्षा प्रोफाइल बनाने के लिए अपना पहला स्कैन पूरा करें।",
        color = Color(0xFF94A3B8), // Slate
        icon = Icons.Rounded.HelpOutline
    )
}

object ProtectionStatusHelper {
    fun calculateStatus(history: List<MessageAnalysis>): ProtectionStatus {
        if (history.isEmpty()) return ProtectionStatus.UNKNOWN
        
        val totalScans = history.size
        val safeCount = history.count { it.score in 0..19 }
        val score = ((safeCount.toFloat() / totalScans) * 100).toInt().coerceIn(0, 100)
        
        return when {
            score >= 90 -> ProtectionStatus.EXCELLENT
            score >= 70 -> ProtectionStatus.GOOD
            score >= 40 -> ProtectionStatus.NEEDS_ATTENTION
            else -> ProtectionStatus.HIGH_RISK
        }
    }

    fun getRecommendations(history: List<MessageAnalysis>, isHindi: Boolean): List<String> {
        return if (isHindi) {
            listOf(
                "✓ दैनिक सुरक्षा चुनौती सक्षम करें",
                "✓ संदिग्ध संदेशों को तुरंत स्कैन करें",
                "✓ अज्ञात लिंक पर क्लिक करने से बचें",
                "✓ संदिग्ध घोटालों की रिपोर्ट करें"
            )
        } else {
            listOf(
                "✓ Enable Daily Challenge",
                "✓ Scan suspicious messages",
                "✓ Avoid clicking unknown links",
                "✓ Report scams"
            )
        }
    }
}
