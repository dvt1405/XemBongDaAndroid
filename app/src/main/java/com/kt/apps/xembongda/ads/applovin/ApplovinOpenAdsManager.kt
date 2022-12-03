package com.kt.apps.xembongda.ads.applovin

import android.content.Context
import androidx.lifecycle.*
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.sdk.AppLovinSdk
import javax.inject.Inject

class ApplovinOpenAdsManager {
}

class ApplovinOpenManager @Inject constructor(
    val context: Context
) : LifecycleEventObserver, MaxAdListener {
    private var appOpenAd: MaxAppOpenAd? = null

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun loadAds() {
        appOpenAd = MaxAppOpenAd("52976aba76189592", context)
        appOpenAd?.setListener(this)
        appOpenAd?.loadAd()
    }
    private fun showAdIfReady() {
        if (!AppLovinSdk.getInstance(context).isInitialized) return

        if (appOpenAd?.isReady == true) {
            appOpenAd?.showAd("YOUR_TEST_PLACEMENT_HERE")
        } else {
            appOpenAd?.loadAd()
        }
    }

    override fun onAdLoaded(ad: MaxAd) {}
    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
    override fun onAdDisplayed(ad: MaxAd) {}
    override fun onAdClicked(ad: MaxAd) {}

    override fun onAdHidden(ad: MaxAd) {
        appOpenAd?.loadAd()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        appOpenAd?.loadAd()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            showAdIfReady()
        }
    }
}