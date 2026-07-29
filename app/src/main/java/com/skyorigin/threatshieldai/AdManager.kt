package com.skyorigin.threatshieldai

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.atomic.AtomicBoolean

object AdManager {
    private const val TAG = "AdManager"
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    fun initialize(context: Context) {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        try {
            MobileAds.initialize(context) { initializationStatus ->
                Log.d(TAG, "AdMob Initialized: $initializationStatus")
                try {
                    RewardedAdManager.loadRewardedAd(context)
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to load rewarded ad in initializer", e)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to initialize MobileAds", e)
        }
    }
}

