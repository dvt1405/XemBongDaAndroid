package com.kt.apps.xembongda.ui.highlight

import com.google.android.gms.ads.nativead.NativeAd
import com.kt.apps.xembongda.model.highlights.HighLightDTO

sealed class ItemHighLights {
    class Ads(var ad: NativeAd? = null) : ItemHighLights()
    class DTO(val dto: HighLightDTO) : ItemHighLights()
}