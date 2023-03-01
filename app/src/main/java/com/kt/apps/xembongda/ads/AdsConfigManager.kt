package com.kt.apps.xembongda.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.BuildConfig
import com.kt.apps.xembongda.di.AppScope
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

typealias PendingInitAds = () -> Unit

@AppScope
class AdsConfigManager @Inject constructor(private val context: Context) {
    private val initStatus by lazy {
        AtomicBoolean(false)
    }
    private val pendingStatus by lazy {
        AtomicBoolean(false)
    }
    private val pendingAction by lazy {
        ArrayDeque<PendingInitAds>()
    }

    fun init(onInitSuccess: PendingInitAds, vararg testDevice: String) {
        try {
            if (MobileAds.getInitializationStatus()
                    ?.adapterStatusMap
                    ?.get("com.google.android.gms.ads.MobileAds")
                    ?.initializationState != AdapterStatus.State.READY
            ) {
                initStatus.set(false)
                pendingStatus.set(false)
            }

        } catch (e: Exception) {
        }
        if (pendingStatus.get()) {
            pendingAction.add(onInitSuccess)
        }
        if (initStatus.get()) {
            onInitSuccess()
            return
        }

        pendingStatus.set(true)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .apply {
                    if (BuildConfig.DEBUG) {
                        this.setTestDeviceIds(
                            listOf(
                                "2E5DBFD4EC17B9BA236B112187D5BE71",
                                "4CD66BD8BFDE7D9A89909952DB95744A",
                                *testDevice
                            )
                        )
                    }
                }
                .build()
        )

        MobileAds.initialize(context) {
            initStatus.set(true)
            pendingStatus.set(false)
            synchronized(pendingAction) {
                while (pendingAction.isNotEmpty()) {
                    pendingAction.firstOrNull()?.let {
                        it.invoke()
                        try {
                            pendingAction.removeFirst()
                        } catch (e: Exception) {
                            Firebase.crashlytics.log(e.message ?: e::class.java.simpleName)
                        }
                    }
                }
            }
            onInitSuccess()
        }
    }

}