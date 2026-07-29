package com.skyorigin.threatshieldai

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object RewardedAdManager {
    private var rewardedAd: RewardedAd? = null
    private const val TAG = "RewardedAdManager"

    fun loadRewardedAd(context: Context) {
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                AdConstants.REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, "Rewarded ad failed to load: ${adError.message}")
                        rewardedAd = null
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(TAG, "Rewarded ad loaded successfully.")
                        rewardedAd = ad
                    }
                }
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Error in loadRewardedAd", e)
            rewardedAd = null
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdDismissed: (Boolean) -> Unit,
        onAdFailedToShow: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad != null) {
            var rewardEarned = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Rewarded ad dismissed.")
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdDismissed(rewardEarned)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d(TAG, "Rewarded ad failed to show: ${adError.message}")
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdFailedToShow()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Rewarded ad showed.")
                }
            }

            var rewardGranted = false
            ad.show(activity) { _ ->
                if (!rewardGranted) {
                    rewardGranted = true
                    Log.d(TAG, "User earned the reward.")
                    rewardEarned = true
                    onRewardEarned()
                }
            }
        } else {
            Log.d(TAG, "Rewarded ad was not ready.")
            onAdFailedToShow()
            loadRewardedAd(activity)
        }
    }
}
