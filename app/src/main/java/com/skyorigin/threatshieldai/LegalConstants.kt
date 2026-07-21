package com.skyorigin.threatshieldai

import android.content.Context
import android.content.Intent
import android.net.Uri

object LegalConstants {
    const val URL_HOME = "https://helloskyorigin.github.io/threatshieldai-legal/"
    const val URL_PRIVACY_POLICY = "https://helloskyorigin.github.io/threatshieldai-legal/privacy.html"
    const val URL_TERMS = "https://helloskyorigin.github.io/threatshieldai-legal/terms.html"
    const val URL_CONTACT = "https://helloskyorigin.github.io/threatshieldai-legal/contact.html"

    private fun openUrlWithCustomTabs(context: Context, url: String) {
        try {
            val intent = androidx.browser.customtabs.CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            intent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun openLegalPortal(context: Context) {
        AnalyticsManager.getInstance(context).logLegalPortalOpened()
        openUrlWithCustomTabs(context, URL_HOME)
    }

    fun openPrivacyPolicy(context: Context) {
        AnalyticsManager.getInstance(context).logPrivacyPolicyOpened()
        openUrlWithCustomTabs(context, URL_PRIVACY_POLICY)
    }

    fun openTermsAndConditions(context: Context) {
        AnalyticsManager.getInstance(context).logTermsOpened()
        openUrlWithCustomTabs(context, URL_TERMS)
    }

    fun openContactSupport(context: Context) {
        AnalyticsManager.getInstance(context).logContactSupportOpened()
        openUrlWithCustomTabs(context, URL_CONTACT)
    }
}
