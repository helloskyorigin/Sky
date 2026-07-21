package com.skyorigin.threatshieldai

data class ScamExample(
    val id: Int,
    val category: String, // One of the 15 categories
    val difficulty: String, // "Easy", "Medium", "Hard"
    val titleEn: String,
    val titleHi: String,
    val messageEn: String,
    val messageHi: String,
    val dangerEn: String,
    val dangerHi: String,
    val redFlagsEn: List<String>,
    val redFlagsHi: List<String>,
    val safeResponseEn: String,
    val safeResponseHi: String
)
