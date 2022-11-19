package com.kt.apps.xembongda.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BuildConfig
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.apps.xembongda.storage.IKeyValueStorage
import javax.inject.Inject

class AdsInterstitialManager @Inject constructor(
    private val context: Context,
    private val playerManager: ExoPlayerManager,
    private val iKeyValueStorage: IKeyValueStorage
) {
    private val adUnit: String
        get() = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else context.getString(R.string.ad_mod_intertitial_id)

    private var ads: InterstitialAd? = null
    private var lastTimeShowAds: Long
        get() = iKeyValueStorage.get("last_time_interstitial", Long::class.java)
        set(value) {
            iKeyValueStorage.save("last_time_interstitial", value, Long::class.java)
        }

    fun loadAds(activity: Activity) {
        if (System.currentTimeMillis() - lastTimeShowAds < 3 * 1000 * 60) return
        InterstitialAd.load(
            activity,
            adUnit,
            AdRequest.Builder()
                .build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    ads = p0
                    ads?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            lastTimeShowAds = System.currentTimeMillis()
                            ads = null
                        }

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            ads = null
                        }
                    }
                    p0.show(activity)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    ads = null
                }

            }
        )
    }
}