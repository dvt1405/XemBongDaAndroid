package com.kt.apps.xembongda.ads.adfalcon

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.firebase.remoteconfig.BuildConfig
import com.kt.apps.xembongda.ads.AdsListener
import com.noqoush.adfalcon.android.sdk.ADFAd
import com.noqoush.adfalcon.android.sdk.ADFInterstitial
import com.noqoush.adfalcon.android.sdk.ADFTargetingParams
import com.noqoush.adfalcon.android.sdk.constant.ADFErrorCode
import javax.inject.Inject

class AdFalconManager @Inject constructor(
    val context: Context
) {
    companion object {
        private const val APP_SITE_ID = "81c32f547fca42cd91cf9363e0a7538c"
    }

    private var adInterstitial: ADFInterstitial? = null

    fun loadInterstitial(
        activity: Context,
        maxRetry: Int = 5,
        onAdShow: () -> Unit,
        onFail: () -> Unit
    ) {
        if (maxRetry < 0) {
            onFail()
            return
        }

        adInterstitial = ADFInterstitial(
            activity,
            APP_SITE_ID,
            object : AdsListener(Type.INTERSTITIAL, Source.ADFALCON) {
                override fun onLoadAd(p0: ADFAd?) {
                    super.onLoadAd(p0)
                    if (p0 is ADFInterstitial) {
                        adInterstitial = p0
                        adInterstitial?.showInterstitialAd()
                        onAdShow()
                    }
                }

                override fun onError(p0: ADFAd?, p1: ADFErrorCode?, p2: String?) {
                    super.onError(p0, p1, p2)
                    if (p0 is ADFInterstitial) {
                        when (p1) {
                            ADFErrorCode.NONE -> {
                            }
                            ADFErrorCode.COMMUNICATION_ERROR -> {
                            }
                            ADFErrorCode.MISSING_PARAM -> {
                            }
                            else -> {
                            }
                        }
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadInterstitial(context, maxRetry - 1, onAdShow, onFail)
                    }, 2000)
                }
            },
            ADFTargetingParams().apply {
//                this.addTestDevice("15031b2f-a328-4435-be56-10ca1ad1abe1")
//                this.addTestDevice("0fee4372-a923-4626-a391-101b6811a291")
            },
            BuildConfig.DEBUG
        )
        adInterstitial?.loadInterstitialAd()
    }
}