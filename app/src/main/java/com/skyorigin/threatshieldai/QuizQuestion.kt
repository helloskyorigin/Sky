package com.skyorigin.threatshieldai

data class QuizQuestion(
    val id: Int,
    val topic: String, // e.g., "Phishing", "OTP Fraud", etc.
    val questionEn: String,
    val questionHi: String,
    val optionsEn: List<String>,
    val optionsHi: List<String>,
    val correctAnswerIndex: Int, // 0 to 3
    val explanationEn: String,
    val explanationHi: String
)
