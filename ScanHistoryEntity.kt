package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val date: String,
    val status: String,
    val score: Int,
    val summary: String,
    val reasons: String,
    val links: String,
    val explain15: String,
    val timestamp: Long,
    val scamType: String,
    val urlStatuses: String,
    val advice: String,
    val confidence: Int,
    val signals: String
)

fun List<String>.toJsonString(): String {
    val arr = org.json.JSONArray()
    this.forEach { arr.put(it) }
    return arr.toString()
}

fun String.toListOfString(): List<String> {
    if (this.isEmpty()) return emptyList()
    val list = mutableListOf<String>()
    try {
        val arr = org.json.JSONArray(this)
        for (i in 0 until arr.length()) {
            list.add(arr.getString(i))
        }
    } catch (e: Exception) {
        if (this.contains(",")) {
            return this.split(",").map { it.trim() }
        }
    }
    return list
}

fun MessageAnalysis.toEntity(): ScanHistoryEntity {
    return ScanHistoryEntity(
        text = this.text,
        date = this.date,
        status = this.status,
        score = this.score,
        summary = this.summary,
        reasons = this.reasons.toJsonString(),
        links = this.links.toJsonString(),
        explain15 = this.explain15,
        timestamp = this.timestamp,
        scamType = this.scamType,
        urlStatuses = this.urlStatuses.toJsonString(),
        advice = this.advice.toJsonString(),
        confidence = this.confidence,
        signals = this.signals.toJsonString()
    )
}

fun ScanHistoryEntity.toDomain(): MessageAnalysis {
    return MessageAnalysis(
        text = this.text,
        date = this.date,
        status = this.status,
        score = this.score,
        summary = this.summary,
        reasons = this.reasons.toListOfString(),
        links = this.links.toListOfString(),
        explain15 = this.explain15,
        timestamp = this.timestamp,
        scamType = this.scamType,
        urlStatuses = this.urlStatuses.toListOfString(),
        advice = this.advice.toListOfString(),
        confidence = this.confidence,
        signals = this.signals.toListOfString()
    )
}
