package com.skyorigin.threatshieldai

import android.content.Context
import android.text.format.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    fun getGreeting(isHindi: Boolean): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return if (isHindi) {
            when (hour) {
                in 5..11 -> "सुप्रभात 👋"
                in 12..16 -> "नमस्कार ☀️"
                in 17..20 -> "शुभ संध्या 🌇"
                else -> "शुभ रात्रि 🌙"
            }
        } else {
            when (hour) {
                in 5..11 -> "Good Morning 👋"
                in 12..16 -> "Good Afternoon ☀️"
                in 17..20 -> "Good Evening 🌇"
                else -> "Good Night 🌙"
            }
        }
    }

    fun getCurrentFormattedDate(context: Context, isHindi: Boolean = false): String {
        val date = Date()
        val dateFormat = DateFormat.getLongDateFormat(context)
        return dateFormat.format(date)
    }

    fun formatHistoryTimestamp(context: Context, timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = DateFormat.getMediumDateFormat(context)
        val timeFormat = DateFormat.getTimeFormat(context)
        return "${dateFormat.format(date)} • ${timeFormat.format(date)}"
    }

    fun getRelativeTime(context: Context, timestamp: Long, isHindi: Boolean = false): String {
        val now = System.currentTimeMillis()
        val diffMs = now - timestamp
        
        val seconds = diffMs / 1000
        val minutes = seconds / 60

        val calNow = Calendar.getInstance()
        calNow.timeInMillis = now

        val calThen = Calendar.getInstance()
        calThen.timeInMillis = timestamp

        val isSameDay = calNow.get(Calendar.YEAR) == calThen.get(Calendar.YEAR) &&
                calNow.get(Calendar.DAY_OF_YEAR) == calThen.get(Calendar.DAY_OF_YEAR)

        val isYesterday = calNow.get(Calendar.YEAR) == calThen.get(Calendar.YEAR) &&
                (calNow.get(Calendar.DAY_OF_YEAR) - calThen.get(Calendar.DAY_OF_YEAR) == 1)

        val isYesterdayCrossYear = if (!isYesterday && calNow.get(Calendar.YEAR) - calThen.get(Calendar.YEAR) == 1) {
            val maxDayThen = calThen.getActualMaximum(Calendar.DAY_OF_YEAR)
            calThen.get(Calendar.DAY_OF_YEAR) == maxDayThen && calNow.get(Calendar.DAY_OF_YEAR) == 1
        } else {
            isYesterday
        }

        val timeFormat = android.text.format.DateFormat.getTimeFormat(context)
        val formattedTime = timeFormat.format(Date(timestamp))

        return if (isHindi) {
            when {
                seconds < 60 -> "अभी-अभी"
                minutes < 60 -> "$minutes मिनट पहले"
                isSameDay -> "आज, $formattedTime"
                isYesterday || isYesterdayCrossYear -> "कल, $formattedTime"
                else -> {
                    val dateFormat = android.text.format.DateFormat.getMediumDateFormat(context)
                    "${dateFormat.format(Date(timestamp))} • $formattedTime"
                }
            }
        } else {
            when {
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes minutes ago"
                isSameDay -> "Today, $formattedTime"
                isYesterday || isYesterdayCrossYear -> "Yesterday, $formattedTime"
                else -> {
                    val dateFormat = android.text.format.DateFormat.getMediumDateFormat(context)
                    "${dateFormat.format(Date(timestamp))} • $formattedTime"
                }
            }
        }
    }

    fun getDayOfYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_YEAR)
    }

    fun getLocalEpochDay(): Long {
        val calendar = Calendar.getInstance()
        val offset = calendar.timeZone.getOffset(calendar.timeInMillis)
        return (calendar.timeInMillis + offset) / (1000 * 60 * 60 * 24L)
    }

    fun getTodayDateKey(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    fun getYesterdayDateKey(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }
}
