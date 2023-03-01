package com.kt.apps.xembongda.ads.applovin

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.os.bundleOf
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.BuildConfig
import com.kt.apps.xembongda.ads.AdsListener
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.pow

class ApplovinAdsManager @Inject constructor(
) {
    private val _initSuccess by lazy { AtomicBoolean() }
    private val firebaseLogging: FirebaseAnalytics
        get() = Firebase.analytics
    private var interstitialAds: MaxInterstitialAd? = null
    private var sdk: AppLovinSdk? = null

    init {
        INSTANCE = this
    }

    fun getInstance(activity: Activity, onInitSuccess: () -> Unit) {
        if (INSTANCE?._initSuccess?.get() == true) {
            onInitSuccess()
            return
        }
        AppLovinPrivacySettings.setIsAgeRestrictedUser(true, activity)
        sdk = AppLovinSdk.getInstance(activity)

        sdk?.mediationProvider = AppLovinMediationProvider.MAX
        sdk?.settings?.isLocationCollectionEnabled = false


        sdk?.initializeSdk {
            _initSuccess.set(true)
            sdk?.settings?.isLocationCollectionEnabled = false
            if (BuildConfig.DEBUG) {
                sdk?.settings?.testDeviceAdvertisingIds =
                    mutableListOf("15031b2f-a328-4435-be56-10ca1ad1abe1", "0fee4372-a923-4626-a391-101b6811a291")
            }
            onInitSuccess()
        }
    }

    fun createInterstitialAd(activity: Activity, maxRetry: Int, onAdLoaded: () -> Unit, onFail: () -> Unit = {}) {
        interstitialAds = MaxInterstitialAd("dab8e8a26db51517", sdk!!, activity)
        // Load the first ad
        var _maxRetry = maxRetry
        interstitialAds?.setListener(
            object : AdsListener(Type.INTERSTITIAL, Source.APP_LOVIN) {
                var retryTimes = 0
                override fun onAdLoaded(ad: MaxAd?) {
                    super.onAdLoaded(ad)
                    onAdLoaded()
                }

                override fun onAdDisplayed(ad: MaxAd?) {
                    super.onAdDisplayed(ad)
                    interstitialAds = null
                    createInterstitialAd(activity, maxRetry, {})
                }

                override fun onAdClicked(ad: MaxAd?) {
                    super.onAdClicked(ad)
                    interstitialAds = null
                    createInterstitialAd(activity, maxRetry,{})
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                    super.onAdLoadFailed(adUnitId, error)
                    retryTimes++
                    val delayMillis =
                        TimeUnit.SECONDS.toMillis(2.0.pow(6.0.coerceAtMost(retryTimes.toDouble())).toLong())
                    if (_maxRetry < 0) {
                        Handler(Looper.getMainLooper()).post { onFail() }
                        return
                    }
                    Handler(Looper.getMainLooper())
                        .postDelayed(
                            {
                                interstitialAds?.loadAd()
                                _maxRetry--
                            }, delayMillis
                        )
                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    super.onAdDisplayFailed(ad, error)
                    if (_maxRetry < 0) {
                        Handler(Looper.getMainLooper()).post { onFail() }
                        return
                    }
                    interstitialAds?.loadAd()
                    _maxRetry--
                }
            }
        )
        interstitialAds?.loadAd()
    }

    fun requestInterstitialAds(activity: Activity, maxRetry: Int = 5, onAdShow: () -> Unit, onFail: () -> Unit) {
        getInstance(activity) {
            if (interstitialAds?.isReady == true) {
                Handler(Looper.getMainLooper()).post {
                    interstitialAds?.showAd()
                    onAdShow()
                }
            } else {
                createInterstitialAd(activity, maxRetry, onFail) {
                    Handler(Looper.getMainLooper()).post {
                        interstitialAds?.showAd()
                        onAdShow()
                    }
                }
            }

        }
    }


    fun targetData(activity: Activity) {
        AppLovinSdk.getInstance(activity).targetingData.email = "user@email.com"
        AppLovinSdk.getInstance(activity).targetingData.keywords = listOf("fruit:apple", "fruit:banana", "fruit:orange")

    }

    fun createNativeAds() {
        MaxNativeAd(
            MaxNativeAd.Builder()

        )
    }

    enum class Type(val adsName: String) {
        INTERSTITIAL_ADS("InterstitialAds"),
        NATIVE_ADS("NativeAds"),
        BANNER("BANNER")

    }

    companion object {
        private const val ERROR_UNSPECIFIED = -1
        private const val ERROR_NO_FILL = 204
        private const val ERROR_AD_LOAD_FAILED = -5001
        private const val ERROR_NETWORK_ERROR = -1000
        private const val ERROR_NETWORK_TIMEOUT = -1001
        private const val ERROR_NO_NETWORK = -1001
        private const val ERROR_FULLSCREEN_AD_ALREADY_SHOWING = -23
        private const val ERROR_FULLSCREEN_AD_NOT_READY = -24
        private const val ERROR_ERROR_CODE_INVALID_LOAD_STATE = -5201
        private const val ERROR_AD_NOT_READY = -5205
        private const val ERROR_INTERNAL_ERROR = -5209
        private const val ERROR_NO_ACTIVITY = -5601
        private const val ERROR_DONT_KEEP_ACTIVITIES_ENABLED = -5602

        private val DEF_BUNDLE_DATA = bundleOf(
            "source" to "Applovin"
        )

        private var INSTANCE: ApplovinAdsManager? = null

        fun getInstance(): ApplovinAdsManager {
            return INSTANCE ?: ApplovinAdsManager()
        }

        private val mainThreadHandler by lazy {
            Handler(Looper.getMainLooper())
        }

        val isMainThread: Boolean
            get() = Looper.myLooper() == Looper.getMainLooper()

    }
}