package com.kt.apps.xembongda.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import javax.inject.Inject

class AdsConfigManager @Inject constructor(private val context: Context) {

    fun init(vararg testDevice: String) {
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(
                    listOf(
                        "2E5DBFD4EC17B9BA236B112187D5BE71",
                        "4CD66BD8BFDE7D9A89909952DB95744A",
                        *testDevice
                    )
                )
                .build()
        )

        MobileAds.initialize(context) {

        }
    }

}