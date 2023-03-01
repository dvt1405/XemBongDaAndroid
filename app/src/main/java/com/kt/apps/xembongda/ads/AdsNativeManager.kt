package com.kt.apps.xembongda.ads

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BuildConfig
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class AdsNativeManager @Inject constructor(context: Context) {

    @FunctionalInterface
    interface Observer {
        fun onReceiveAds(nativeAd: NativeAd)
    }

    private val compositeDisposable by lazy { CompositeDisposable() }
    private val videoOptions by lazy {
        VideoOptions.Builder()
            .setStartMuted(true)
            .build()
    }

    private val nativeOptions by lazy {
        NativeAdOptions.Builder()
            .setReturnUrlsForImageAssets(true)
            .setRequestMultipleImages(true)
            .setVideoOptions(videoOptions)
            .build()
    }


    private val nativeAdsListener by lazy {
        var retryCount: Int = 5
        object : AdsListener(Type.NATIVE) {
            override fun onAdLoaded() {
                super.onAdLoaded()
                isLoading.compareAndSet(false, true)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                if (!isLoading.get() && listAds.isEmpty()) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        preloadNativeAds()
                    }, 5000)
                }
            }
        }
    }

    private val nativeAdListener by lazy {
        NativeAd.OnNativeAdLoadedListener {
            synchronized(listAds) {
                listAds.add(it)
            }
            emitter?.onNext(it)
        }
    }

    private val adUnits by lazy {
        if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/2247696110"
        } else {
            context.getString(R.string.ad_mod_native_id)
        }
    }


    private val addLoader by lazy {
        AdLoader.Builder(context, adUnits)
            .forNativeAd(nativeAdListener)
            .withNativeAdOptions(nativeOptions)
            .withAdListener(nativeAdsListener)
            .build()
    }


    val listAds by lazy {
        ConcurrentLinkedQueue<NativeAd>()
    }

    private val isLoading by lazy {
        AtomicBoolean(false)
    }

    private val adRequest by lazy {
        AdRequest.Builder()
            .build()

    }

    private var emitter: ObservableEmitter<NativeAd>? = null
    fun preloadNativeAds(retry: Int = 5) {
        if (addLoader.isLoading) return
        compositeDisposable.add(
            Observable.create {
                emitter = it
                if (!addLoader.isLoading) {
                    addLoader.loadAds(adRequest, 10)
                }
            }
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (concurrentLinkedQueue.isEmpty()) return@subscribe
                    concurrentLinkedQueue.last()?.onReceiveAds(it)
                }, { t ->

                }, {

                })
        )

    }

    fun getLastItem(): NativeAd? {
        return try {
            val lastItem = if (listAds.isNotEmpty()) {
                val last = listAds.last()
                listAds.remove(listAds.last())
                last
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


    private val concurrentLinkedQueue by lazy {
        ConcurrentLinkedQueue<Observer>()
    }

    fun addObserver(observer: Observer) {
        concurrentLinkedQueue.add(observer)
    }

    fun unregister(observer: Observer) {
        concurrentLinkedQueue.remove(observer)
    }

}