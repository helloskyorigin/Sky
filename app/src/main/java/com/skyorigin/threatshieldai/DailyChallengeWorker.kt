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

        if (lastNotifDate != todayStr) {
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
            val prefs = UserPreferencesRepository(context)
            
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()

            dueDate.set(Calendar.HOUR_OF_DAY, 9)
            dueDate.set(Calendar.MINUTE, 0)
            dueDate.set(Calendar.SECOND, 0)
            dueDate.set(Calendar.MILLISECOND, 0)

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
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
            
            // Handle missed notification immediately if today's hasn't been shown and it is past 9 AM
            val calendarNow = Calendar.getInstance()
            val calendar9AM = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (calendarNow.after(calendar9AM)) {
                val todayWorkRequest = OneTimeWorkRequestBuilder<DailyChallengeWorker>().build()
                WorkManager.getInstance(context).enqueueUniqueWork(
                    "DailyChallengeWorker_Missed",
                    ExistingWorkPolicy.KEEP,
                    todayWorkRequest
                )
            }
        }
    }
}
