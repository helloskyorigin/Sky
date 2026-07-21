package com.skyorigin.threatshieldai

data class ScamCategory(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val shortDescEn: String,
    val shortDescHi: String,
    val iconName: String, // Maps to icon
    val whatIsItEn: String,
    val whatIsItHi: String,
    val howItWorksEn: List<String>,
    val howItWorksHi: List<String>,
    val warningSignsEn: List<String>,
    val warningSignsHi: List<String>,
    val howToStaySafeEn: List<String>,
    val howToStaySafeHi: List<String>,
    val quickSummaryEn: String,
    val quickSummaryHi: String
)
