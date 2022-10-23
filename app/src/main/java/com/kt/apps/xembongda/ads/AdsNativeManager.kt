package com.kt.apps.xembongda.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.kt.apps.xembongda.R
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class AdsNativeManager @Inject constructor(context: Context) {

    private val videoOptions by lazy {
        VideoOptions.Builder()
            .setStartMuted(true)
            .build()
    }

    private val nativeOptions by lazy {
        NativeAdOptions.Builder()
            .setReturnUrlsForImageAssets(true)
            .setVideoOptions(videoOptions)
            .setRequestCustomMuteThisAd(true)
            .build()
    }

    var onLoadDone: ((ad: NativeAd) -> Unit)? = null

    private val nativeAdsListener by lazy {
        object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                isLoading.compareAndSet(false, true)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                if (!isLoading.get() && listAds.isEmpty()) {
                    preloadNativeAds()
                }
            }
        }
    }

    private val addLoader by lazy {
        AdLoader.Builder(context, context.getString(R.string.ad_mod_native_id))
            .forNativeAd {
                synchronized(listAds) {
                    Log.e("TAG", "HasVideo: ${it.mediaContent?.hasVideoContent()}")
                    listAds.add(it.apply {
                        if (it.mediaContent?.mainImage == null) {
                            Log.e("TAG", "mainImage: NUll")
                            it.mediaContent?.mainImage = it.icon?.drawable
                        }
                    })
                    this.onLoadDone?.invoke(it)
                }
            }
            .withNativeAdOptions(nativeOptions)
            .withAdListener(nativeAdsListener)
            .build()
    }

    val listAds by lazy {
        ArrayDeque<NativeAd>()
    }

    private val isLoading by lazy {
        AtomicBoolean(false)
    }

    fun preloadNativeAds(onLoadDone: ((ad: NativeAd) -> Unit)? = null) {
        this.onLoadDone = onLoadDone
        if (!addLoader.isLoading) {
            listAds.clear()
            addLoader.loadAds(
                AdRequest.Builder()
                    .build(), 10
            )
        }
    }

    fun getLastItem(): NativeAd? {
        return try {
            val lastItem = if (listAds.isNotEmpty()) {
                listAds.removeLast()
            } else {
                null
            }
            if (lastItem == null && !isLoading.get()) {
                preloadNativeAds()
            }
            lastItem
        } catch (e: Exception) {
            null
        }
    }

}