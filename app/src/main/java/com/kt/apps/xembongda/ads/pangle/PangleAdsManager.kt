package com.kt.apps.xembongda.ads.pangle

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.bytedance.sdk.openadsdk.api.init.PAGSdk.PAGInitCallback
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialRequest
import com.kt.apps.xembongda.BuildConfig
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.AdsListener
import com.kt.apps.xembongda.di.AppScope
import javax.inject.Inject


@AppScope
class PangleAdsManager @Inject constructor(
    private val context: Context
) {

    fun initSDK(initSuccess: () -> Unit) {
        if (PAGSdk.isInitSuccess()) {
            initSuccess()
            return
        }
        PAGSdk.init(context, buildNewConfig(), object : PAGInitCallback {
            override fun success() {
                initSuccess()
            }

            override fun fail(code: Int, msg: String) {
            }
        })
    }

    fun loadInterstitial(activity: Activity, maxRetry: Int = 5, onShow: () -> Unit, onFail: () -> Unit) {
        if (!PAGSdk.isInitSuccess()) {
            initSDK {
                Handler(Looper.getMainLooper()).post {
                    loadInterstitial(activity, maxRetry, onShow, onFail)
                }
            }
            return
        }
        val request = PAGInterstitialRequest()
        PAGInterstitialAd.loadAd(if (BuildConfig.DEBUG) "980088186" else "980325165",
            request,
            object : AdsListener(Type.INTERSTITIAL, Source.PANGLE) {
                override fun onError(p0: Int, p1: String?) {
                    super.onError(p0, p1)
                    onFail()
                }

                override fun onAdLoaded(p0: PAGInterstitialAd?) {
                    super.onAdLoaded(p0)
                    p0?.setAdInteractionListener(this)
                    p0?.show(activity)
                    onShow()
                }
            })
    }

    companion object {
        fun buildNewConfig(): PAGConfig {
            return PAGConfig.Builder()
                .appId(if (BuildConfig.DEBUG) "8025677" else "8093136")
                .appIcon(R.mipmap.ic_launcher)
                .debugLog(BuildConfig.DEBUG)
                .build()
        }
    }
}