package com.kt.apps.xembongda.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.applovin.ApplovinAdsManager
import com.kt.apps.xembongda.base.BuildConfig
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.apps.xembongda.storage.IKeyValueStorage
import javax.inject.Inject

class AdsInterstitialManager @Inject constructor(
    private val context: Context,
    private val playerManager: ExoPlayerManager,
    private val iKeyValueStorage: IKeyValueStorage,
    private val applovinAdsManager: ApplovinAdsManager
) {
    private val adUnit: String
        get() = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else context.getString(R.string.ad_mod_intertitial_id)

    private var ads: InterstitialAd? = null
    private var lastTimeShowAds: Long
        get() = iKeyValueStorage.get("last_time_interstitial", Long::class.java)
        set(value) {
            iKeyValueStorage.save("last_time_interstitial", value, Long::class.java)
        }
    private val adsListener by lazy {
        AdsListener(AdsListener.Type.INTERSTITIAL)
    }

    fun loadAds(activity: Activity, maxRetry: Int = MAX_RETRY) {
        if (maxRetry < 0) {
            loadApplovin(activity, MAX_RETRY)
        }
        if (System.currentTimeMillis() - lastTimeShowAds < 3 * 1000 * 60) return
        InterstitialAd.load(
            activity,
            adUnit,
            AdRequest.Builder()
                .build()
                ,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    ads = p0
                    ads?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            lastTimeShowAds = System.currentTimeMillis()
                            ads = null
                            adsListener.onAdLoaded()
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            adsListener.onAdClicked()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            ads = null
                            adsListener.onAdClosed()
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            adsListener.onAdImpression()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            adsListener.onAdFailedToShow(p0)
                        }
                    }
                    p0.show(activity)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    ads = null
                    loadAds(activity, maxRetry - 1)
                    adsListener.onAdFailedToLoad(p0)
                }

            }
        )
    }

    fun loadApplovin(activity: Activity, maxRetry: Int = MAX_RETRY) {
        if (maxRetry < 0) return
        applovinAdsManager.requestInterstitialAds(activity)
    }

    companion object {
        private const val MAX_RETRY = 5
    }
}