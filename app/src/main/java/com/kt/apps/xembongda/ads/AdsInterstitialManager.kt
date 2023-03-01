package com.kt.apps.xembongda.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.kt.apps.xembongda.BuildConfig
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.adfalcon.AdFalconManager
import com.kt.apps.xembongda.ads.applovin.ApplovinAdsManager
import com.kt.apps.xembongda.ads.pangle.PangleAdsManager
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.apps.xembongda.storage.IKeyValueStorage
import javax.inject.Inject

class AdsInterstitialManager @Inject constructor(
    private val context: Context,
    private val playerManager: ExoPlayerManager,
    private val iKeyValueStorage: IKeyValueStorage,
    private val applovinAdsManager: ApplovinAdsManager,
    private val adFalconManager: AdFalconManager,
    private val adsConfigManager: AdsConfigManager,
    private val pangleAdsManager: PangleAdsManager
) {
    private val adUnit: String by lazy {
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else context.getString(R.string.ad_mod_intertitial_id)
    }

    private var ads: InterstitialAd? = null
    private var lastTimeShowAds: Long
        get() = iKeyValueStorage.get("last_time_interstitial", Long::class.java)
        set(value) {
            iKeyValueStorage.save("last_time_interstitial", value, Long::class.java)
        }
    private val adsListener by lazy {
        AdsListener(AdsListener.Type.INTERSTITIAL)
    }
    private val loadPangleFirst: Boolean by lazy {
        true
    }

    fun loadAds(activity: Activity, maxRetry: Int = MAX_RETRY) {
        if (maxRetry < 0) {
            val onAdShow = {
                lastTimeShowAds = System.currentTimeMillis()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                if (loadPangleFirst) {
                    loadPangle(activity, MAX_RETRY, onAdShow) {
                        loadApplovin(activity, MAX_RETRY, onAdShow = onAdShow) {
                            loadAdFalcon(activity, onAdShow = onAdShow) {
                            }
                        }
                    }
                } else {
                    loadApplovin(activity, MAX_RETRY, onAdShow) {
                        loadPangle(activity, MAX_RETRY, onAdShow = onAdShow) {
                            loadAdFalcon(activity, onAdShow = onAdShow) {
                            }
                        }
                    }

                }
            }, 2000)

        }
        if (System.currentTimeMillis() - lastTimeShowAds < 3 * 1000 * 60) return
        adsConfigManager.init({
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
                        Handler(Looper.getMainLooper()).postDelayed({
                            loadAds(activity, maxRetry - 1)
                        }, 2000)
                        adsListener.onAdFailedToLoad(p0)
                    }

                }
            )
        })
    }

    private fun loadApplovin(
        activity: Activity, maxRetry: Int = MAX_RETRY,
        onAdShow: () -> Unit = {},
        onFail: () -> Unit = {}
    ) {
        if (maxRetry < 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                onFail()
            }, 2000)
            return
        }
        applovinAdsManager.requestInterstitialAds(activity, maxRetry = maxRetry, onAdShow = onAdShow, onFail)
    }

    private fun loadAdFalcon(
        activity: Activity,
        maxRetry: Int = MAX_RETRY,
        onAdShow: () -> Unit = {},
        onFail: () -> Unit = {}
    ) {
        if (maxRetry < 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                onFail()
            }, 2000)
            return
        }
        adFalconManager.loadInterstitial(activity, 5, onAdShow = onAdShow, onFail = onFail)
    }

    private fun loadPangle(
        activity: Activity,
        maxRetry: Int = MAX_RETRY,
        onAdShow: () -> Unit = {},
        onFail: () -> Unit = {}
    ) {
        if (maxRetry < 0) {
            onFail()
            return
        }
        pangleAdsManager.loadInterstitial(activity, maxRetry, onAdShow) {
            Handler(Looper.getMainLooper()).postDelayed({
                loadPangle(activity, maxRetry - 1, onAdShow, onFail)
            }, 2000)
        }
    }

    companion object {
        private const val MAX_RETRY = 5
    }
}