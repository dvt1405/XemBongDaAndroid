package com.kt.apps.xembongda

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kt.apps.xembongda.ads.AdsConfigManager
import com.kt.apps.xembongda.ads.AdsNativeManager
import com.kt.apps.xembongda.ads.RewardedAdsManager
import com.kt.apps.xembongda.di.DaggerAppComponents
import com.kt.apps.xembongda.di.DaggerBaseComponents
import com.kt.apps.xembongda.receiver.VolumeChangeReceiver
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

class App : DaggerApplication(), ActivityLifecycleCallbacks {
    private val _baseComponents by lazy {
        DaggerBaseComponents.builder()
            .application(this)
            .build()
    }

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    @Inject
    lateinit var adsLoaderManager: AdsNativeManager

    @Inject
    lateinit var mobileAdsConfig: AdsConfigManager

    @Inject
    lateinit var rewardedAdsManager: RewardedAdsManager


    private val showAds: Boolean by lazy {
        if (BuildConfig.DEBUG) true
        else remoteConfig.getBoolean("ShowAds")
    }

    val showBanner: Boolean by lazy {
        when {
            BuildConfig.DEBUG -> true
            !showAds -> false
            else -> remoteConfig.getBoolean("ShowBanner")
        }
    }


    val showNative: Boolean by lazy {
        when {
            BuildConfig.DEBUG -> true
            !showAds -> false
            else -> remoteConfig.getBoolean("ShowNative")
        }
    }

    override fun onCreate() {
        super.onCreate()
        mobileAdsConfig.init()
        adsLoaderManager.preloadNativeAds()
        app = this
        remoteConfig.fetchAndActivate()
        remoteConfig.fetch(60 * 1000)
            .addOnSuccessListener {
                remoteConfig.activate()
            }
//        rewardedAdsManager.preLoadAds()
    }


    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponents.factory().create(_baseComponents, this)
    }

    companion object {
        private lateinit var app: App
        fun get() = app
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        activity.registerReceiver(VolumeChangeReceiver.getInstance(), IntentFilter().apply {
            addAction("android.media.VOLUME_CHANGED_ACTION")
            addAction(Intent.ACTION_MEDIA_BUTTON)
        })
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        unregisterReceiver(VolumeChangeReceiver.getInstance())
    }

}