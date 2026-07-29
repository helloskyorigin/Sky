package com.skyorigin.threatshieldai

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

class DailyChallengeWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = UserPreferencesRepository(context)
        val lastNotifDate = prefs.lastNotificationDateFlow.first()
        val todayStr = getTodayString()

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_YEAR)
        val clampedDay = if (day > 365) 365 else if (day < 1) 1 else day
        val sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isCompletedToday = sp.getBoolean("challenge_completed_$clampedDay", false)

        if (lastNotifDate != todayStr && !isCompletedToday) {
            NotificationHelper.showDailyChallengeNotification(context)
            prefs.setLastNotificationDate(todayStr)
        }

        // Schedule next one
        schedule(context)

        return Result.success()
    }

    private fun getTodayString(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    companion object {
        fun schedule(context: Context) {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.DAY_OF_YEAR, 1)
            }

            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

            val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyChallengeWorker>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "DailyChallengeWorker",
                ExistingWorkPolicy.REPLACE,
                dailyWorkRequest
            )
        }
    }
}
