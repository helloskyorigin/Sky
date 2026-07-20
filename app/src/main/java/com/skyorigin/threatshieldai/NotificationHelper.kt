package com.skyorigin.threatshieldai

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "scam_shield_notifications"
    private const val CHANNEL_NAME = "ThreatShield Alerts"
    private const val CHANNEL_DESC = "Notifications for scanned messages and daily challenges"

    private const val DAILY_CHALLENGE_CHANNEL_ID = "daily_security_challenge"
    private const val DAILY_CHALLENGE_CHANNEL_NAME = "Daily Security Challenge"
    private const val DAILY_CHALLENGE_CHANNEL_DESC = "Daily reminders to test your cyber safety skills"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val quizChannel = NotificationChannel(DAILY_CHALLENGE_CHANNEL_ID, DAILY_CHALLENGE_CHANNEL_NAME, importance).apply {
                description = DAILY_CHALLENGE_CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(quizChannel)
        }
    }

    fun scheduleDailyChallengeNotification(context: Context) {
        DailyChallengeWorker.schedule(context)
    }

    fun showDailyChallengeNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "daily_challenge")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 2001, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val titles = listOf(
            "🛡️ Today's Cyber Safety Challenge",
            "🛡️ Daily Scam Challenge Ready",
            "🛡️ Test Your Threat Radar",
            "🛡️ New Security Challenge"
        )
        val bodies = listOf(
            "Can you identify today's scam in under 30 seconds? Tap to test yourself and improve your cyber safety.",
            "A new scam message is waiting for your analysis. Tap to see if you can spot it.",
            "Stay sharp! Tap to complete today's quick cyber security challenge.",
            "Boost your threat awareness score by solving today's challenge."
        )

        val builder = NotificationCompat.Builder(context, DAILY_CHALLENGE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield_notif)
            .setLargeIcon(BitmapHelper.getBitmapFromVectorDrawable(context, R.drawable.ic_official_logo))
            .setContentTitle(titles.random())
            .setContentText(bodies.random())
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodies.random()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(1001, builder.build())
            } catch (e: SecurityException) {
                // Ignore missing notification permission in tests or pre-OREO
            }
        }
    }

    fun showScanCompleteNotification(context: Context, analysis: MessageAnalysis) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "result")
            putExtra("timestamp", analysis.timestamp)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, analysis.timestamp.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield_notif)
            .setLargeIcon(BitmapHelper.getBitmapFromVectorDrawable(context, R.drawable.ic_official_logo))
            .setContentTitle("Analysis Completed")
            .setContentText("Status: ${analysis.status} (Score: ${analysis.score}%)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(1002, builder.build())
            } catch (e: SecurityException) {
                // Ignore missing notification permission in tests or pre-OREO
            }
        }
    }
}
