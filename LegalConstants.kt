package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri

object LegalConstants {
    const val PRIVACY_POLICY_URL = "https://helloskyorigin.github.io/ThreatShield-AI/privacy.html"
    const val TERMS_OF_USE_URL = "https://helloskyorigin.github.io/ThreatShield-AI/terms.html"
    const val DISCLAIMER_URL = "https://helloskyorigin.github.io/ThreatShield-AI/disclaimer.html"
    const val CONTACT_URL = "https://helloskyorigin.github.io/ThreatShield-AI/contact.html"

    fun openContactEmail(context: Context) {
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
