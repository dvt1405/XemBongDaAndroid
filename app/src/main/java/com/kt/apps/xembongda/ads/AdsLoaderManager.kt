package com.kt.apps.xembongda.ads

import android.content.Context
import com.google.android.gms.ads.AdLoader

abstract class AdsLoaderManager(private val context: Context) {
    abstract val adsUnit: String

    protected val adLoader by lazy {
        AdLoader.Builder(context, adsUnit)
            .forNativeAd {

            }
            .build()
    }
}