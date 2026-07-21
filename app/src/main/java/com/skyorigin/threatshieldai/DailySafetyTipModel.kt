package com.skyorigin.threatshieldai

data class SafetyTip(
    val id: Int, // 1 to 100+
    val titleEn: String,
    val titleHi: String,
    val explanationEn: String,
    val explanationHi: String,
    val whyItMattersEn: String,
    val whyItMattersHi: String,
    val staySafeActionEn: String,
    val staySafeActionHi: String,
    val iconName: String // Maps to specific ImageVector
)
