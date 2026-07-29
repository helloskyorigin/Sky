package com.skyorigin.threatshieldai

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class VerdictInfo(
    val titleEn: String,
    val titleHi: String,
    val color: Color,
    val colorInt: Int,
    val subtitleEn: String,
    val subtitleHi: String,
    val icon: ImageVector
) {
    fun getTitle(isHindi: Boolean): String = if (isHindi) titleHi else titleEn
    fun getSubtitle(isHindi: Boolean): String = if (isHindi) subtitleHi else subtitleEn
}

object VerdictMapper {
    fun getVerdictForScore(score: Int): VerdictInfo {
        return when (score.coerceIn(0, 100)) {
            in 0..19 -> VerdictInfo(
                titleEn = "SAFE",
                titleHi = "सुरक्षित",
                color = Color(0xFF22C55E), // Green
                colorInt = 0xFF22C55E.toInt(),
                subtitleEn = "Safe message pattern detected.",
                subtitleHi = "सुरक्षित संदेश पैटर्न पाया गया।",
                icon = Icons.Rounded.Check
            )
            in 20..39 -> VerdictInfo(
                titleEn = "LOW RISK",
                titleHi = "कम जोखिम",
                color = Color(0xFF0EA5E9), // Sky Blue / Cyan (distinct caution)
                colorInt = 0xFF0EA5E9.toInt(),
                subtitleEn = "Some caution is recommended.",
                subtitleHi = "कुछ सावधानी बरतने की सलाह दी जाती है।",
                icon = Icons.Rounded.Info
            )
            in 40..69 -> VerdictInfo(
                titleEn = "SUSPICIOUS",
                titleHi = "संदिग्ध",
                color = Color(0xFFF59E0B), // Amber / Orange
                colorInt = 0xFFF59E0B.toInt(),
                subtitleEn = "Suspicious activity indicators detected.",
                subtitleHi = "संदिग्ध गतिविधि के संकेत मिले।",
                icon = Icons.Rounded.Warning
            )
            else -> VerdictInfo( // 70..100
                titleEn = "HIGH RISK",
                titleHi = "उच्च जोखिम",
                color = Color(0xFFEF4444), // Red
                colorInt = 0xFFEF4444.toInt(),
                subtitleEn = "High-risk scam indicators detected.",
                subtitleHi = "उच्च जोखिम वाले घोटाले के संकेत मिले।",
                icon = Icons.Rounded.PriorityHigh
            )
        }
    }
}
