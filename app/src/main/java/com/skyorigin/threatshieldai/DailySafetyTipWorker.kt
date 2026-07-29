package com.skyorigin.threatshieldai

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailySafetyTipWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val todayStr = getTodayString()
        val lastNotifDate = sp.getString("last_tip_notif_date", "")
        val lastOpenedDate = sp.getString("last_opened_tip_date", "")

        if (lastOpenedDate != todayStr && lastNotifDate != todayStr) {
            NotificationHelper.showDailySafetyTipNotification(context)
            sp.edit().putString("last_tip_notif_date", todayStr).apply()
        }

        // Schedule next one
        schedule(context)

        return Result.success()
    }

    private fun getTodayString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }

    companion object {
        fun schedule(context: Context) {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 14) // 2:00 PM local time
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.DAY_OF_YEAR, 1)
            }

            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

            val dailyWorkRequest = OneTimeWorkRequestBuilder<DailySafetyTipWorker>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "DailySafetyTipWorker",
                ExistingWorkPolicy.REPLACE,
                dailyWorkRequest
            )
        }
    }
}
