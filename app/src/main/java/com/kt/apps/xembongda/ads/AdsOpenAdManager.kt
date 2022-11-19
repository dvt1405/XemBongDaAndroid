package com.kt.apps.xembongda.ads

import android.content.Context
import com.kt.apps.xembongda.R
import javax.inject.Inject

class AdsOpenAdManager @Inject constructor(
    private val context: Context
) {
    val adUnit: String
    get() = context.getString(R.string.ad_mod_open_apps_id)

    fun requestAds() {

    }
}