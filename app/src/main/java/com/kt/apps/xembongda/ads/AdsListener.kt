package com.kt.apps.xembongda.ads

import androidx.core.os.bundleOf
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

open class AdsListener(val type: Type) : AdListener() {
    private val firebaseLogging by lazy {
        Firebase.analytics
    }

    enum class Type {
        INTERSTITIAL, BANNER, NATIVE, REWARD
    }

    override fun onAdClicked() {
        super.onAdClicked()
        firebaseLogging.logEvent(
            "${type.name}_DisplayFail", DEF_BUNDLE_DATA
        )
    }

    override fun onAdLoaded() {
        super.onAdLoaded()
        firebaseLogging.logEvent(
            "${type.name}_AdLoaded", DEF_BUNDLE_DATA
        )
    }

    override fun onAdImpression() {
        super.onAdImpression()
        firebaseLogging.logEvent(
            "${type.name}_AdImpression", DEF_BUNDLE_DATA
        )
    }

    override fun onAdClosed() {
        super.onAdClosed()
    }

    override fun onAdFailedToLoad(p0: LoadAdError) {
        super.onAdFailedToLoad(p0)
        firebaseLogging.logEvent(
            "${type.name}_AdFailedToLoad", DEF_BUNDLE_DATA.apply {
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
            "${type.name}_AdOpened", DEF_BUNDLE_DATA
        )
    }

    fun onAdFailedToShow(p0: AdError) {
        firebaseLogging.logEvent(
            "${type.name}_AdFailedToShow", DEF_BUNDLE_DATA.apply {
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
}