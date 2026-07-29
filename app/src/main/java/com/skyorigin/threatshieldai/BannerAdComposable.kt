package com.skyorigin.threatshieldai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import android.util.DisplayMetrics

@Composable
fun BannerAdComposable(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    
    // Screen width minus 32dp for padding (16dp each side)
    val adWidth = maxOf(300, configuration.screenWidthDp - 32)
    val adWidthPixels = (adWidth * context.resources.displayMetrics.density).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sponsored",
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
        )
        
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { factoryContext ->
                AdView(factoryContext).apply {
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(factoryContext, adWidth))
                    adUnitId = AdConstants.BANNER_AD_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = {
                // No update needed on recomposition
            },
            onRelease = { adView ->
                adView.destroy()
            }
        )
    }
}
