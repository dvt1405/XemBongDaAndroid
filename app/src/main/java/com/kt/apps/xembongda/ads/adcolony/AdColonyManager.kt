package com.kt.apps.xembongda.ads.adcolony

import android.app.Application
import android.content.Context
import com.adcolony.sdk.*
import com.applovin.mediation.AppLovinUtils.ServerParameterKeys.ZONE_ID
import javax.inject.Inject


class AdColonyManager @Inject constructor(
    private val context: Context
) {
    private var ad: AdColonyInterstitial? = null

    fun init() {

    }

    fun requestInterstitial(adUnit: String = ZONE_ID_INTERSTITIAL) {
        if (ad != null && !ad!!.isExpired) {
            ad?.show()
            return
        }
        val appOptions = AdColonyAppOptions()
            .setKeepScreenOn(true)

        // Configure AdColony in your launching Activity's onCreate() method so that cached ads can
        // be available as soon as possible.

        // Configure AdColony in your launching Activity's onCreate() method so that cached ads can
        // be available as soon as possible.
        AdColony.configure(context.applicationContext as Application, appOptions, APP_ID)

        // Ad specific options to be sent with request

        // Ad specific options to be sent with request
        val adOptions = AdColonyAdOptions()
        val listener = object : AdColonyInterstitialListener() {
            override fun onRequestFilled(ad: AdColonyInterstitial) {
                this@AdColonyManager.ad = ad
            }

            override fun onRequestNotFilled(zone: AdColonyZone) {
                // Ad request was not filled
            }

            override fun onOpened(ad: AdColonyInterstitial) {
                // Ad opened, reset UI to reflect state change
            }

            override fun onExpiring(ad: AdColonyInterstitial) {
                AdColony.requestInterstitial(ZONE_ID, this, adOptions)
            }
        }
        AdColony.requestInterstitial(ZONE_ID_INTERSTITIAL, listener, adOptions)
    }

    companion object {
        private const val APP_ID = "app44b9d935b7884d49b3"
        private const val ZONE_ID_INTERSTITIAL = "vza7f4c6eacbf44db9a9"
    }
}