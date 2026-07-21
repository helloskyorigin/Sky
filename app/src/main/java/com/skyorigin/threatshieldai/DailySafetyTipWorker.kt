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

        if (lastNotifDate != todayStr) {
            NotificationHelper.showDailySafetyTipNotification(context)
            sp.edit().putString("last_tip_notif_date", todayStr).apply()
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
            val dueDate = Calendar.getInstance()

            dueDate.set(Calendar.HOUR_OF_DAY, 12)
            dueDate.set(Calendar.MINUTE, 0)
            dueDate.set(Calendar.SECOND, 0)
            dueDate.set(Calendar.MILLISECOND, 0)

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
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
            
            // Handle missed notification immediately if today's hasn't been shown and it is past 12:00 PM
            val calendarNow = Calendar.getInstance()
            val calendar12PM = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (calendarNow.after(calendar12PM)) {
                val todayWorkRequest = OneTimeWorkRequestBuilder<DailySafetyTipWorker>().build()
                WorkManager.getInstance(context).enqueueUniqueWork(
                    "DailySafetyTipWorker_Missed",
                    ExistingWorkPolicy.KEEP,
                    todayWorkRequest
                )
            }
        }
    }
}
