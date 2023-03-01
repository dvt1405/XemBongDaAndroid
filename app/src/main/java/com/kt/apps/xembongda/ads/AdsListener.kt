package com.kt.apps.xembongda.ads

import androidx.core.os.bundleOf
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdInteractionListener
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdLoadListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.noqoush.adfalcon.android.sdk.ADFAd
import com.noqoush.adfalcon.android.sdk.ADFListener
import com.noqoush.adfalcon.android.sdk.constant.ADFErrorCode

open class AdsListener(
    val type: Type,
    val source: Source = Source.ADMOB
) : AdListener(), ADFListener, MaxAdViewAdListener, PAGInterstitialAdLoadListener, PAGInterstitialAdInteractionListener {
    private val firebaseLogging by lazy {
        Firebase.analytics
    }

    enum class Type {
        INTERSTITIAL, BANNER, NATIVE, REWARD
    }

    enum class Source {
        ADMOB, APP_LOVIN, ADCOLONY, ADFALCON, PANGLE
    }

    private val sourceName by lazy {
        if (source == Source.ADMOB) "" else source.name
    }


    override fun onAdClicked() {
        super.onAdClicked()
        firebaseLogging.logEvent(
            "${type.name}${sourceName}_AdClicked", DEF_BUNDLE_DATA
        )
    }


    override fun onAdLoaded() {
        super.onAdLoaded()
        firebaseLogging.logEvent(
            "${type.name}${sourceName}_AdLoaded", DEF_BUNDLE_DATA
        )
    }

    override fun onAdImpression() {
        super.onAdImpression()
        firebaseLogging.logEvent(
            "${type.name}${sourceName}_AdImpression", DEF_BUNDLE_DATA
        )
    }

    override fun onAdClosed() {
        super.onAdClosed()
    }

    override fun onAdFailedToLoad(p0: LoadAdError) {
        super.onAdFailedToLoad(p0)
        firebaseLogging.logEvent(
            "${type.name}${sourceName}_AdFailedToLoad", DEF_BUNDLE_DATA.apply {
                p0.responseInfo?.let { res ->
                    res.mediationAdapterClassName?.let {
                        this.putString("source", it)
                    }
                    this.putAll(res.responseExtras)
                }
                this.putInt("errorCode", p0.code)
                this.putString("errorMessage", p0.message)
                this.putString("errorDomain", p0.domain)

            }
        )
    }

    override fun onAdOpened() {
        super.onAdOpened()
        firebaseLogging.logEvent(
            "${type.name}${sourceName}_AdOpened", DEF_BUNDLE_DATA
        )
    }

    fun onAdFailedToShow(p0: AdError) {
        firebaseLogging.logEvent(
            "${type.name}${sourceName}_AdFailedToShow", DEF_BUNDLE_DATA.apply {
                this.putInt("errorCode", p0.code)
                this.putString("errorMessage", p0.message)
                this.putString("errorDomain", p0.domain)
            }
        )
    }

    companion object {
        private val DEF_BUNDLE_DATA = bundleOf(
            "source" to "ADMOB"
        )
    }

    override fun onLoadAd(p0: ADFAd?) {

    }

    override fun onError(p0: ADFAd?, p1: ADFErrorCode?, p2: String?) {
        onAdFailedToLoad(
            LoadAdError(
                -1,
                "AdFalcon fail to load ad",
                "AdFalcon",
                null,
                null
            )
        )
    }

    override fun onPresentAdScreen(p0: ADFAd?) {
        onAdLoaded()
    }

    override fun onDismissAdScreen(p0: ADFAd?) {
        onAdClosed()
    }

    override fun onLeaveApplication() {

    }

    override fun onAdLoaded(ad: MaxAd?) {
        onAdLoaded()
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        onAdImpression()
    }

    override fun onAdHidden(ad: MaxAd?) {

    }

    override fun onAdClicked(ad: MaxAd?) {
        onAdClicked()
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        onAdFailedToLoad(
            LoadAdError(
                error?.code ?: -3,
                error?.message ?: "Applovin",
                adUnitId ?: "Applovin",
                AdError(
                    error?.code ?: -3,
                    error?.message ?: "Applovin",
                    adUnitId ?: error?.message ?: "Applovin"
                ),
                null
            )

        )
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        onAdFailedToShow(
            AdError(
                -2,
                "ApplovinFailToShow",
                ad?.dspName ?: error?.message ?: "Applovin"
            )
        )
    }

    override fun onAdExpanded(ad: MaxAd?) {
    }

    override fun onAdCollapsed(ad: MaxAd?) {
    }

    override fun onError(p0: Int, p1: String?) {
        onAdFailedToLoad(
            LoadAdError(p0, p1 ?: "PangleNoMsg", "Pangle", null, null)
        )
    }

    override fun onAdLoaded(p0: PAGInterstitialAd?) {
        onAdLoaded()
    }

    override fun onAdShowed() {
        onAdImpression()
    }


    override fun onAdDismissed() {
        onAdClosed()
    }

}