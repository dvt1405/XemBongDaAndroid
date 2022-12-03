package com.kt.apps.xembongda.usecase.logging

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseLogUtils {
    private val log by lazy {
        Firebase.analytics.apply {
            setDefaultEventParameters(Bundle().apply {

            })
        }
    }

    fun logOpenAppEvent(fromDeepLink: String? = null) {
        val params = fromDeepLink?.let {
            bundleOf("deepLink" to it)
        } ?: bundleOf()
        log.logEvent("openApp", params)
    }

    fun logViewVideo(channel: String) {
        log.logEvent(
            "viewVideo", bundleOf(
                "channel" to channel
            )
        )
    }

    fun logGetListChannel(sourceFrom: String, data: Bundle = bundleOf()) {
        log.logEvent(
            "GetListChannelFrom_$sourceFrom", data
        )
    }

    fun logGetListChannelError(sourceFrom: String, e: Throwable) {
        log.logEvent(
            "Error_GetListChannelFrom_$sourceFrom",
            bundleOf(
                "reason" to (e.message ?: e::class.java.name)
            )
        )
    }

    fun logLoadOpenAds() {
        log.logEvent(
            "loadAdsSuccess", bundleOf(
                "type" to "openApp"
            )
        )
    }

    fun logLoadOpenAdsError() {
        log.logEvent("loadAdsError", bundleOf())
    }


    object BannerAds {
        fun logLoadBanner(id: String?, inFragment: Fragment) {
            log.logEvent(
                "AdLoadBanner", bundleOf(
                    "id" to id,
                    "time" to "${System.currentTimeMillis()}"
                )
            )
        }

        fun logLoadBannerFail(id: String?) {
            log.logEvent(
                "AdLoadFail", bundleOf(
                    "id" to id,
                    "time" to "${System.currentTimeMillis()}"
                )
            )
        }

        fun onAdLoaded(id: String?) {
            log.logEvent(
                "AdLoaded", bundleOf(
                    "id" to id,
                    "time" to "${System.currentTimeMillis()}"
                )
            )
        }

        fun logAdClick(id: String?) {
            log.logEvent(
                "AdClick", bundleOf(
                    "id" to id,
                    "time" to "${System.currentTimeMillis()}"
                )
            )
        }

    }
}