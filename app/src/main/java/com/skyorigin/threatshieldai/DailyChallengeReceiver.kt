package com.skyorigin.threatshieldai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DailyChallengeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationHelper.scheduleDailyChallengeNotification(context)
        }
    }
}
