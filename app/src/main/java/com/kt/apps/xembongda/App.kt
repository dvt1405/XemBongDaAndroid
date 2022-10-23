package com.kt.apps.xembongda

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kt.apps.xembongda.ads.AdsConfigManager
import com.kt.apps.xembongda.ads.AdsNativeManager
import com.kt.apps.xembongda.di.DaggerAppComponents
import com.kt.apps.xembongda.di.DaggerBaseComponents
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

class App : DaggerApplication() {
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
        adsLoaderManager.preloadNativeAds(null)
        app = this
        remoteConfig.fetch()
    }


    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponents.factory().create(_baseComponents, this)
    }

    companion object {
        private lateinit var app: App
        fun get() = app
    }

}