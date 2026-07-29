package com.skyorigin.threatshieldai

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

class ConsentManager(context: Context) {
    private var consentInformation: ConsentInformation? = null

    init {
        try {
            consentInformation = UserMessagingPlatform.getConsentInformation(context)
        } catch (e: Throwable) {
            Log.e("ConsentManager", "Failed to initialize ConsentInformation", e)
        }
    }

    val canRequestAds: Boolean
        get() = try {
            consentInformation?.canRequestAds() ?: false
        } catch (e: Throwable) {
            Log.e("ConsentManager", "Failed to check canRequestAds", e)
            false
        }

    val isPrivacyOptionsRequired: Boolean
        get() = try {
            consentInformation?.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        } catch (e: Throwable) {
            Log.e("ConsentManager", "Failed to check isPrivacyOptionsRequired", e)
            false
        }

    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: (FormError?) -> Unit
    ) {
        val info = consentInformation
        if (info == null) {
            onConsentGatheringCompleteListener(null)
            return
        }

        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        try {
            info.requestConsentInfoUpdate(
                activity,
                params,
                {
                    try {
                        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                            onConsentGatheringCompleteListener(formError)
                        }
                    } catch (e: Throwable) {
                        Log.e("ConsentManager", "Failed to load/show consent form", e)
                        onConsentGatheringCompleteListener(null)
                    }
                },
                { requestConsentError ->
                    Log.e("ConsentManager", "Consent info update failed: ${requestConsentError.errorCode} - ${requestConsentError.message}")
                    onConsentGatheringCompleteListener(requestConsentError)
                }
            )
        } catch (e: Throwable) {
            Log.e("ConsentManager", "Error requesting consent info update", e)
            onConsentGatheringCompleteListener(null)
        }
    }

    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: (FormError?) -> Unit
    ) {
        try {
            UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
        } catch (e: Throwable) {
            Log.e("ConsentManager", "Error showing privacy options form", e)
            onConsentFormDismissedListener(null)
        }
    }
}

