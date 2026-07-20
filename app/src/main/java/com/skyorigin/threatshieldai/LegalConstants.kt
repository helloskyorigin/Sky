package com.skyorigin.threatshieldai

import android.content.Context
import android.content.Intent
import android.net.Uri

object LegalConstants {
    const val LEGAL_BASE_URL = "https://helloskyorigin.github.io/threatshield-ai-legal/"

    fun openLegalPortal(context: Context) {
        AnalyticsManager.getInstance(context).logLegalPortalOpened()
        try {
            val intent = androidx.browser.customtabs.CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            intent.launchUrl(context, Uri.parse(LEGAL_BASE_URL))
        } catch (e: Exception) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(LEGAL_BASE_URL)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun openContactEmail(context: Context) {
        AnalyticsManager.getInstance(context).logContactSupportOpened()
        val appVersion = try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName
        } catch (e: Exception) {
            "1.0.0"
        }
        val androidVersion = android.os.Build.VERSION.RELEASE
        val deviceModel = android.os.Build.MODEL
        val bodyText = """
            Hello ThreatShield AI Team,

            App Version: ${appVersion}
            Android Version: ${androidVersion}
            Device Model: ${deviceModel}

            Please describe your issue below:
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:threatshieldai@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "ThreatShield AI Support Request")
            putExtra(Intent.EXTRA_TEXT, bodyText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
